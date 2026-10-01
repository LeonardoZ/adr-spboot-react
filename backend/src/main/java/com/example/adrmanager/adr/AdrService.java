package com.example.adrmanager.adr;

import com.example.adrmanager.adl.Adl;
import com.example.adrmanager.adl.AdlRepository;
import com.example.adrmanager.api.ConflictException;
import com.example.adrmanager.api.ResourceNotFoundException;
import com.example.adrmanager.audit.AuditActor;
import com.example.adrmanager.audit.AuditEntityType;
import com.example.adrmanager.audit.AuditService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdrService {

	private final AdrRepository adrs;

	private final AdlRepository adls;

	private final AuditService audit;

	public AdrService(AdrRepository adrs, AdlRepository adls, AuditService audit) {
		this.adrs = adrs;
		this.adls = adls;
		this.audit = audit;
	}

	@Transactional
	public AdrResponse create(String adlIdentifier, AdrCreateRequest request, AuditActor actor) {
		Adl adl = adls.findByBusinessIdentifier(adlIdentifier)
			.orElseThrow(() -> new ResourceNotFoundException("ADL " + adlIdentifier + " was not found"));
		if (adl.isArchived())
			throw new ConflictException("Archived ADLs cannot receive ADRs");
		Adr adr = new Adr("ADR-" + UUID.randomUUID(), adl, request.title().trim(), request.context().trim(),
				request.problem().trim(), actor.userId(), actor.displayName(), Instant.now());
		adr.setDecisionContent(blankToNull(request.optionsConsidered()), blankToNull(request.decision()),
				blankToNull(request.consequences()));
		adrs.save(adr);
		audit.record(AuditEntityType.ADR, adr.getBusinessIdentifier(), "CREATED", actor, null,
				Map.of("status", "DRAFT", "adlIdentifier", adlIdentifier));
		return AdrResponse.from(adr);
	}

	@Transactional(readOnly = true)
	public List<AdrResponse> list(String adlIdentifier) {
		if (!adls.findByBusinessIdentifier(adlIdentifier).isPresent())
			throw new ResourceNotFoundException("ADL " + adlIdentifier + " was not found");
		return adrs.findByAdlBusinessIdentifierOrderByCreatedAtAsc(adlIdentifier)
			.stream()
			.map(AdrResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public AdrResponse detail(String identifier) {
		return adrs.findByBusinessIdentifier(identifier)
			.map(AdrResponse::from)
			.orElseThrow(() -> new ResourceNotFoundException("ADR " + identifier + " was not found"));
	}

	@Transactional
	public AdrResponse update(String identifier, AdrUpdateRequest request, AuditActor actor) {
		Adr adr = adrs.findByBusinessIdentifier(identifier)
			.orElseThrow(() -> new ResourceNotFoundException("ADR " + identifier + " was not found"));
		if (adr.getVersion() != request.version())
			throw new ConflictException("ADR version is stale");
		if (adr.getStatus() != AdrStatus.DRAFT)
			throw new ConflictException("Only DRAFT ADRs can be edited");
		Map<String, Object> before = Map.of("title", adr.getTitle(), "context", adr.getContext(), "problem",
				adr.getProblem());
		adr.update(request.title().trim(), request.context().trim(), request.problem().trim(),
				blankToNull(request.optionsConsidered()), blankToNull(request.decision()),
				blankToNull(request.consequences()), actor.userId(), actor.displayName(), Instant.now());
		adrs.saveAndFlush(adr);
		audit.record(AuditEntityType.ADR, identifier, "UPDATED", actor, before,
				Map.of("title", adr.getTitle(), "context", adr.getContext(), "problem", adr.getProblem()));
		return AdrResponse.from(adr);
	}

	@Transactional
	public AdrResponse submit(String identifier, AdrActionRequest request, AuditActor actor) {
		Adr adr = mutableActionAdr(identifier, request.version(), actor, AdrStatus.DRAFT);
		if (adr.getAdl().isArchived())
			throw new ConflictException("Archived ADLs cannot submit ADRs for review");
		if (isBlank(adr.getTitle()) || isBlank(adr.getContext()) || isBlank(adr.getProblem())
				|| isBlank(adr.getOptionsConsidered()) || isBlank(adr.getDecision())
				|| isBlank(adr.getConsequences())) {
			throw new ConflictException("All ADR content fields are required before review submission");
		}
		Instant now = Instant.now();
		adr.submit(actor.userId(), now);
		adrs.saveAndFlush(adr);
		audit.record(AuditEntityType.ADR, identifier, "SUBMITTED", actor, Map.of("status", AdrStatus.DRAFT.name()),
				Map.of("status", AdrStatus.UNDER_REVIEW.name()));
		return AdrResponse.from(adr);
	}

	@Transactional
	public AdrResponse cancelReview(String identifier, AdrActionRequest request, AuditActor actor) {
		Adr adr = mutableActionAdr(identifier, request.version(), actor, AdrStatus.UNDER_REVIEW);
		if (adr.getAdl().isArchived())
			throw new ConflictException("Archived ADLs cannot change ADR review state");
		adr.cancelReview(Instant.now());
		adrs.saveAndFlush(adr);
		audit.record(AuditEntityType.ADR, identifier, "REVIEW_CANCELLED", actor,
				Map.of("status", AdrStatus.UNDER_REVIEW.name()), Map.of("status", AdrStatus.DRAFT.name()));
		return AdrResponse.from(adr);
	}

	@Transactional
	public AdrResponse approve(String identifier, AdrDecisionRequest request, AuditActor actor) {
		Adr adr = decisionAdr(identifier, request.version());
		Instant now = Instant.now();
		adr.approve(actor.userId(), actor.displayName(), blankToNull(request.comment()), now);
		adrs.saveAndFlush(adr);
		audit.record(AuditEntityType.ADR, identifier, "APPROVED", actor,
				Map.of("status", AdrStatus.UNDER_REVIEW.name()), Map.of("status", AdrStatus.APPROVED.name()));
		return AdrResponse.from(adr);
	}

	@Transactional
	public AdrResponse reject(String identifier, AdrDecisionRequest request, AuditActor actor) {
		Adr adr = decisionAdr(identifier, request.version());
		if (isBlank(request.rejectionJustification()))
			throw new IllegalArgumentException("rejectionJustification is required");
		Instant now = Instant.now();
		adr.reject(actor.userId(), actor.displayName(), blankToNull(request.comment()),
				request.rejectionJustification().trim(), now);
		adrs.saveAndFlush(adr);
		audit.record(AuditEntityType.ADR, identifier, "REJECTED", actor,
				Map.of("status", AdrStatus.UNDER_REVIEW.name()),
				Map.of("status", AdrStatus.REJECTED.name(), "justification", request.rejectionJustification().trim()));
		return AdrResponse.from(adr);
	}

	private Adr decisionAdr(String identifier, Long version) {
		Adr adr = adrs.findByBusinessIdentifier(identifier)
			.orElseThrow(() -> new ResourceNotFoundException("ADR " + identifier + " was not found"));
		if (adr.getVersion() != version)
			throw new ConflictException("ADR version is stale");
		if (adr.getStatus() != AdrStatus.UNDER_REVIEW)
			throw new ConflictException("Only UNDER_REVIEW ADRs can be decided");
		if (adr.getAdl().isArchived())
			throw new ConflictException("Archived ADLs cannot change ADR review state");
		return adr;
	}

	private Adr mutableActionAdr(String identifier, Long version, AuditActor actor, AdrStatus requiredStatus) {
		Adr adr = adrs.findByBusinessIdentifier(identifier)
			.orElseThrow(() -> new ResourceNotFoundException("ADR " + identifier + " was not found"));
		if (adr.getVersion() != version)
			throw new ConflictException("ADR version is stale");
		if (adr.getStatus() != requiredStatus)
			throw new ConflictException("ADR must be " + requiredStatus + " for this action");
		if (!adr.getAuthorUserId().equals(actor.userId()))
			throw new ConflictException("Only the ADR author can change review state");
		return adr;
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	private String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}

}
