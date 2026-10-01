# audit-history Specification

## Purpose
Provide an immutable record of significant decision-register actions for governance and traceability.

## Requirements

### Requirement: Record auditable business events
The system SHALL record creation, updates, responsibility changes, review submission, approval, rejection, cancellation, archival, and supersession with actor, timestamp, entity type, entity identifier, action, and structured before/after values where applicable.

#### Scenario: Critical decision is recorded atomically
- **WHEN** an ADR approval or rejection succeeds
- **THEN** its audit event is persisted in the same transaction as the business-state change

### Requirement: Preserve and consult history
The system SHALL preserve audit events when an ADL or ADR is archived, prohibit modification or deletion through normal user flows, and allow authenticated users to consult relevant history.

#### Scenario: Consult archived ADL history
- **WHEN** a user views an archived ADL
- **THEN** the system returns its retained audit events
