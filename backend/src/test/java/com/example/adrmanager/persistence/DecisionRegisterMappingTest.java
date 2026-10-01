package com.example.adrmanager.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.adrmanager.adl.Adl;
import com.example.adrmanager.adl.AdlRepository;
import com.example.adrmanager.adr.Adr;
import com.example.adrmanager.adr.AdrRepository;
import com.example.adrmanager.audit.AuditEntityType;
import com.example.adrmanager.audit.AuditEvent;
import com.example.adrmanager.audit.AuditEventRepository;
import java.time.Instant;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest(properties = { "spring.datasource.url=jdbc:h2:mem:decision-register;MODE=MariaDB;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa",
		"spring.datasource.password=" })
class DecisionRegisterMappingTest {

	@Autowired
	private AdlRepository adls;

	@Autowired
	private AdrRepository adrs;

	@Autowired
	private AuditEventRepository auditEvents;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void persistsCoreEntitiesWithImmutableBusinessIdentifiersAndVersions() {
		Instant now = Instant.parse("2026-01-01T00:00:00Z");
		Adl adl = adls.saveAndFlush(
				new Adl("ADL-0001", "Payment architecture", "Context", "Problem", "user-1", "Alice Architect", now));
		Adr adr = adrs.saveAndFlush(
				new Adr("ADR-0001", adl, "Use ledger", "Context", "Problem", "user-1", "Alice Architect", now));
		auditEvents.saveAndFlush(new AuditEvent(AuditEntityType.ADR, "ADR-0001", "CREATED", "user-1", "Alice Architect",
				now, null, "{\"status\":\"DRAFT\"}"));

		assertThat(adls.findByBusinessIdentifier("ADL-0001")).contains(adl);
		assertThat(adrs.findByBusinessIdentifier("ADR-0001")).contains(adr);
		assertThat(auditEvents.findByEntityIdentifierOrderByOccurredAtAsc("ADR-0001")).hasSize(1);
		assertThat(adr.getVersion()).isZero();
	}

	@Test
	void databasePermitsMultipleApprovedAdrsForOneAdl() {
		Instant now = Instant.parse("2026-01-01T00:00:00Z");
		Adl adl = adls.saveAndFlush(
				new Adl("ADL-0002", "Identity architecture", "Context", "Problem", "user-2", "Bob Approver", now));

		adrs.saveAndFlush(newApprovedAdr("ADR-0002", adl, now));

		adrs.saveAndFlush(newApprovedAdr("ADR-0003", adl, now));

		assertThat(adrs.findByAdlBusinessIdentifierOrderByCreatedAtAsc("ADL-0002")).hasSize(2);
	}

	@Test
	void migrationRemovesProjectTableAndAdlReference() {
		assertThat(jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'PROJECTS'", Integer.class))
			.isZero();
		assertThat(jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'ADLS' AND COLUMN_NAME = 'PROJECT_ID'",
				Integer.class))
			.isZero();
	}

	@Test
	void migrationConvertsLegacyRowsAndRemovesLegacyColumns() {
		String url = "jdbc:h2:mem:legacy-decision-register;MODE=MariaDB;DB_CLOSE_DELAY=-1";
		Flyway.configure().dataSource(url, "sa", "").locations("classpath:db/migration").target("3").load().migrate();
		JdbcTemplate legacy = new JdbcTemplate(
				new org.springframework.jdbc.datasource.DriverManagerDataSource(url, "sa", ""));

		legacy
			.update("""
					INSERT INTO adls (business_identifier, title, context, problem, status, responsible_user_id,
					    responsible_display_name, created_by_user_id, created_by_display_name, created_at, updated_at, version)
					VALUES ('ADL-LEGACY', 'Legacy ADL', 'Context', 'Problem', 'ARCHIVED', 'owner-1', 'Owner',
					    'creator-1', 'Creator', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
					""");
		Long adlId = legacy.queryForObject("SELECT id FROM adls WHERE business_identifier = 'ADL-LEGACY'", Long.class);
		legacy.update("""
				INSERT INTO adrs (adl_id, business_identifier, title, context, problem, status,
				    author_user_id, author_display_name, created_at, updated_at, version)
				VALUES (?, 'ADR-LEGACY', 'Legacy ADR', 'Context', 'Problem', 'SUPERSEDED',
				    'author-1', 'Author', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
				""", adlId);

		Flyway.configure().dataSource(url, "sa", "").locations("classpath:db/migration").load().migrate();

		assertThat(legacy.queryForObject(
				"SELECT archived_at IS NOT NULL FROM adls WHERE business_identifier = 'ADL-LEGACY'", Boolean.class))
			.isTrue();
		assertThat(
				legacy.queryForObject("SELECT status FROM adrs WHERE business_identifier = 'ADR-LEGACY'", String.class))
			.isEqualTo("APPROVED");
		assertThat(legacy.queryForObject(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'ADLS' AND COLUMN_NAME IN ('STATUS', 'RESPONSIBLE_USER_ID', 'RESPONSIBLE_DISPLAY_NAME')",
				Integer.class))
			.isZero();
		assertThat(legacy.queryForObject(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'ADRS' AND COLUMN_NAME = 'SUPERSEDED_BY_ADR_ID'",
				Integer.class))
			.isZero();
	}

	private Adr newApprovedAdr(String identifier, Adl adl, Instant now) {
		Adr adr = new Adr(identifier, adl, "Decision", "Context", "Problem", "user-2", "Bob Approver", now);
		adr.markApproved("user-2", "Bob Approver", now);
		return adr;
	}

}
