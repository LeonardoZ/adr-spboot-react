# Proposal

## Why

Architecture decisions need a company-wide, traceable place where teams can capture the problem, compare alternatives, and preserve approved decisions. The current requirements describe this process but there is no application that enforces its workflow, access rules, history, or integrity guarantees.

## What Changes

- Introduce a web platform for a company-wide register of Architecture Decision Logs (ADLs) and their Architecture Decision Records (ADRs).
- Authenticate users and authorize actions with Keycloak-managed accounts and roles; enforce authorization in the backend.
- Provide application-managed systems/projects, ADL creation and search, ADR authoring, and the defined review, approval, rejection, cancellation, archival, and supersession workflows.
- Preserve immutable audit events and decision history; prevent physical deletion through normal application flows.
- Establish a Spring Boot/Java 21 API with MariaDB, Hibernate, Flyway migrations, OpenAPI, optimistic locking, and standardized API errors.
- Establish a React/Vite frontend using React Query, Material UI, React Hook Form, and Yup, plus Docker Compose development services for the frontend, API, MariaDB, and Keycloak.

## Capabilities

### New Capabilities

- `identity-access`: Authenticate with Keycloak and enforce role-based access to company-wide register capabilities.
- `project-catalog`: Maintain active systems/projects used to classify ADLs.
- `adl-management`: Create, query, view, update, and archive ADLs.
- `adr-management`: Create, view, and update ADR alternatives attached to ADLs.
- `decision-workflow`: Submit, approve, reject, cancel, and supersede ADR decisions while enforcing lifecycle invariants.
- `audit-history`: Persist and expose immutable, traceable business-event history.

### Modified Capabilities

- None.

## Impact

- Adds a React frontend, Spring Boot backend, database schema and Flyway migrations, Docker Compose development environment, and automated tests.
- Integrates the backend and frontend with Keycloak; the backend uses a service account for authorized user lookup while Keycloak remains the user and role source of truth.
- Adds REST APIs and an OpenAPI contract for the frontend, with MariaDB as persistent storage.
