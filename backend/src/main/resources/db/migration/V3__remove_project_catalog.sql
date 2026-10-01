DELETE FROM audit_events WHERE entity_type = 'PROJECT';
ALTER TABLE adls DROP CONSTRAINT fk_adls_project;
DROP INDEX ix_adls_project_status ON adls;
ALTER TABLE adls DROP COLUMN project_id;
DROP TABLE projects;
