# Tasks

## 1. Persist the simplified domain model

- [x] 1.1 Add a forward Flyway migration that backfills `archived_at` for legacy archived ADLs, converts legacy SUPERSEDED ADRs to APPROVED, and removes obsolete ADL responsibility/status and ADR replacement schema objects; verify it succeeds against a database seeded with legacy rows.
- [x] 1.2 Remove ADL status and responsible-user persistence fields and make archival checks derive from `archivedAt`; verify ADL entity/repository integration tests cover active and archived records.
- [x] 1.3 Remove ADR supersession persistence fields and the SUPERSEDED enum value; verify migration and ADR persistence tests no longer require replacement references.

## 2. Update backend API and workflow behavior

- [x] 2.1 Remove ADL responsible-user and status fields from create/update/detail/search contracts, validation, filters, OpenAPI descriptions, and audit snapshots; verify controller and service tests reject neither omitted responsibility nor use obsolete filters.
- [x] 2.2 Preserve ADL archival through its timestamp, including idempotent archive behavior and archive guards for content edits, ADR creation, and ADR workflow transitions; verify integration tests cover each archived-parent rejection and the under-review archival guard.
- [x] 2.3 Remove parent-ADL state transitions and ADL audit events from ADR submit, approve, and reject workflows; verify ADR workflow tests retain ADR state and decision metadata without changing the parent ADL.
- [x] 2.4 Remove the supersede endpoint, service behavior, response field, audit action, and related security routes; verify API/security regression tests confirm the remaining ADR routes and no supersede route is exposed.

## 3. Update the web experience

- [x] 3.1 Remove ADL responsibility/status types, form controls, filters, and displays; expose archive state from the revised API and verify ADL list, form, and detail component tests cover the new contract.
- [x] 3.2 Add a Create ADR button on active ADL details that routes to the existing nested ADR creation page, and hide it for archived ADLs; verify frontend tests cover both states and the generated route.
- [x] 3.3 Remove ADR supersession API types, client method, detail display, and action controls; verify ADR page tests cover only DRAFT, UNDER_REVIEW, APPROVED, and REJECTED actions.

## 4. Verify integration and release compatibility

- [x] 4.1 Update API integration fixtures, compose E2E input, and developer documentation to omit removed ADL and ADR fields; verify the Compose end-to-end scenario completes ADL creation, ADR creation, review, approval, and audit consultation.
- [x] 4.2 Run backend tests, frontend tests, and strict OpenSpec validation for `simplify-adl-adr-model`; verify all pass with no references to ADL status/responsibility or ADR supersession in supported contracts.
