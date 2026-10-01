# Spec Delta

## ADDED Requirements

### Requirement: Access ADRs from ADL detail
The system SHALL present a neutral ADR-management action on every ADL detail page. It SHALL present an ADR-creation action only while the ADL is active, and SHALL not render the related ADR summary as an Alternatives section on that page.

#### Scenario: Active ADL ADR actions
- **WHEN** an authorized user views an active ADL
- **THEN** the page provides Manage ADRs and Create ADR actions for that ADL without an Alternatives summary section

#### Scenario: Archived ADL ADR access
- **WHEN** an authenticated user views an archived ADL
- **THEN** the page provides Manage ADRs for consultation, omits Create ADR, and does not render an Alternatives summary section
