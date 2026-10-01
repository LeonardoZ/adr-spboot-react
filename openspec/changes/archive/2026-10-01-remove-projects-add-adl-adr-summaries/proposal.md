# Proposal

## Why

ADLs currently depend on project classification and expose ADRs through a separate list, even though an ADL represents the collection of its ADRs. Removing project classification simplifies the register, while showing ADR summaries on ADL detail makes that relationship immediately visible.

## What Changes

- **BREAKING** Remove project as an ADL field, including its required create/update input, search filter, and presentation.
- **BREAKING** Remove project management APIs, navigation, and persisted project data; existing project assignments are discarded.
- Include the ADL's zero or more ADR summaries in its detail response and show them on the ADL detail page, linking each summary to the complete ADR.
- Preserve the dedicated ADR list endpoint and full ADR detail behavior.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `adl-management`: Remove project classification and include ADR summaries when viewing an ADL.
- `project-catalog`: Remove project management and project reference preservation requirements.

## Impact

The change affects ADL persistence and migration, ADL request/response/search APIs, project catalog APIs and UI, ADL forms/list/detail views, related tests and end-to-end fixtures, and the two existing capability specs. Existing project records and ADL assignments will be discarded.
