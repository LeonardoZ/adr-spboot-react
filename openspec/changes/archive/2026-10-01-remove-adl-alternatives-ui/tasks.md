# Tasks

## 1. Update ADL detail ADR access

- [x] 1.1 Remove the ADL detail Alternatives summary, count, empty state, and linked ADR rows; add a Manage ADRs action for both active and archived ADLs, and verify it routes to the existing nested ADR list.
- [x] 1.2 Retain Create ADR only on active ADL details and update ADL view tests to verify active and archived action visibility plus the absence of an Alternatives section.

## 2. Neutralize ADR presentation terminology

- [x] 2.1 Update ADR list breadcrumbs, headings, section titles, and empty-state copy to use Architecture Decision Records terminology while retaining the existing list and creation routes; verify ADR view tests cover the renamed empty state and Create ADR action.
- [x] 2.2 Update ADR creation, editing, and detail page labels and descriptions to remove alternative terminology without changing input fields or workflow actions; verify existing ADR form and workflow tests continue to pass.
- [x] 2.3 Update user-facing decision-register copy that still describes ADRs as alternatives, and verify the frontend source has no remaining Alternatives or Decision alternative UI labels outside intentionally retained ADR data-field wording.

## 3. Verify retained ADR behavior

- [x] 3.1 Run the frontend test suite with `npm test` in `frontend` and verify ADR and ADL UI tests pass.
- [x] 3.2 Run `npm run build` in `frontend` and verify the production bundle compiles without route or type errors.
