CREATE INDEX ix_adls_project_status ON adls (project_id, status);
CREATE INDEX ix_adls_responsible_user ON adls (responsible_user_id);
CREATE INDEX ix_adls_updated_at ON adls (updated_at);
CREATE INDEX ix_adl_tags_tag ON adl_tags (tag);
CREATE INDEX ix_adrs_adl_status ON adrs (adl_id, status);
CREATE INDEX ix_audit_events_entity_time ON audit_events (entity_identifier, occurred_at);
