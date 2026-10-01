# Spec Delta

## ADDED Requirements

### Requirement: Present ADRs with neutral terminology
The system SHALL present ADR list, creation, editing, and detail interfaces as Architecture Decision Records and SHALL not label them as alternatives.

#### Scenario: Browse ADRs for an ADL
- **WHEN** an authenticated user opens the ADR management page for an ADL
- **THEN** the page, breadcrumb, and empty state use Architecture Decision Records terminology and retain the existing ADR list and creation capability

#### Scenario: Create or consult an ADR
- **WHEN** an authorized user opens an ADR creation, editing, or detail interface
- **THEN** the interface uses Architecture Decision Record terminology while retaining its existing fields and workflow actions
