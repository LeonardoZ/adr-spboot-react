# Spec Delta

## MODIFIED Requirements

### Requirement: Create ADLs
The system SHALL allow an authorized architect to create an ADL with a generated immutable identifier, title, context, problem, active responsible user, optional tags, creator, timestamps, and initial OPEN status.

#### Scenario: Valid ADL creation
- **WHEN** an authorized architect submits all required valid ADL fields
- **THEN** the system creates an OPEN ADL with a generated identifier and records its creator and creation time

#### Scenario: Invalid ADL creation
- **WHEN** a required field or active responsible user is invalid
- **THEN** the system creates no ADL and returns a validation error

### Requirement: Search and view ADLs
The system SHALL let every authenticated user browse the company-wide register with pagination, supported sorting, archived-hidden-by-default behavior, and combined filters for identifier, text, status, responsible user, tag, period, and archived state. ADL detail SHALL include summaries of zero or more related ADRs.

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

## REMOVED Requirements
