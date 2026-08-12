CREATE TABLE marketing_usp (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    evidence_type VARCHAR(128) NOT NULL,
    evidence_reference VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    priority SMALLINT NOT NULL DEFAULT 1 CHECK (priority BETWEEN 0 AND 3),
    approved_by VARCHAR(160),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_marketing_usp_active_priority
    ON marketing_usp (active, priority DESC, id ASC);

CREATE TABLE marketing_icp (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    language VARCHAR(8),
    country VARCHAR(80),
    region VARCHAR(160),
    primary_goal TEXT,
    main_problem TEXT,
    search_intent VARCHAR(160),
    preferred_content_type VARCHAR(128),
    preferred_channel VARCHAR(128),
    main_objections JSONB NOT NULL DEFAULT '[]'::jsonb,
    funnel_stage VARCHAR(64),
    conversion_goal VARCHAR(128),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    approved_by VARCHAR(160),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_marketing_icp_active ON marketing_icp (active, id);

CREATE TABLE marketing_positioning (
    id BIGSERIAL PRIMARY KEY,
    statement TEXT NOT NULL,
    brand_identity JSONB NOT NULL DEFAULT '[]'::jsonb,
    brand_voice JSONB NOT NULL DEFAULT '[]'::jsonb,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    approved_by VARCHAR(160),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_marketing_positioning_single_active
    ON marketing_positioning (active)
    WHERE active = TRUE;

CREATE TABLE marketing_content_pillars (
    id BIGSERIAL PRIMARY KEY,
    pillar_key VARCHAR(96) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    priority SMALLINT NOT NULL DEFAULT 1 CHECK (priority BETWEEN 0 AND 3),
    approved_by VARCHAR(160),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_marketing_content_pillars_active_priority
    ON marketing_content_pillars (active, priority DESC, id ASC);

CREATE TABLE marketing_funnel_stages (
    id BIGSERIAL PRIMARY KEY,
    stage_key VARCHAR(64) NOT NULL UNIQUE,
    sequence_number SMALLINT NOT NULL UNIQUE CHECK (sequence_number > 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    approved_by VARCHAR(160),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_marketing_funnel_stages_active_sequence
    ON marketing_funnel_stages (active, sequence_number ASC);

CREATE TABLE marketing_conversion_goals (
    id BIGSERIAL PRIMARY KEY,
    goal_key VARCHAR(96) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    primary_cta VARCHAR(200) NOT NULL,
    funnel_stage_id BIGINT NOT NULL REFERENCES marketing_funnel_stages(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    approved_by VARCHAR(160),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_marketing_conversion_goals_stage
    ON marketing_conversion_goals (funnel_stage_id, active, id);

CREATE TABLE social_proof_items (
    id BIGSERIAL PRIMARY KEY,
    proof_type VARCHAR(96) NOT NULL,
    claim TEXT NOT NULL,
    evidence_reference VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    approved_by VARCHAR(160),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_social_proof_items_active_type
    ON social_proof_items (active, proof_type, id);
