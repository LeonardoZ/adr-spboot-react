# Design

## Context

See proposal.md for motivation and specs/adl-management/spec.md for the behavior contract. ADL responses currently include a project identifier, ADL create and update require a project, search supports project filtering, and the project catalog has API and UI surfaces. ADRs are stored with a foreign key to their parent ADL and can already be listed by parent.

## Goals / Non-Goals

**Goals:** Remove project classification through the application and persistence layers. Include compact ADR summaries in ADL detail responses and render them on the ADL detail page.

**Non-Goals:** Change ADR lifecycle behavior, the full ADR response, or the existing nested ADR list endpoint.

## Decisions

- Add an ADR summary response type containing identifier, title, status, author display name, and creation time. Load summaries for ADL detail using a focused repository projection/query, avoiding serialization of full ADRs and unnecessary context/decision fields. Paginated ADL search responses need not load ADR summaries.
- Render the summaries in an ADR section on ADL detail, with each summary linking to its full ADR detail. Keep the existing nested ADR list route available.
- Remove the project relationship from the ADL entity and request, response, search, and audit value models. Remove project navigation and management screens and the project API/module once no longer referenced.
- Preserve the existing Flyway migrations and add a new migration that drops the ADL-to-project foreign key and column, then drops project tables. This intentionally discards existing project assignments and project records as requested.

## Risks / Trade-offs

- [Dropping project data is irreversible after the migration is applied.] -> The proposal explicitly requires discarding assignments and project records; document the migration clearly and ensure it targets only the project tables and ADL project reference.
- [ADL detail cost grows with the number of ADRs.] -> Return only compact summary fields and exclude summary loading from paginated ADL search.
- [Existing API clients may send or expect project fields.] -> Treat removal as a breaking API change and update API documentation and first-party clients together.

## Migration Plan

Deploy a new Flyway migration that removes the ADL project foreign key and column and deletes project catalog data and tables. Deploy the application version that no longer reads or writes project data with that migration. Rollback requires restoring the project tables and assignments from a pre-migration backup; the migration itself cannot recover discarded records.
