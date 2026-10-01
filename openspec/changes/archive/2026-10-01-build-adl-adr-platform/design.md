# Design

## Context

This is a greenfield implementation of the behavior in the proposal and capability specs. It requires a browser application, protected API, durable relational data, identity integration, lifecycle invariants, and an easy local development environment.

## Goals / Non-Goals

**Goals:**

- Deliver a maintainable React and Spring Boot modular monolith with a documented REST contract.
- Enforce authorization, state transitions, auditing, and concurrency in the backend.
- Keep Keycloak authoritative for users and roles while preserving readable business history.
- Make a complete development stack runnable with Docker Compose.

**Non-Goals:**

- User-account administration or custom credential storage.
- Project-level visibility restrictions; the register is company-wide for authenticated users.
- Physical deletion, real-time collaboration, notifications, or an external project catalog integration.

## Decisions

### Service and web architecture

Build a Spring Boot 3.x (Java 21) REST modular monolith backed by MariaDB, with a separate React/Vite single-page application. Group backend code by capability (identity, catalog, ADL, ADR, workflow, audit), keeping HTTP controllers, application services, and persistence concerns separated. Publish OpenAPI and use it as the frontend/API contract.

This avoids distributed-service operational overhead while maintaining clear boundaries. Microservices were rejected because the initial product needs transactional workflow invariants more than independent deployment.

### Authentication and authorization

Use Keycloak OIDC authorization-code flow with PKCE in the SPA and Spring Security OAuth2 Resource Server JWT validation in the API. Map only recognized realm or client roles to application authorities. The API uses a least-privilege Keycloak service account for responsible-user search/active validation; the SPA never uses Keycloak administrative APIs.

Store `keycloak_user_id` plus a display-name snapshot in business and audit records. This preserves historical readability without duplicating account ownership. Local user tables were rejected because Keycloak is the source of truth.

### Persistence, migration, and integrity

Use Hibernate/JPA for persistence and Flyway as the only schema-change mechanism; Hibernate validates rather than creates schemas outside local ephemeral tests. Core entities include Project, ADL, ADR, and AuditEvent. ADL and ADR have numeric optimistic-lock versions. No normal-flow delete endpoint exists.

Use foreign keys and indexes for relationships/query filters and unique generated business identifiers. An ADL may retain multiple APPROVED and REJECTED ADRs. Approval, rejection, archival, supersession, and corresponding audit writes execute inside a single transaction; concurrent independent approvals preserve every ADR decision while making the parent ADL's DECIDED transition idempotent. Application service validation remains necessary for clear errors and state-machine checks.

### API and error contract

Expose resource APIs for projects, ADLs, nested ADR lists, ADR actions, responsible-user lookup, and audit history. Use pagination and an explicit allowlist for sortable fields. Send the version on mutable resources and require it on updates/actions; return `409 Conflict` for stale version or decision conflicts. Return a stable problem response with a machine-readable code and field errors for `400`, `401`, `403`, `404`, and `409` conditions.

### Frontend state and forms

Use React Query for server-state fetching, mutation invalidation, and error handling; Material UI for the component system; React Hook Form with Yup schemas for client-side feedback. The API remains the final validator. Route guards and disabled actions improve UX but do not replace backend authorization.

### Development environment

Provide Dockerfiles for frontend and backend plus Docker Compose services for MariaDB, Keycloak, API, and SPA. Seed a development-only Keycloak realm, client, roles, and test users through imported configuration. Credentials and production URLs remain environment variables and are not committed as production secrets.

## Risks / Trade-offs

- [Keycloak role/claim differences across environments] → Document expected client/realm role mapping and validate startup configuration.
- [Keycloak user lookup adds availability coupling] → Use it only for responsible selection/validation; token validation remains local through issuer keys.
- [Concurrent approval requests] → Combine transaction boundaries and optimistic locking so independent ADR decisions are retained while the parent ADL transition remains idempotent.
- [Text search performance on large registers] → Start with indexed relational filtering and revisit full-text search only when measured demand warrants it.
- [Development identity setup drift] → Version the development realm import beside Compose configuration.

## Migration Plan

1. Provision MariaDB and Keycloak configuration, then deploy the API with Flyway migrations enabled.
2. Validate the API health endpoint, JWT role mapping, and Keycloak service-account permissions before exposing the SPA.
3. Deploy the SPA with the API and Keycloak public configuration.
4. Roll back application releases while retaining forward-compatible Flyway migrations; use a compensating migration rather than modifying applied migrations.
