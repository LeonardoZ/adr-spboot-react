# decision-workflow Specification

## Purpose
Enforce the review and decision lifecycle for ADR alternatives and their parent ADLs.

## Requirements

### Requirement: Submit and cancel ADR review
The system SHALL allow an authorized author or architect to move a complete DRAFT ADR to UNDER_REVIEW and to return an UNDER_REVIEW ADR to DRAFT, provided its parent ADL is not archived. These transitions SHALL not change the parent ADL's state.

#### Scenario: Submit complete draft
- **WHEN** an authorized user submits a complete DRAFT ADR
- **THEN** the ADR becomes UNDER_REVIEW and submission metadata is recorded

#### Scenario: Submit incomplete draft
- **WHEN** an ADR lacking required review content is submitted
- **THEN** the system rejects the transition and leaves it DRAFT

### Requirement: Approve or reject ADRs
The system SHALL allow any APPROVER to approve or reject an UNDER_REVIEW ADR, requiring a rejection justification and recording decision actor, time, and comment. Approval or rejection SHALL not change the parent ADL's state.

#### Scenario: Approve ADR
- **WHEN** an APPROVER approves an UNDER_REVIEW ADR
- **THEN** the ADR becomes APPROVED and its decision metadata is recorded

#### Scenario: Reject ADR
- **WHEN** an APPROVER rejects an UNDER_REVIEW ADR with a justification
- **THEN** the ADR becomes REJECTED and the decision metadata is retained

### Requirement: Preserve multiple ADR decisions
The system SHALL allow an ADL to retain multiple ADRs in APPROVED or REJECTED status, each with its own immutable decision metadata.

#### Scenario: Additional approved ADR
- **WHEN** an APPROVER approves an UNDER_REVIEW ADR whose ADL already has approved ADRs
- **THEN** the new ADR becomes APPROVED and every existing ADR decision remains preserved
