# Spec Delta

## Purpose

Maintain the systems and projects that classify architectural decision logs.

## ADDED Requirements

### Requirement: Manage projects
The system SHALL allow ADMIN users to create, view, update, and deactivate systems/projects used by ADLs.

#### Scenario: Administrator creates a project
- **WHEN** an ADMIN submits valid project details
- **THEN** the project becomes available for ADL classification

### Requirement: Preserve referenced projects
The system SHALL prevent an inactive project from being selected for new or updated ADLs while preserving it and its historical ADL references for consultation.

#### Scenario: Deactivated project on an existing ADL
- **WHEN** an ADMIN deactivates a project already referenced by an ADL
- **THEN** the existing ADL remains consultable with its project reference
