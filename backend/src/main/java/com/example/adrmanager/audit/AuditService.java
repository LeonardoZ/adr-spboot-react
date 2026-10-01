package com.example.adrmanager.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

	private final AuditEventRepository repository;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public AuditService(AuditEventRepository repository) {
		this.repository = repository;
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public AuditEvent record(AuditEntityType entityType, String entityIdentifier, String action, AuditActor actor,
			Map<String, ?> before, Map<String, ?> after) {
		return repository.save(new AuditEvent(entityType, entityIdentifier, action, actor.userId(), actor.displayName(),
				Instant.now(), json(before), json(after)));
	}

	private String json(Map<String, ?> value) {
		if (value == null)
			return null;
		try {
			return objectMapper.writeValueAsString(value);
		}
		catch (JsonProcessingException exception) {
			throw new IllegalArgumentException("Audit values must be JSON serializable", exception);
		}
	}

}
