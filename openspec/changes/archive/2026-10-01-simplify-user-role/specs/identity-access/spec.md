# Spec Delta

## MODIFIED Requirements

### Requirement: Role-based authorization
The system SHALL enforce authorization in the backend using `USER` as its sole recognized Keycloak role. An authenticated caller with `USER` SHALL be allowed to consult the register, manage projects, author ADLs and ADRs, perform ADR workflow actions, decide ADRs under review, and read audit history.

#### Scenario: Authenticated user performs any protected action
- **WHEN** a caller with a valid access token containing the `USER` role invokes any protected application action
- **THEN** the system authorizes the request subject to the action's non-authorization validation and workflow rules

#### Scenario: Unauthorized backend action
- **WHEN** a caller presents a valid access token that does not contain the `USER` role
- **THEN** the system rejects every protected application action even if the frontend exposed it
