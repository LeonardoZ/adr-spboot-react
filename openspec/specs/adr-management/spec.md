# adr-management Specification

## Purpose
Capture and preserve alternative Architecture Decision Records within an ADL.

## Requirements

### Requirement: Create ADR alternatives
The system SHALL allow an authorized architect to create a DRAFT ADR under a non-archived ADL with a generated immutable identifier, title, content fields, author, and creation time.

#### Scenario: ADR creation for an active ADL
- **WHEN** an authorized architect submits a valid ADR for an active ADL
- **THEN** the system creates a DRAFT ADR linked to that ADL

#### Scenario: ADR creation for archived ADL
- **WHEN** an architect attempts to create an ADR for an archived ADL
- **THEN** the system rejects the request and creates no ADR

### Requirement: Consult ADRs
The system SHALL let every authenticated user list ADR summaries and consult complete ADR details for any company-wide ADL, including author, status, and decision metadata. ADR details SHALL not expose a supersession reference.

#### Scenario: Consult rejected ADR
- **WHEN** a user opens a rejected ADR
- **THEN** the system returns its preserved details and decision history metadata

### Requirement: Present ADRs with neutral terminology
The system SHALL present ADR list, creation, editing, and detail interfaces as Architecture Decision Records and SHALL not label them as alternatives.

#### Scenario: Browse ADRs for an ADL
- **WHEN** an authenticated user opens the ADR management page for an ADL
- **THEN** the page, breadcrumb, and empty state use Architecture Decision Records terminology and retain the existing ADR list and creation capability

#### Scenario: Create or consult an ADR
- **WHEN** an authorized user opens an ADR creation, editing, or detail interface
- **THEN** the interface uses Architecture Decision Record terminology while retaining its existing fields and workflow actions

### Requirement: Edit draft ADRs
The system SHALL allow authorized edits only while an ADR is DRAFT, keep the ADR identifier and parent ADL immutable, record the editor and time, and reject stale concurrent modifications.

#### Scenario: Edit decided ADR
- **WHEN** a user attempts to edit an APPROVED or REJECTED ADR
- **THEN** the system rejects the change and preserves its content
