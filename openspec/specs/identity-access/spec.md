# identity-access Specification

## Purpose
Secure access to the decision register with Keycloak identities and role-based authorization.

## Requirements

### Requirement: Authenticated access
The system SHALL require a valid Keycloak-issued access token for protected API and frontend routes, and SHALL identify the caller by the Keycloak subject identifier.

#### Scenario: Missing or invalid token
- **WHEN** a caller requests a protected API without a valid access token
- **THEN** the system denies access without returning protected data

### Requirement: Role-based authorization
The system SHALL enforce authorization in the backend using `USER` as its sole recognized Keycloak role. An authenticated caller with `USER` SHALL be allowed to consult the register, manage projects, author ADLs and ADRs, perform ADR workflow actions, decide ADRs under review, and read audit history.

#### Scenario: Authenticated user performs any protected action
- **WHEN** a caller with a valid access token containing the `USER` role invokes any protected application action
- **THEN** the system authorizes the request subject to the action's non-authorization validation and workflow rules

#### Scenario: Unauthorized backend action
- **WHEN** a caller presents a valid access token that does not contain the `USER` role
- **THEN** the system rejects every protected application action even if the frontend exposed it

### Requirement: Active user validation
The system SHALL validate an ADL responsible against an active Keycloak account and SHALL retain the Keycloak identifier and a display-name snapshot for business and audit records.

#### Scenario: Inactive responsible user
- **WHEN** an ADL is created or reassigned to an inactive or unknown Keycloak user
- **THEN** the system rejects the request
