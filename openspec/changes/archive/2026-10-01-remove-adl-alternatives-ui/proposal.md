# Proposal

## Why

The ADL workspace currently presents its associated ADRs as "Alternatives," which obscures their role as durable Architecture Decision Records. The application should use neutral ADR terminology while preserving the existing ADR capability and workflow.

## What Changes

- Remove the Alternatives summary, count, empty state, and "View alternatives" wording from the ADL detail UI.
- Retain ADR creation, consultation, editing, workflow, audit history, APIs, routes, and persisted data.
- Provide a neutral **Manage ADRs** entry point on every ADL detail page.
- Provide **Create ADR** on active ADL detail pages only; archived ADLs remain read-only and do not expose ADR creation.
- Rename ADR list and form presentation from "Alternatives" / "Decision alternative" to Architecture Decision Records terminology.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `adl-management`: ADL detail navigation and presentation no longer expose associated ADRs as alternatives, while retaining neutral ADR management and active-ADL creation entry points.
- `adr-management`: ADR user-facing terminology is presented as Architecture Decision Records rather than alternatives.

## Impact

- Frontend ADL detail, ADR list/form/detail views, associated routing affordances, and frontend tests.
- Existing backend contracts, database schema, ADR persistence, and ADR workflow remain unchanged.
