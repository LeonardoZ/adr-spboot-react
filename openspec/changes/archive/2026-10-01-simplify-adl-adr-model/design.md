# Design

## Context

The current REST API, JPA model, Flyway schema, React API types, and views expose ADL responsibility and lifecycle status. ADR workflow operations update parent ADL status, and ADR supersession is persisted as a self-reference and exposed through a dedicated endpoint and UI. See `proposal.md` for the motivation and the delta specs for the required behavior.

The database is managed with Flyway and existing installations may contain ADLs and ADRs using the fields being removed. The archive timestamp already exists and is the durable replacement for ADL archival state.

## Goals / Non-Goals

**Goals:**

- Make archival an ADL property derived from `archivedAt`, without an ADL status enum or field.
- Make ADR status the only workflow state and retain archive guards for ADR creation and transitions.
- Remove the full supersession contract and storage relation.
- Provide a direct, context-preserving ADR creation entry point on active ADL details.
- Migrate existing records without deleting ADLs, ADRs, tags, or audit history.

**Non-Goals:**

- Introducing a separate responsible-user field on ADRs.
- Replacing supersession with another ADR-to-ADR relationship.
- Changing ADR authorship, review permissions, approval/rejection rules, or audit-history retention.
- Adding an ADL unarchive operation.

## Decisions

### Derive ADL archival state from `archivedAt`

The persistence model and API will expose archival as an archive timestamp and/or derived boolean rather than a status enum. Services will use a single archived predicate for edit, ADR creation, submission, cancellation, approval, rejection, and detail-page affordances.

This keeps the existing archive behavior while eliminating lifecycle states whose meanings duplicate ADR workflow. Retaining an ADL status with only `ARCHIVED` was rejected because it keeps status as an ADL concern and creates two sources of truth.

### Remove ADL responsibility at every boundary

The ADL responsible-user ID and display name will be removed from the Flyway-managed table, entity, requests, responses, search criteria, audit value maps, OpenAPI parameters, frontend types, forms, and tests. ADL create/update no longer call active-user lookup solely to validate a responsible user.

Leaving deprecated nullable fields or silently accepting responsibility in requests was rejected because the requested API model is intentionally simplified and stale fields would keep the obsolete contract alive.

### Eliminate supersession rather than hide it

The ADR status enum will contain DRAFT, UNDER_REVIEW, APPROVED, and REJECTED only. The replacement column and self-referential foreign key, service/controller operation, response field, client method, UI controls, and supersession-specific tests will be removed. Existing ADR rows that are SUPERSEDED must be migrated to APPROVED before the enum/value is no longer supported, preserving their decision record even though their former replacement linkage is discarded.

Keeping SUPERSEDED data as a legacy read-only state was rejected because it would retain a decision state the new model explicitly removes.

### Use a forward, data-preserving Flyway migration

A new migration will first preserve archive semantics by setting `archived_at` for any legacy ARCHIVED ADL lacking it, and normalize legacy SUPERSEDED ADRs to APPROVED. It will then remove the ADL status and responsible-user columns plus their index, and remove the ADR supersession column and foreign key. Previous migration files remain immutable.

The migration removes obsolete field values and replacement links. Database backup remains the rollback mechanism after deployment; application rollback after the migration requires a compatible version that no longer depends on the removed columns.

### Reuse the existing ADR create route from ADL detail

The active ADL detail view will link to the existing nested ADR creation route. The control is absent for archived ADLs; the backend continues to enforce the same guard so a manually constructed request cannot bypass it. This avoids adding a second creation API or form.

## Risks / Trade-offs

- [Clients still send removed fields or call supersession] → Publish the breaking API contract and update repository tests, E2E script, and frontend in the same release.
- [Legacy archived rows lack an archive timestamp] → Backfill `archivedAt` from the existing update timestamp before removing ADL status.
- [Legacy superseded rows lose replacement context] → Normalize them to APPROVED and retain all non-supersession ADR and audit records; take a database backup before migration.
- [A client bypasses the detail-page UI] → Keep backend archive checks as the authoritative enforcement.

## Migration Plan

1. Back up the database and deploy the application release containing the new forward Flyway migration.
2. Backfill missing archive timestamps for legacy ARCHIVED ADLs and normalize legacy SUPERSEDED ADRs to APPROVED.
3. Drop obsolete indexes, foreign keys, and columns after normalization.
4. Deploy the backend and frontend contract changes together, then run API, migration, frontend, and compose E2E verification.
5. If deployment fails before migration completion, roll back normally. After destructive columns are dropped, restore the pre-migration backup to recover the old contract.
