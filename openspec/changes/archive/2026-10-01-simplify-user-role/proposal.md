# Proposal

## Why

The current four-role authorization model makes ordinary decision-register work depend on separate role assignments. The product now requires a single authenticated-user role so every registered user can access and operate the register.

## What Changes

- **BREAKING** Replace the `USER`, `ARCHITECT`, `APPROVER`, and `ADMIN` authorization model with `USER` as the only recognized application role.
- Grant every authenticated `USER` access to all register capabilities: project management, ADL and ADR authoring, workflow actions, ADR decisions, audit history, and read access.
- Continue enforcing authentication in the frontend and backend, as well as existing workflow, ownership, validation, and concurrency rules that are unrelated to roles.
- Simplify the development Keycloak realm, client role mapping, navigation, and authorization regression coverage to the single-role model.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `identity-access`: Replace differentiated role-based authorization with universal access for authenticated users carrying the sole `USER` role.

## Impact

- Backend Spring Security role mapping and method-level authorization on project, ADL, ADR, and audit APIs.
- ADR workflow controller logic that currently distinguishes architects from other users.
- Frontend role extraction, navigation, route/page action visibility, and role-focused tests.
- Development Keycloak realm roles and seeded users, Compose end-to-end scenario, API security tests, and user-facing OpenAPI descriptions.
