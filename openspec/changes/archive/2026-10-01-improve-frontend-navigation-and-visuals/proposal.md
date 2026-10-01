# Proposal

## Why

The frontend exposes the decision register's core functions but presents them as disconnected, text-heavy screens. Users lose their place while moving between logs and decision records, and the interface does not make a record's lifecycle or next meaningful action easy to understand.

## What Changes

- Add a lightweight authenticated home dashboard with a direct route into the ADL register, a primary create-ADL action, a concise register summary, and recently created ADLs.
- Establish clear global and contextual navigation with active top-level destinations and breadcrumbs that preserve the relationship from an ADR back to its ADL.
- Present ADL and ADR details as scannable decision-workspace views, including lifecycle status, key facts, grouped content, and context-aware workflow actions.
- Apply a cohesive warm, collaborative visual system across the authenticated frontend, including responsive layouts, accessible status treatment, and improved loading, empty, and error states.
- Present audit events as a readable timeline within their existing ADL and ADR contexts; do not add a global audit destination.

## Capabilities

### New Capabilities
- `frontend-decision-workspace`: Provide lightweight register entry, contextual navigation, and a collaborative, lifecycle-aware interface for ADL and ADR work.

### Modified Capabilities

- None.

## Impact

- Affected code: `frontend/src/App.tsx`, `frontend/src/main.tsx`, authentication navigation, ADL/ADR pages, audit history, and their frontend tests.
- The existing ADL, ADR, audit, and authentication APIs remain unchanged; the dashboard will use the existing ADL list endpoint.
- No new runtime dependencies or backend/database changes are expected.
