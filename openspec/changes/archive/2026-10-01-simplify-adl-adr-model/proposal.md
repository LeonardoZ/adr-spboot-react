# Proposal

## Why

ADLs currently duplicate ownership of responsibility and decision lifecycle state that belongs to the ADRs they contain. Removing those concepts from the ADL makes the register a stable, archivable container while each ADR is the sole record of decision progress.

## What Changes

- **BREAKING** Remove responsible-user identity and display-name fields from ADL creation, update, search, detail, persistence, and audit representations.
- **BREAKING** Remove the ADL status field and its OPEN, IN_ANALYSIS, DECIDED, and ARCHIVED transitions. Preserve ADL archival using `archivedAt` as the source of truth for archive state.
- Keep archived ADLs consultable and read-only; prevent creation of ADRs and ADR workflow state changes under an archived ADL.
- **BREAKING** Remove ADR supersession: the SUPERSEDED status, replacement relation, endpoint, API fields, UI controls, audit event, and supporting database schema are removed.
- Add a Create ADR action to the ADL detail page for active ADLs, routing to the existing ADR creation flow for that ADL.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `adl-management`: Simplify ADLs by removing responsible-user and lifecycle-status behavior while retaining archival through an archive marker and providing the detail-page ADR creation entry point.
- `adr-management`: Remove supersession data and terminal state from ADR consultation and editing behavior.
- `decision-workflow`: Make ADR status the only decision lifecycle, remove parent-ADL status transitions and supersession workflow, and retain archived-parent guards.

## Impact

- Backend JPA entities, request/response DTOs, controllers, query criteria, services, Keycloak responsible-user lookup usage, and audit snapshots.
- A forward Flyway migration that removes obsolete ADL and ADR columns, index, and self-referential foreign key while preserving existing records and archive timestamps.
- Frontend API types, ADL forms/list/detail views, ADR detail actions, and corresponding tests.
- API consumers must stop sending or reading ADL responsibility/status fields and ADR supersession fields, and must not call the supersede endpoint.
