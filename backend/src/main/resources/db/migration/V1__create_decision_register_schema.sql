CREATE TABLE projects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    business_identifier VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_projects_business_identifier UNIQUE (business_identifier)
);

CREATE TABLE adls (
    id BIGINT NOT NULL AUTO_INCREMENT,
    business_identifier VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    context TEXT NOT NULL,
    problem TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    project_id BIGINT NOT NULL,
    responsible_user_id VARCHAR(128) NOT NULL,
    responsible_display_name VARCHAR(255) NOT NULL,
    created_by_user_id VARCHAR(128) NOT NULL,
    created_by_display_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_by_user_id VARCHAR(128) NULL,
    updated_by_display_name VARCHAR(255) NULL,
    updated_at TIMESTAMP NOT NULL,
    archived_at TIMESTAMP NULL,
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_adls_business_identifier UNIQUE (business_identifier),
    CONSTRAINT fk_adls_project FOREIGN KEY (project_id) REFERENCES projects (id)
);

CREATE TABLE adl_tags (
    adl_id BIGINT NOT NULL,
    tag VARCHAR(64) NOT NULL,
    PRIMARY KEY (adl_id, tag),
    CONSTRAINT fk_adl_tags_adl FOREIGN KEY (adl_id) REFERENCES adls (id)
);

CREATE TABLE adrs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    business_identifier VARCHAR(64) NOT NULL,
    adl_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    context TEXT NOT NULL,
    problem TEXT NOT NULL,
    options_considered TEXT NULL,
    decision TEXT NULL,
    consequences TEXT NULL,
    status VARCHAR(32) NOT NULL,
    author_user_id VARCHAR(128) NOT NULL,
    author_display_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_by_user_id VARCHAR(128) NULL,
    updated_by_display_name VARCHAR(255) NULL,
    updated_at TIMESTAMP NOT NULL,
    submitted_by_user_id VARCHAR(128) NULL,
    submitted_at TIMESTAMP NULL,
    decided_by_user_id VARCHAR(128) NULL,
    decided_by_display_name VARCHAR(255) NULL,
    decided_at TIMESTAMP NULL,
    decision_comment TEXT NULL,
    rejection_justification TEXT NULL,
    superseded_by_adr_id BIGINT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_adrs_business_identifier UNIQUE (business_identifier),
    CONSTRAINT fk_adrs_adl FOREIGN KEY (adl_id) REFERENCES adls (id),
    CONSTRAINT fk_adrs_superseded_by FOREIGN KEY (superseded_by_adr_id) REFERENCES adrs (id)
);

CREATE TABLE audit_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    entity_type VARCHAR(32) NOT NULL,
    entity_identifier VARCHAR(64) NOT NULL,
    action VARCHAR(64) NOT NULL,
    actor_user_id VARCHAR(128) NOT NULL,
    actor_display_name VARCHAR(255) NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    before_value LONGTEXT NULL,
    after_value LONGTEXT NULL,
    PRIMARY KEY (id)
);
