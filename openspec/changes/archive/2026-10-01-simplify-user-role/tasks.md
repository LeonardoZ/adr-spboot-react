# Tasks

## 1. Backend single-role authorization

- [x] 1.1 Restrict JWT role recognition and API request authorization to `USER`, and update project, ADL, ADR, and audit controller guards so a USER can invoke every protected capability; verify controller security tests prove USER access and denial for a valid token without USER.
- [x] 1.2 Remove the architect-role bypass from ADR submit and cancel-review handling while preserving the applicable author, state, version, and validation rules; verify ADR workflow integration tests cover the retained ownership behavior.
- [x] 1.3 Update role-specific OpenAPI operation descriptions and backend security regression fixtures for the USER-only contract; verify `mvn test` passes in `backend`.

## 2. Frontend universal capability access

- [x] 2.1 Simplify authentication role state and navigation so authenticated application users see the Projects entry without legacy role checks; verify navigation and authentication tests cover the single-role behavior.
- [x] 2.2 Remove ARCHITECT, APPROVER, and ADMIN UI gating from project, ADL, and ADR pages while retaining state- and ownership-driven action availability; verify component tests cover project administration, authoring, approval/rejection, and normal mutation error feedback for USER.
- [x] 2.3 Update frontend test fixtures to model USER and no-longer-present role restrictions; verify the frontend test command passes.

## 3. Development identity configuration and end-to-end validation

- [x] 3.1 Update the Keycloak realm import so USER is the only application realm role and every seeded interactive account has it; verify the import contains no legacy application-role definition or assignment.
- [x] 3.2 Update the Compose end-to-end scenario to demonstrate one USER performing project, ADL, ADR, decision, and audit operations; verify `scripts/compose-e2e.sh` succeeds against the Compose stack.
- [x] 3.3 Update README and role-oriented user documentation to describe the USER-only access model and its breaking migration requirement; verify documentation no longer advertises ARCHITECT, APPROVER, or ADMIN application access.
