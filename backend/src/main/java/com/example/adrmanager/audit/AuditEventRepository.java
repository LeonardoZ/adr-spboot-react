package com.example.adrmanager.audit;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

	List<AuditEvent> findByEntityTypeAndEntityIdentifierOrderByOccurredAtAsc(AuditEntityType entityType,
			String entityIdentifier);

	List<AuditEvent> findByEntityIdentifierOrderByOccurredAtAsc(String entityIdentifier);

}
