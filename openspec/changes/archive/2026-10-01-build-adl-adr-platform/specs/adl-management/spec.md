# Spec Delta

## Purpose

Manage company-wide Architecture Decision Logs from creation through archival and discovery.

## ADDED Requirements

### Requirement: Create ADLs
The system SHALL allow an authorized architect to create an ADL with a generated immutable identifier, title, context, problem, active responsible user, active project, optional tags, creator, timestamps, and initial OPEN status.

#### Scenario: Valid ADL creation
- **WHEN** an authorized architect submits all required valid ADL fields
- **THEN** the system creates an OPEN ADL with a generated identifier and records its creator and creation time

#### Scenario: Invalid ADL creation
- **WHEN** a required field, active responsible user, or active project is invalid
- **THEN** the system creates no ADL and returns a validation error

### Requirement: Search and view ADLs
The system SHALL let every authenticated user browse the company-wide register with pagination, supported sorting, archived-hidden-by-default behavior, and combined filters for identifier, text, status, responsible user, project, tag, period, and archived state.

#### Scenario: Combined search
- **WHEN** a user supplies multiple valid filters
- **THEN** the returned page contains only ADLs matching all supplied filters

#### Scenario: Empty search result
- **WHEN** no ADL matches a valid query or page
- **THEN** the system returns an empty page without an error

### Requirement: Update ADLs safely
The system SHALL permit authorized updates to mutable ADL content while keeping its identifier immutable, recording the updater and timestamp, and rejecting stale concurrent modifications.

#### Scenario: Concurrent ADL edit
- **WHEN** a user submits an update based on an outdated ADL version
- **THEN** the system rejects the update as a conflict without silently overwriting newer data

### Requirement: Archive ADLs
The system SHALL allow an authorized user to archive an ADL without deleting it, and SHALL reject archival while any related ADR is UNDER_REVIEW.

#### Scenario: Archive eligible ADL
- **WHEN** an authorized user archives an ADL with no ADR under review
- **THEN** the ADL becomes ARCHIVED, remains consultable, and cannot receive content changes
