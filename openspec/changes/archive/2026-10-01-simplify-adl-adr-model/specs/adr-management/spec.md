# Spec Delta

## MODIFIED Requirements

### Requirement: Consult ADRs
The system SHALL let every authenticated user list ADR summaries and consult complete ADR details for any company-wide ADL, including author, status, and decision metadata. ADR details SHALL not expose a supersession reference.

#### Scenario: Consult rejected ADR
- **WHEN** a user opens a rejected ADR
- **THEN** the system returns its preserved details and decision history metadata

### Requirement: Edit draft ADRs
The system SHALL allow authorized edits only while an ADR is DRAFT, keep the ADR identifier and parent ADL immutable, record the editor and time, and reject stale concurrent modifications.

#### Scenario: Edit decided ADR
- **WHEN** a user attempts to edit an APPROVED or REJECTED ADR
- **THEN** the system rejects the change and preserves its content
