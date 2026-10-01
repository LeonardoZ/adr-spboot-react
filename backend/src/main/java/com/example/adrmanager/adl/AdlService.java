package com.example.adrmanager.adl;

import com.example.adrmanager.api.ResourceNotFoundException;
import com.example.adrmanager.api.ConflictException;
import com.example.adrmanager.audit.AuditActor;
import com.example.adrmanager.audit.AuditEntityType;
import com.example.adrmanager.audit.AuditService;
import com.example.adrmanager.adr.AdrRepository;
import com.example.adrmanager.adr.AdrStatus;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdlService {

	private final AdlRepository repository;

	private final AuditService audit;

	private final AdrRepository adrs;

	public AdlService(AdlRepository repository, AuditService audit, AdrRepository adrs) {
		this.repository = repository;
		this.audit = audit;
		this.adrs = adrs;
	}

	@Transactional
	public AdlResponse create(AdlCreateRequest request, AuditActor actor) {
		Adl adl = new Adl("ADL-" + UUID.randomUUID(), request.title().trim(), request.context().trim(),
				request.problem().trim(), actor.userId(), actor.displayName(), Instant.now());
		adl.setTags(normalizeTags(request.tags()));
		repository.save(adl);
		audit.record(AuditEntityType.ADL, adl.getBusinessIdentifier(), "CREATED", actor, null,
				Map.of("title", adl.getTitle()));
		return AdlResponse.from(adl);
	}

	@Transactional(readOnly = true)
	public AdlPageResponse search(AdlSearchCriteria criteria, int page, int size, String sort) {
		if (page < 0 || size < 1 || size > 100)
			throw new IllegalArgumentException("page must be non-negative and size must be between 1 and 100");
		Sort order = parseSort(sort);
		Specification<Adl> specification = Specification.where(null);
		if (Boolean.TRUE.equals(criteria.archived()))
			specification = specification.and((root, query, cb) -> cb.isNotNull(root.get("archivedAt")));
		else
			specification = specification.and((root, query, cb) -> cb.isNull(root.get("archivedAt")));
		if (criteria.identifier() != null && !criteria.identifier().isBlank())
			specification = specification.and(contains("businessIdentifier", criteria.identifier()));
		if (criteria.text() != null && !criteria.text().isBlank())
			specification = specification
				.and((root, query, cb) -> cb.or(contains("title", criteria.text()).toPredicate(root, query, cb),
						contains("context", criteria.text()).toPredicate(root, query, cb),
						contains("problem", criteria.text()).toPredicate(root, query, cb)));
		if (criteria.tag() != null && !criteria.tag().isBlank())
			specification = specification.and((root, query, cb) -> {
				query.distinct(true);
				return cb.equal(root.join("tags"), criteria.tag().trim());
			});
		if (criteria.createdFrom() != null)
			specification = specification
				.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.createdFrom()));
		if (criteria.createdTo() != null)
			specification = specification
				.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), criteria.createdTo()));
		var results = repository.findAll(specification, PageRequest.of(page, size, order));
		return new AdlPageResponse(results.getContent().stream().map(AdlResponse::from).toList(), results.getNumber(),
				results.getSize(), results.getTotalElements(), results.getTotalPages());
	}

	@Transactional(readOnly = true)
	public AdlResponse detail(String identifier) {
		Adl adl = repository.findByBusinessIdentifier(identifier)
			.orElseThrow(() -> new ResourceNotFoundException("ADL " + identifier + " was not found"));
		return AdlResponse.from(adl, adrs.findSummariesByAdlIdentifier(identifier));
	}

	@Transactional
	public AdlResponse update(String identifier, AdlUpdateRequest request, AuditActor actor) {
		Adl adl = repository.findByBusinessIdentifier(identifier)
			.orElseThrow(() -> new ResourceNotFoundException("ADL " + identifier + " was not found"));
		if (adl.isArchived())
			throw new ConflictException("Archived ADLs cannot be updated");
		if (adl.getVersion() != request.version())
			throw new ConflictException("ADL was modified by another user");
		Map<String, ?> before = values(adl);
		adl.update(request.title().trim(), request.context().trim(), request.problem().trim(),
				normalizeTags(request.tags()), actor.userId(), actor.displayName(), Instant.now());
		audit.record(AuditEntityType.ADL, adl.getBusinessIdentifier(), "UPDATED", actor, before, values(adl));
		return AdlResponse.from(adl);
	}

	@Transactional
	public AdlResponse archive(String identifier, AdlArchiveRequest request, AuditActor actor) {
		Adl adl = repository.findByBusinessIdentifier(identifier)
			.orElseThrow(() -> new ResourceNotFoundException("ADL " + identifier + " was not found"));
		if (adl.isArchived())
			return AdlResponse.from(adl);
		if (adl.getVersion() != request.version())
			throw new ConflictException("ADL was modified by another user");
		if (adrs.existsByAdlBusinessIdentifierAndStatus(identifier, AdrStatus.UNDER_REVIEW)) {
			throw new ConflictException("ADL cannot be archived while an ADR is under review");
		}
		Map<String, ?> before = values(adl);
		adl.markArchived(Instant.now());
		audit.record(AuditEntityType.ADL, adl.getBusinessIdentifier(), "ARCHIVED", actor, before, values(adl));
		return AdlResponse.from(adl);
	}

	private Set<String> normalizeTags(Set<String> tags) {
		return tags == null ? Set.of()
				: tags.stream()
					.map(String::trim)
					.collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
	}

	private Map<String, Object> values(Adl adl) {
		Map<String, Object> values = new java.util.LinkedHashMap<>();
		values.put("title", adl.getTitle());
		values.put("context", adl.getContext());
		values.put("problem", adl.getProblem());
		values.put("archivedAt", adl.getArchivedAt() == null ? null : adl.getArchivedAt().toString());
		values.put("tags", adl.getTags());
		return values;
	}

	private Specification<Adl> contains(String property, String value) {
		return (root, query, cb) -> cb.like(cb.lower(root.get(property)),
				"%" + value.trim().toLowerCase(java.util.Locale.ROOT) + "%");
	}

	private Sort parseSort(String requestedSort) {
		String value = requestedSort == null || requestedSort.isBlank() ? "createdAt,desc" : requestedSort;
		String[] parts = value.split(",", -1);
		if (parts.length != 2 || !Set.of("identifier", "title", "createdAt").contains(parts[0]))
			throw new IllegalArgumentException("Unsupported sort field");
		String property = "identifier".equals(parts[0]) ? "businessIdentifier" : parts[0];
		try {
			return Sort.by(Sort.Direction.fromString(parts[1]), property);
		}
		catch (IllegalArgumentException exception) {
			throw new IllegalArgumentException("Sort direction must be asc or desc");
		}
	}

}
