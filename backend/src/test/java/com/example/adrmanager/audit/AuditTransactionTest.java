package com.example.adrmanager.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.adrmanager.adl.Adl;
import com.example.adrmanager.adl.AdlRepository;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@DataJpaTest
@Import(AuditService.class)
class AuditTransactionTest {

	@Autowired
	private AdlRepository adls;

	@Autowired
	private AuditEventRepository auditEvents;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Test
	void rollsBackBusinessAndAuditWritesTogetherWhenCriticalWorkFails() {
		AuditService auditService = new AuditService(auditEvents);
		TransactionTemplate transaction = new TransactionTemplate(transactionManager);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

		assertThatThrownBy(() -> transaction.executeWithoutResult(status -> {
			adls.save(new Adl("ADL-ROLLBACK", "Rollback", "Context", "Problem", "user-1", "Alice", Instant.now()));
			auditService.record(AuditEntityType.ADL, "ADL-ROLLBACK", "CREATED", new AuditActor("user-1", "Alice"), null,
					Map.of("status", "OPEN"));
			throw new IllegalStateException("critical action failed");
		})).isInstanceOf(IllegalStateException.class);

		assertThat(adls.findByBusinessIdentifier("ADL-ROLLBACK")).isEmpty();
		assertThat(auditEvents.findByEntityIdentifierOrderByOccurredAtAsc("ADL-ROLLBACK")).isEmpty();
	}

}
