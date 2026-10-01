# Spec Delta

## Purpose

Enforce the review and decision lifecycle for ADR alternatives and their parent ADLs.

## ADDED Requirements

### Requirement: Submit and cancel ADR review
The system SHALL allow an authorized author or architect to move a complete DRAFT ADR to UNDER_REVIEW and to return an UNDER_REVIEW ADR to DRAFT, provided its parent ADL is not archived.

#### Scenario: Submit complete draft
- **WHEN** an authorized user submits a complete DRAFT ADR
- **THEN** the ADR becomes UNDER_REVIEW, submission metadata is recorded, and the parent ADL becomes IN_ANALYSIS when it was OPEN

#### Scenario: Submit incomplete draft
- **WHEN** an ADR lacking required review content is submitted
- **THEN** the system rejects the transition and leaves it DRAFT

### Requirement: Approve or reject ADRs
The system SHALL allow any APPROVER to approve or reject an UNDER_REVIEW ADR, requiring a rejection justification and recording decision actor, time, and comment.

#### Scenario: Approve ADR
- **WHEN** an APPROVER approves an UNDER_REVIEW ADR
- **THEN** the ADR becomes APPROVED and its parent ADL becomes DECIDED in the same operation

#### Scenario: Reject ADR
- **WHEN** an APPROVER rejects an UNDER_REVIEW ADR with a justification
- **THEN** the ADR becomes REJECTED and the decision metadata is retained

### Requirement: Preserve multiple ADR decisions
The system SHALL allow an ADL to retain multiple ADRs in APPROVED or REJECTED status, each with its own immutable decision metadata.

#### Scenario: Additional approved ADR
- **WHEN** an APPROVER approves an UNDER_REVIEW ADR whose ADL already has approved ADRs
- **THEN** the new ADR becomes APPROVED and every existing ADR decision remains preserved

### Requirement: Supersede an approved decision
The system SHALL allow an authorized architect to supersede an APPROVED ADR only with a different approved replacement ADR from the same ADL, retaining the replacement reference and preventing supersession cycles.

#### Scenario: Supersede approved ADR
- **WHEN** an authorized architect supplies a distinct approved replacement ADR for an approved ADR
- **THEN** the prior ADR becomes SUPERSEDED and references the replacement
