package com.example.adrmanager.adl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.adrmanager.api.ConflictException;
import com.example.adrmanager.audit.AuditActor;
import com.example.adrmanager.audit.AuditEventRepository;
import com.example.adrmanager.audit.AuditService;
import com.example.adrmanager.adr.Adr;
import com.example.adrmanager.adr.AdrRepository;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
@Import({ AdlService.class, AuditService.class })
class AdlCreationIntegrationTest {

	private static final AuditActor ARCHITECT = new AuditActor("architect-1", "Architect One");

	@Autowired
	private AdlService service;

	@Autowired
	private AdlRepository adls;

	@Autowired
	private AdrRepository adrRepository;

	@Autowired
	private AuditEventRepository auditEvents;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void createsAnActiveAdlWithoutResponsibleUserOrStatus() {
		AdlResponse response = service.create(new AdlCreateRequest(" Ledger decision ", " Current ledger ",
				" Choose a ledger ", Set.of(" finance ", "core")), ARCHITECT);

		assertThat(response.identifier()).startsWith("ADL-");
		assertThat(response.archivedAt()).isNull();
		assertThat(response.tags()).containsExactlyInAnyOrder("finance", "core");
		assertThat(auditEvents.findByEntityIdentifierOrderByOccurredAtAsc(response.identifier())).singleElement()
			.satisfies(event -> assertThat(event.getAction()).isEqualTo("CREATED"));
	}

	@Test
	void searchesBySupportedFiltersAndHidesArchivedAdlsByDefault() {
		Adl matching = persist("ADL-PAY-1", "Ledger architecture", Set.of("finance", "core"));
		persist("ADL-IDENTITY-1", "Authentication architecture", Set.of("security"));
		Adl archived = persist("ADL-PAY-OLD", "Legacy ledger", Set.of("finance"));
		archived.markArchived(Instant.now());
		adls.saveAndFlush(archived);

		assertThat(
				service.search(new AdlSearchCriteria("PAY", "ledger", "finance", null, null, null), 0, 20, "title,asc")
					.content())
			.extracting(AdlResponse::identifier)
			.containsExactly(matching.getBusinessIdentifier());
		assertThat(service.search(new AdlSearchCriteria(null, null, null, null, null, true), 0, 20, null).content())
			.extracting(AdlResponse::identifier)
			.containsExactly(archived.getBusinessIdentifier());
	}

	@Test
	void archivesEligibleAdlsAndPreventsFurtherContentChanges() {
		AdlResponse created = service.create(new AdlCreateRequest("Archive me", "Context", "Problem", Set.of()),
				ARCHITECT);
		AdlResponse archived = service.archive(created.identifier(), new AdlArchiveRequest(created.version()),
				ARCHITECT);

		assertThat(archived.archivedAt()).isNotNull();
		assertThatThrownBy(() -> service.update(created.identifier(),
				new AdlUpdateRequest("Changed", "Context", "Problem", Set.of(), archived.version()), ARCHITECT))
			.isInstanceOf(ConflictException.class);
		assertThat(service.archive(created.identifier(), new AdlArchiveRequest(archived.version()), ARCHITECT)
			.archivedAt()).isEqualTo(archived.archivedAt());
	}

	@Test
	void refusesArchivalWhileAnyAdrIsUnderReview() {
		Adl adl = persist("ADL-REVIEW", "Review", Set.of());
		Adr adr = adrRepository.saveAndFlush(new Adr("ADR-REVIEW", adl, "Decision", "Context", "Problem", "architect-1",
				"Architect One", Instant.now()));
		jdbcTemplate.update("UPDATE adrs SET status = 'UNDER_REVIEW' WHERE id = ?", adr.getId());

		assertThatThrownBy(() -> service.archive("ADL-REVIEW", new AdlArchiveRequest(adl.getVersion()), ARCHITECT))
			.isInstanceOf(ConflictException.class);
		assertThat(service.detail("ADL-REVIEW").archivedAt()).isNull();
	}

	private Adl persist(String identifier, String title, Set<String> tags) {
		Adl adl = new Adl(identifier, title, "Context", "Problem", "architect-1", "Architect One", Instant.now());
		adl.setTags(tags);
		return adls.saveAndFlush(adl);
	}

}
