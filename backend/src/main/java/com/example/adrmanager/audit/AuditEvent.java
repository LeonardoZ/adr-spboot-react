package com.example.adrmanager.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "audit_events")
@Immutable
public class AuditEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "entity_type", nullable = false)
	private AuditEntityType entityType;

	@Column(name = "entity_identifier", nullable = false)
	private String entityIdentifier;

	@Column(nullable = false)
	private String action;

	@Column(name = "actor_user_id", nullable = false)
	private String actorUserId;

	@Column(name = "actor_display_name", nullable = false)
	private String actorDisplayName;

	@Column(name = "occurred_at", nullable = false, updatable = false)
	private Instant occurredAt;

	@Column(name = "before_value", columnDefinition = "LONGTEXT")
	private String beforeValue;

	@Column(name = "after_value", columnDefinition = "LONGTEXT")
	private String afterValue;

	protected AuditEvent() {
	}

	public AuditEvent(AuditEntityType entityType, String entityIdentifier, String action, String actorUserId,
			String actorDisplayName, Instant occurredAt, String beforeValue, String afterValue) {
		this.entityType = entityType;
		this.entityIdentifier = entityIdentifier;
		this.action = action;
		this.actorUserId = actorUserId;
		this.actorDisplayName = actorDisplayName;
		this.occurredAt = occurredAt;
		this.beforeValue = beforeValue;
		this.afterValue = afterValue;
	}

	public Long getId() {
		return id;
	}

	public String getEntityIdentifier() {
		return entityIdentifier;
	}

	public AuditEntityType getEntityType() {
		return entityType;
	}

	public String getAction() {
		return action;
	}

	public String getActorUserId() {
		return actorUserId;
	}

	public String getActorDisplayName() {
		return actorDisplayName;
	}

	public Instant getOccurredAt() {
		return occurredAt;
	}

	public String getBeforeValue() {
		return beforeValue;
	}

	public String getAfterValue() {
		return afterValue;
	}

}
