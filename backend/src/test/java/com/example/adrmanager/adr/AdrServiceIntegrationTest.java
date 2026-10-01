package com.example.adrmanager.adr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.adrmanager.adl.Adl;
import com.example.adrmanager.adl.AdlRepository;
import com.example.adrmanager.api.ConflictException;
import com.example.adrmanager.audit.AuditActor;
import com.example.adrmanager.audit.AuditEventRepository;
import com.example.adrmanager.audit.AuditService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({ AdrService.class, AuditService.class })
class AdrServiceIntegrationTest {

	private static final AuditActor ARCHITECT = new AuditActor("architect-1", "Architect One");

	@Autowired
	private AdrService service;

	@Autowired
	private AdlRepository adls;

	@Autowired
	private AuditEventRepository auditEvents;

	@Test
	void createsDraftAndRejectsCreationForArchivedAdl() {
		Adl active = activeAdl("ADL-ACTIVE");
		AdrResponse created = service.create(active.getBusinessIdentifier(), completeRequest(), ARCHITECT);
		assertThat(created.status()).isEqualTo(AdrStatus.DRAFT);

		Adl archived = activeAdl("ADL-ARCHIVED");
		archived.markArchived(Instant.now());
		adls.saveAndFlush(archived);
		assertThatThrownBy(() -> service.create(archived.getBusinessIdentifier(), completeRequest(), ARCHITECT))
			.isInstanceOf(ConflictException.class);
	}

	@Test
	void workflowChangesOnlyAdrStatusAndHonorsArchivedParent() {
		Adl parent = activeAdl("ADL-WORKFLOW");
		AdrResponse draft = service.create(parent.getBusinessIdentifier(), completeRequest(), ARCHITECT);
		AdrResponse submitted = service.submit(draft.identifier(), new AdrActionRequest(draft.version()), ARCHITECT);
		assertThat(submitted.status()).isEqualTo(AdrStatus.UNDER_REVIEW);
		assertThat(adls.findByBusinessIdentifier(parent.getBusinessIdentifier()))
			.hasValueSatisfying(adl -> assertThat(adl.isArchived()).isFalse());

		AdrResponse approved = service.approve(submitted.identifier(),
				new AdrDecisionRequest(submitted.version(), "Approved"), new AuditActor("approver", "Approver"));
		assertThat(approved.status()).isEqualTo(AdrStatus.APPROVED);
		assertThat(auditEvents.findByEntityIdentifierOrderByOccurredAtAsc(parent.getBusinessIdentifier())).isEmpty();

		Adl archivedParent = activeAdl("ADL-ARCHIVED-WORKFLOW");
		AdrResponse archivedDraft = service.create(archivedParent.getBusinessIdentifier(), completeRequest(),
				ARCHITECT);
		archivedParent.markArchived(Instant.now());
		adls.saveAndFlush(archivedParent);
		assertThatThrownBy(() -> service.submit(archivedDraft.identifier(),
				new AdrActionRequest(archivedDraft.version()), ARCHITECT))
			.isInstanceOf(ConflictException.class);
	}

	@Test
	void rejectsEditsToAnyNonDraftAdr() {
		AdrResponse draft = service.create(activeAdl("ADL-EDIT").getBusinessIdentifier(), completeRequest(), ARCHITECT);
		AdrResponse submitted = service.submit(draft.identifier(), new AdrActionRequest(draft.version()), ARCHITECT);
		assertThatThrownBy(() -> service.update(submitted.identifier(), new AdrUpdateRequest("Changed", "Context",
				"Problem", "Options", "Decision", "Consequences", submitted.version()), ARCHITECT))
			.isInstanceOf(ConflictException.class);
	}

	private Adl activeAdl(String identifier) {
		return adls.saveAndFlush(
				new Adl(identifier, "Decision", "Context", "Problem", "architect-1", "Architect One", Instant.now()));
	}

	private AdrCreateRequest completeRequest() {
		return new AdrCreateRequest("Decision", "Context", "Problem", "Options", "Decision", "Consequences");
	}

}
