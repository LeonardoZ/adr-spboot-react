# Spec Delta

## MODIFIED Requirements

### Requirement: Create ADLs
The system SHALL allow an authorized architect to create an ADL with a generated immutable identifier, title, context, problem, optional tags, creator, and timestamps. An ADL SHALL not have a responsible user or lifecycle status.

#### Scenario: Valid ADL creation
- **WHEN** an authorized architect submits all required valid ADL fields
- **THEN** the system creates an active ADL with a generated identifier and records its creator and creation time

#### Scenario: Invalid ADL creation
- **WHEN** a required field is invalid
- **THEN** the system creates no ADL and returns a validation error

### Requirement: Search and view ADLs
The system SHALL let every authenticated user browse the company-wide register with pagination, supported sorting, archived-hidden-by-default behavior, and combined filters for identifier, text, tag, period, and archived state. ADL detail SHALL expose whether the ADL is archived, include summaries of zero or more related ADRs, and provide an ADR-creation action for active ADLs.

#### Scenario: Combined search
- **WHEN** a user supplies multiple valid filters
- **THEN** the returned page contains only ADLs matching all supplied filters

#### Scenario: Empty search result
- **WHEN** no ADL matches a valid query or page
- **THEN** the system returns an empty page without an error

#### Scenario: ADL detail includes ADR summaries
- **WHEN** an authenticated user views an ADL with related ADRs
- **THEN** the detail response includes each related ADR's identifier, title, status, author display name, and creation time

#### Scenario: ADL detail has no ADRs
- **WHEN** an authenticated user views an ADL with no related ADRs
- **THEN** the detail response includes an empty ADR summary list

#### Scenario: Create ADR from an active ADL detail
- **WHEN** an authorized architect views an active ADL detail
- **THEN** the interface provides an action that starts ADR creation for that ADL

#### Scenario: Archived ADL detail
- **WHEN** an authenticated user views an archived ADL detail
- **THEN** the interface identifies it as archived and does not provide an ADR-creation action

### Requirement: Update ADLs safely
The system SHALL permit authorized updates to mutable ADL content while keeping its identifier immutable, recording the updater and timestamp, and rejecting stale concurrent modifications. Updates SHALL not accept or modify responsible-user or lifecycle-status data.

#### Scenario: Concurrent ADL edit
- **WHEN** a user submits an update based on an outdated ADL version
- **THEN** the system rejects the update as a conflict without silently overwriting newer data

### Requirement: Archive ADLs
The system SHALL allow an authorized user to archive an ADL without deleting it, record the archive timestamp, and reject archival while any related ADR is UNDER_REVIEW. An archived ADL remains consultable, cannot receive content changes, and cannot receive ADR creation or ADR workflow state changes.

#### Scenario: Archive eligible ADL
- **WHEN** an authorized user archives an ADL with no ADR under review
- **THEN** the ADL is marked archived, remains consultable, and cannot receive content changes or ADR activity
