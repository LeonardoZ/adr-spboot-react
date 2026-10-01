# Tasks

## 1. Remove project persistence and backend behavior

- [x] 1.1 Add a Flyway migration that drops the ADL project foreign key and column and removes project catalog tables, then verify migration tests confirm existing project assignments and records are discarded.
- [x] 1.2 Remove project references from the ADL entity, create/update requests, response, search criteria, service, and audit values; update backend tests and verify ADL creation, update, search, and detail behavior without projects.
- [x] 1.3 Add compact ADR summaries to ADL detail using a repository projection or focused query; update backend tests to verify empty and populated summary lists and confirm ADL search does not load summaries.
- [x] 1.4 Remove project catalog API and implementation; update backend tests and verify no project endpoints or persistence dependencies remain.

## 2. Update the frontend and integration flow

- [x] 2.1 Remove project management navigation and screens, remove project from ADL forms and lists, and show linked ADR summaries on ADL detail; update frontend tests to verify project controls are absent and summaries render and navigate correctly.
- [x] 2.2 Update API client types and tests for project-free ADLs with ADR summaries, and update the end-to-end fixture to create and consult ADLs without projects; verify the frontend suite and Compose end-to-end scenario pass.

## 3. Update documentation and check integration

- [x] 3.1 Update README and API documentation to describe project removal, the breaking ADL contract, and ADR summaries in ADL detail; verify documented endpoints and payloads match the implementation.
- [x] 3.2 Run the relevant backend, frontend, migration, and end-to-end checks; verify the complete ADL-to-ADR summary flow works and the application starts against a migrated database.
