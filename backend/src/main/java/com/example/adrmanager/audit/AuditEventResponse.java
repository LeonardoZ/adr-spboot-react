package com.example.adrmanager.audit;

import java.time.Instant;

public record AuditEventResponse(String entityType, String entityIdentifier, String action, String actorUserId,
		String actorDisplayName, Instant occurredAt, String beforeValue, String afterValue) {
	static AuditEventResponse from(AuditEvent event) {
		return new AuditEventResponse(event.getEntityType().name(), event.getEntityIdentifier(), event.getAction(),
				event.getActorUserId(), event.getActorDisplayName(), event.getOccurredAt(), event.getBeforeValue(),
				event.getAfterValue());
	}
}
