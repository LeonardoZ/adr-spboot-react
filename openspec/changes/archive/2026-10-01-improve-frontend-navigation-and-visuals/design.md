# Design

## Context

The React SPA uses React Router, Material UI, React Query, and feature-local ADL, ADR, and audit components. Its authenticated shell currently has a single text navigation item, while the home route is only a heading. ADL and ADR data already supports cards, status, parent ADR relationships, and existing workflow controls; the ADL list endpoint supports a total count and descending creation-date sort. See `proposal.md` for motivation and the delta spec for required behavior.

## Goals / Non-Goals

**Goals:**

- Establish a reusable authenticated application shell with global navigation, active-route treatment, and contextual breadcrumbs.
- Make register entry, record state, next actions, and traceability immediately legible.
- Apply a warm, collaborative visual system without changing API contracts or workflow rules.
- Keep the landing page deliberately lightweight and based on existing ADL list data.

**Non-Goals:**

- Adding a global audit page, cross-register activity feed, notifications, roles, or analytics.
- Changing ADL/ADR data models, APIs, backend sorting, or authorization behavior.
- Introducing a new component library, image assets, or runtime dependencies.

## Decisions

### Reusable workspace primitives

Create small frontend presentation primitives for page headers, status chips, breadcrumbs, content sections, empty states, and an audit timeline. Feature pages compose these primitives rather than each creating a competing visual language.

Material UI is retained because it is already the application component system. A theme configured at the application root will express the warm surface palette, accessible semantic status colors, type scale, shape, and component defaults. CSS-only styling was considered, but a theme and shared primitives prevent duplicated per-page styling and keep keyboard and responsive behavior aligned.

### Navigation model

The authenticated app shell owns global Home and Decision Logs navigation, with route-aware active treatment. ADL and ADR pages supply breadcrumb segments from route parameters and loaded record data. ADR breadcrumbs always retain a link to the parent ADL and register, avoiding dead-end detail views.

Audit remains embedded in the record because its API and value are entity-specific. A global audit navigation destination was rejected because it conflicts with the agreed lightweight workspace and would require a broader query and filtering model.

### Lightweight dashboard data

The home view will request the existing ADL list with a small page size and its supported creation-date descending sort. It will use the returned total for the summary and a bounded subset for recent links. The dashboard will not infer workflow metrics by fetching every ADL or ADR.

Using existing register data avoids API work and makes the empty state truthful. "Recently created" is used instead of "recently updated" because the current response exposes the former ordering but not update timestamps.

### Record presentation and actions

ADL and ADR details will use grouped content surfaces: overview, decision content, alternatives, available actions, and history. Status chips include text in addition to semantic color. Primary action placement follows current state, while existing mutation eligibility and backend validation remain authoritative.

Tables will remain useful on wide screens, with compact stacked/card presentation or overflow containment on narrow screens. Navigation and action groups wrap or collapse appropriately so page-level horizontal scrolling is avoided.

## Risks / Trade-offs

- [More visual components increase presentation code] -> Keep primitives narrow, feature-agnostic, and covered by component/page tests.
- [Route parameters alone cannot provide a human-readable parent ADL title] -> Render the identifier immediately and enhance the breadcrumb once the ADL data is available; retain functional links throughout.
- [MUI color choices can reduce status distinction] -> Use semantic chip text, theme contrast checks, and tests that assert visible status names and action availability.
- [Responsive alternatives to tables can diverge] -> Use one underlying data source and test both core content and links rather than duplicating feature logic.

## Migration Plan

1. Deploy as a frontend-only replacement using the existing API contracts.
2. Verify authenticated navigation, creation, review actions, and detail routes against the current backend.
3. Roll back by deploying the prior frontend bundle; no persisted data or API migration is involved.
