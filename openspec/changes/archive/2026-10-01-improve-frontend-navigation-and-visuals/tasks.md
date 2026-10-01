# Tasks

## 1. Establish the workspace foundation

- [x] 1.1 Define the warm, collaborative Material UI theme and reusable workspace presentation primitives (page header, content section, status chip, and empty state), then verify their accessible text and theme rendering with focused frontend tests.
- [x] 1.2 Extend authenticated navigation with Home and Decision Logs active-route treatment, then verify active labels, keyboard-operable links, and existing anonymous return-path behavior in navigation and application-shell tests.
- [x] 1.3 Add reusable contextual breadcrumb support for ADL and ADR routes, then verify breadcrumb links resolve to the register and parent ADL routes in page tests.

## 2. Deliver lightweight register entry

- [x] 2.1 Implement the authenticated home dashboard using the existing ADL list endpoint with bounded creation-date-descending results, then verify the summary, recent ADL links, register link, and create-ADL action with mocked query tests.
- [x] 2.2 Implement the no-ADL dashboard state and loading/error presentation, then verify it remains actionable and does not show a false failure state in frontend tests.
- [x] 2.3 Refresh the ADL register header, search, empty state, and list presentation with the shared workspace primitives, then verify search and existing ADL navigation continue to work in ADL page tests.

## 3. Make record views lifecycle-aware

- [x] 3.1 Recompose ADL detail and form views into labeled, scannable workspace sections with status and contextual actions, then verify active, archived, and ADR-summary flows in ADL page tests.
- [x] 3.2 Recompose ADR list, detail, and form views with parent context, textual status chips, grouped decision content, and state-appropriate action emphasis, then verify draft, under-review, and terminal-record behavior in ADR page tests.
- [x] 3.3 Render contextual audit events as a readable timeline while retaining the existing empty and error states, then verify action, actor, timestamp, and empty-history output in audit-history tests.
- [x] 3.4 Apply responsive layout rules to navigation, action groups, tables, and content sections, then verify narrow-viewport layouts preserve core content and avoid page-level horizontal overflow with frontend component or browser-level tests.

## 4. Verify the frontend integration

- [x] 4.1 Run `npm run test` from `frontend` and fix any regressions in the navigation, dashboard, ADL, ADR, or audit workflows.
- [x] 4.2 Run `npm run build` from `frontend` and verify the production bundle compiles without new dependencies or backend/API changes.
