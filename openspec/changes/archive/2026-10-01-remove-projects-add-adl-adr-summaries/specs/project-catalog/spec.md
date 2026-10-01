# Spec Delta

## REMOVED Requirements

### Requirement: Manage projects
**Reason**: Project classification is no longer part of the decision register.
**Migration**: Remove project management APIs and user interface. Existing project records are discarded.

### Requirement: Preserve referenced projects
**Reason**: ADLs no longer reference projects.
**Migration**: Discard all existing ADL project assignments and remove project references and project data from persistence.
