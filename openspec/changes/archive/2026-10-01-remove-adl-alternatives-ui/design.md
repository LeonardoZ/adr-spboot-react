# Design

## Context

The frontend currently receives ADR summaries as part of an ADL detail response and renders them in an Alternatives section. The same frontend already has nested ADR list and creation routes, plus ADR detail and editing routes. See `proposal.md` and the delta specs for the required behavior.

## Goals / Non-Goals

**Goals:**

- Separate ADL context presentation from ADR management while preserving a direct path between them.
- Use consistent Architecture Decision Record terminology across ADR-facing screens.
- Preserve existing active-versus-archived creation eligibility.

**Non-Goals:**

- Changing ADR API contracts, routes, persistence, audit history, or workflow behavior.
- Removing ADR summaries from the ADL detail API response.
- Reworking ADR form fields, decision content, or review actions.

## Decisions

### Replace embedded ADR summary with navigation actions

The ADL detail page will stop rendering its associated ADR collection and will instead link to the existing nested ADR list with a **Manage ADRs** action. Active ADLs retain the existing **Create ADR** link; archived ADLs retain management access but omit creation.

Keeping the embedded summary was rejected because it is the Alternatives UI being removed. Removing the nested ADR list would make retained ADR consultation inaccessible from its ADL context.

### Make terminology a frontend-only change

User-facing ADR headings, breadcrumbs, section titles, empty states, and descriptions will use Architecture Decision Records terminology. Existing ADR field labels such as "Options considered" remain because they describe ADR content rather than navigation or feature naming.

Renaming routes, API fields, or persisted entities was rejected because it would break clients and expand a presentation-only change.

## Risks / Trade-offs

- [Users lose at-a-glance ADR counts and summaries on ADL detail] -> Preserve a clearly labeled Manage ADRs action on every ADL detail page.
- [Archived ADLs could accidentally expose a creation affordance] -> Cover active and archived action visibility in ADL page tests while retaining the existing backend archive guard.
- [Terminology becomes inconsistent across ADR screens] -> Update and test the list, form, and detail views together.

## Migration Plan

1. Deploy the frontend presentation update alongside its tests.
2. No data migration, backend deployment dependency, or API-consumer migration is required.
3. Roll back by redeploying the preceding frontend version if necessary.
