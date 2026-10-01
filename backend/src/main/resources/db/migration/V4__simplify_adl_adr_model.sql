UPDATE adls
SET archived_at = updated_at
WHERE status = 'ARCHIVED' AND archived_at IS NULL;

UPDATE adrs
SET status = 'APPROVED'
WHERE status = 'SUPERSEDED';

ALTER TABLE adrs DROP CONSTRAINT IF EXISTS fk_adrs_superseded_by;
ALTER TABLE adrs DROP COLUMN IF EXISTS superseded_by_adr_id;

ALTER TABLE adls DROP COLUMN IF EXISTS status;
ALTER TABLE adls DROP COLUMN IF EXISTS responsible_user_id;
ALTER TABLE adls DROP COLUMN IF EXISTS responsible_display_name;
