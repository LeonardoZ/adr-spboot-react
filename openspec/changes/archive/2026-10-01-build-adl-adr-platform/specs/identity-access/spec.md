# Spec Delta

## Purpose

Secure access to the decision register with Keycloak identities and role-based authorization.

## ADDED Requirements

### Requirement: Authenticated access
The system SHALL require a valid Keycloak-issued access token for protected API and frontend routes, and SHALL identify the caller by the Keycloak subject identifier.

#### Scenario: Missing or invalid token
- **WHEN** a caller requests a protected API without a valid access token
- **THEN** the system denies access without returning protected data

### Requirement: Role-based authorization
The system SHALL enforce authorization in the backend from recognized Keycloak roles: USER may consult the register, ARCHITECT may author ADLs and ADRs, APPROVER may decide ADRs under review, and ADMIN may manage projects.

#### Scenario: Unauthorized backend action
- **WHEN** a caller without the required role invokes a protected action
- **THEN** the system rejects the action even if the frontend exposed it

### Requirement: Active user validation
The system SHALL validate an ADL responsible against an active Keycloak account and SHALL retain the Keycloak identifier and a display-name snapshot for business and audit records.

#### Scenario: Inactive responsible user
- **WHEN** an ADL is created or reassigned to an inactive or unknown Keycloak user
- **THEN** the system rejects the request
