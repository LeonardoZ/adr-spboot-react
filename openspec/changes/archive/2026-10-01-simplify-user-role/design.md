# Design

## Context

See `proposal.md` for the motivation and the identity-access delta for the behavior contract. The API currently maps four realm/client roles and uses method-level annotations to distinguish users, architects, approvers, and administrators. The SPA repeats those role checks to hide navigation, forms, and ADR actions. The development realm and security tests seed the same role matrix.

## Goals / Non-Goals

**Goals:**

- Make `USER` the only application role recognized from Keycloak claims.
- Allow that role to reach every application capability while preserving the existing state-machine, optimistic-lock, validation, and responsible-user checks.
- Keep backend authorization authoritative and keep the SPA free of obsolete role-based visibility logic.
- Make local Keycloak and automated security coverage represent the production authorization contract.

**Non-Goals:**

- Change the OIDC flow, token issuer, Keycloak user lifecycle, or service-account lookup permissions.
- Relax record ownership or lifecycle constraints that are not role checks; in particular, retain the existing author-based restrictions where the workflow defines them.
- Migrate existing production realm assignments automatically. Administrators must assign `USER` before deploying this version.

## Decisions

### Recognize and require only `USER`

The JWT authority converter and API-level protection will accept only `ROLE_USER`; every protected endpoint will require that authority. Method-level endpoint restrictions will use the same role or rely on the API-level rule where no more specific condition is needed.

This preserves the current deny-by-default behavior for tokens without an application role. Accepting any valid Keycloak token was rejected because it would grant register access to identities outside the application population.

### Remove role differentiation from feature actions

Project management, ADL/ADR create and update, approval/rejection, supersession, audit reads, and their SPA controls will be available to authenticated users. ADR workflow service calls that currently receive an architect flag will be simplified so they retain only the applicable actor/ownership checks.

Keeping the four roles and assigning all of them to every user was rejected: it retains needless configuration, encourages future divergent checks, and fails the required single-role contract.

### Simplify client authorization state

The SPA will no longer expose a multi-role union or condition navigation and controls on individual roles. Its existing authenticated-route protection stays in place; API responses remain the final authorization enforcement. The Projects entry and all user actions become visible to an authenticated application user.

### Align realm fixtures and tests

The development realm will declare only `USER`, and each interactive seeded account will receive it. API, frontend, and Compose tests will use `USER` tokens for successful actions and a token without `USER` for denial coverage.

## Risks / Trade-offs

- [Previously valid ARCHITECT, APPROVER, or ADMIN tokens lose access] -> Assign `USER` to every intended application account before the API is upgraded, and deploy the realm change with that assignment.
- [A missed UI condition leaves a capability hidden] -> Search for legacy role names and cover navigation, project administration, authoring, and decisions with frontend tests.
- [A missed endpoint annotation leaves an action unavailable or overexposed] -> Replace the role-matrix regression suite with endpoint-level tests proving USER succeeds and non-USER fails for every protected action.
- [Role removal accidentally weakens workflow rules] -> Preserve existing service-level state, actor, version, and ownership tests alongside authorization test updates.

## Migration Plan

1. Update Keycloak realm configuration so all interactive accounts intended to use the register receive `USER`; remove legacy application role definitions and mappings.
2. Release the API and SPA together with the single-role checks and UI behavior.
3. Verify a USER can perform each protected action and a valid token without USER receives the standard authorization problem response.
4. Roll back by restoring the prior realm import and application release. No database migration is required.
