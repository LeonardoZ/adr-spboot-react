# Tasks

## 1. Repository and development environment

- [x] 1.1 Create the Spring Boot Java 21 backend structure with web, validation, security, JPA, Flyway, MariaDB, OpenAPI, and test dependencies; verify the backend test command runs successfully
- [x] 1.2 Create the React/Vite frontend with Material UI, React Query, React Hook Form, Yup, routing, and test tooling; verify the frontend build and test commands run successfully
- [x] 1.3 Add backend and frontend Dockerfiles plus Docker Compose services for MariaDB, Keycloak, API, and SPA; verify the full stack starts from documented environment variables
- [x] 1.4 Version a development Keycloak realm import with client, roles, test users, and backend service account; verify a test user can obtain a token and the service account can perform configured user lookup
- [x] 1.5 Document local setup, startup, test, and configuration procedures in the project README; verify the documented Compose commands work on a clean environment

## 2. API foundation and identity access

- [x] 2.1 Configure Spring Security as a Keycloak JWT resource server with recognized USER, ARCHITECT, APPROVER, and ADMIN authorities; verify API tests reject missing, invalid, and unrecognized-role tokens
- [x] 2.2 Implement backend Keycloak client support for active-user lookup and display-name snapshots using service-account credentials; verify tests cover active, inactive, unknown, and unavailable users
- [x] 2.3 Establish API request validation and the standard problem-error response for validation, authentication, authorization, missing-resource, and conflict errors; verify controller integration tests assert each response shape and status
- [x] 2.4 Configure OpenAPI documentation and API health checks; verify the generated OpenAPI endpoint describes secured APIs and the health endpoint reports dependencies
- [x] 2.5 Implement SPA OIDC login, logout, token renewal, route protection, and role-aware navigation; verify frontend tests cover anonymous redirects and hidden unauthorized actions

## 3. Database model and audit infrastructure

- [x] 3.1 Create Flyway migrations and JPA mappings for projects, ADLs, ADRs, tags, audit events, immutable business identifiers, timestamps, and optimistic-lock versions; verify migrations run against MariaDB and repository mapping tests pass
- [x] 3.2 Add relational indexes, foreign keys, and non-delete protections while permitting multiple approved or rejected ADRs per ADL; verify integration tests exercise invalid references and multiple approval persistence
- [x] 3.3 Implement immutable audit-event persistence with structured before/after values and transactional participation; verify service tests show failed critical actions leave no business or audit partial state

## 4. Project catalog

- [x] 4.1 Implement ADMIN-only project create, list, update, and deactivate APIs with validation and standardized errors; verify API integration tests cover authorized and unauthorized requests
- [x] 4.2 Implement the project administration list and form screens with React Query mutations and React Hook Form/Yup validation; verify frontend tests cover create, update, and deactivation feedback
- [x] 4.3 Enforce active-project selection while preserving references to inactive projects; verify integration tests retain historical ADL project data after deactivation

## 5. ADL management

- [x] 5.1 Implement ADL creation with generated identifiers, responsible-user validation, active-project validation, tags, actor/timestamp fields, OPEN state, and audit events; verify integration tests reject invalid input without partial records
- [x] 5.2 Implement paginated ADL search and detail APIs with sorting allowlist, combined filters, archived-hidden default, and company-wide authenticated visibility; verify integration tests cover each filter, empty pages, and invalid parameters
- [x] 5.3 Implement authorized ADL update with version conflict handling, audit events, and immutable identifiers; verify integration tests reject stale versions and preserve the newer record
- [x] 5.4 Implement ADL archival with under-review ADR guard, immutable archived content, and audit events; verify integration tests cover eligible and blocked archival
- [x] 5.5 Implement ADL list, filter, detail, create, edit, and archive SPA views with query invalidation and server-error presentation; verify frontend tests cover empty results, validation, conflict, and archived states

## 6. ADR management

- [x] 6.1 Implement ADR creation, list, and detail APIs under active ADLs with generated identifiers, DRAFT status, author metadata, and audit events; verify integration tests cover active, archived, and nonexistent parent ADLs
- [x] 6.2 Implement DRAFT-only ADR updates with parent/identifier immutability, version conflict handling, and audit events; verify integration tests reject edits to UNDER_REVIEW and terminal ADRs
- [x] 6.3 Implement ADR list, detail, creation, and DRAFT edit SPA views including all decision content fields and read-only terminal-state presentation; verify frontend tests cover form validation and non-editable ADRs

## 7. Decision workflow

- [x] 7.1 Implement the ADR state machine for submission and cancellation, including required-review content, actor permissions, parent-ADL archive guard, IN_ANALYSIS transition, and audit events; verify workflow integration tests cover all valid and invalid transitions
- [x] 7.2 Implement any-APPROVER approval and rejection with comments/required rejection justification, atomic idempotent ADL DECIDED updates, and concurrent independent-decision protection; verify transaction tests cover successful, unauthorized, concurrent, and rollback cases
- [x] 7.3 Implement approved-ADR supersession with same-ADL approved replacements, cycle prevention, immutable history, and audit events; verify integration tests reject self, unapproved, cross-ADL, and cyclic replacements
- [x] 7.4 Implement workflow action controls and decision metadata in the SPA, driven by current state and role while relying on API enforcement; verify frontend tests cover allowed and unavailable actions plus mutation error feedback

## 8. Audit history and API integration

- [x] 8.1 Implement authenticated audit-history APIs for ADLs and ADRs that return immutable events including retained archived history; verify API integration tests cover chronology, content, and archived records
- [x] 8.2 Add audit-history presentation to ADL and ADR detail views; verify frontend tests render structured event data and empty-history behavior
- [x] 8.3 Complete OpenAPI descriptions and examples for pagination, filters, optimistic versioning, workflow actions, and error responses; verify the generated document remains valid in CI

## 9. End-to-end quality checks

- [x] 9.1 Add Compose-backed end-to-end coverage for login, project setup, ADL creation, ADR drafting, submission, approval, and audit consultation; verify the scenario passes against the local container stack
- [x] 9.2 Add a role/security regression suite covering API authorization for every protected action; verify it prevents frontend-only authorization bypasses
- [x] 9.3 Run backend tests, frontend tests, production builds, Compose startup, and OpenSpec validation; verify all commands pass and document the final verification results
