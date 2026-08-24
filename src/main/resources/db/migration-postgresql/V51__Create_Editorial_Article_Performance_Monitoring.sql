CREATE TABLE article_performance_snapshots (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE RESTRICT,
    article_publication_id BIGINT NOT NULL REFERENCES article_publications(id) ON DELETE RESTRICT,
    language VARCHAR(8) NOT NULL CHECK (language IN ('AR', 'NL', 'FR', 'EN')),
    published_path TEXT NOT NULL CHECK (published_path LIKE '/%'),
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    previous_period_start DATE NOT NULL,
    previous_period_end DATE NOT NULL,
    clicks NUMERIC(18,4) NOT NULL DEFAULT 0 CHECK (clicks >= 0),
    impressions NUMERIC(18,4) NOT NULL DEFAULT 0 CHECK (impressions >= 0),
    ctr NUMERIC(12,8) NOT NULL DEFAULT 0 CHECK (ctr >= 0),
    average_position NUMERIC(12,6) NOT NULL DEFAULT 0 CHECK (average_position >= 0),
    previous_clicks NUMERIC(18,4) NOT NULL DEFAULT 0 CHECK (previous_clicks >= 0),
    previous_impressions NUMERIC(18,4) NOT NULL DEFAULT 0 CHECK (previous_impressions >= 0),
    previous_ctr NUMERIC(12,8) NOT NULL DEFAULT 0 CHECK (previous_ctr >= 0),
    previous_average_position NUMERIC(12,6) NOT NULL DEFAULT 0 CHECK (previous_average_position >= 0),
    evidence_state VARCHAR(16) NOT NULL CHECK (evidence_state IN ('PRESENT', 'MISSING')),
    indexing_state VARCHAR(24) NOT NULL CHECK (indexing_state IN ('DISCOVERED', 'NO_DATA')),
    monitoring_task_id BIGINT NOT NULL REFERENCES agent_tasks(id) ON DELETE RESTRICT,
    analytics_task_id BIGINT NOT NULL REFERENCES agent_tasks(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_article_performance_window
        UNIQUE (article_publication_id, period_start, period_end),
    CHECK (period_end >= period_start),
    CHECK (previous_period_end >= previous_period_start),
    CHECK (previous_period_end < period_start)
);

CREATE INDEX idx_article_performance_article_period
    ON article_performance_snapshots (article_id, period_end DESC, language);

CREATE INDEX idx_article_performance_monitoring_task
    ON article_performance_snapshots (monitoring_task_id, article_id);

CREATE TABLE article_refresh_recommendations (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE RESTRICT,
    performance_task_id BIGINT NOT NULL REFERENCES agent_tasks(id) ON DELETE RESTRICT,
    recommendation_task_id BIGINT NOT NULL REFERENCES agent_tasks(id) ON DELETE RESTRICT,
    period_end DATE NOT NULL,
    recommended BOOLEAN NOT NULL,
    reason_codes JSONB NOT NULL DEFAULT '[]'::jsonb,
    evidence JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_article_refresh_performance_task UNIQUE (article_id, performance_task_id),
    CONSTRAINT chk_article_refresh_reason_codes_array CHECK (jsonb_typeof(reason_codes) = 'array'),
    CONSTRAINT chk_article_refresh_evidence_object CHECK (jsonb_typeof(evidence) = 'object')
);

CREATE INDEX idx_article_refresh_article_created
    ON article_refresh_recommendations (article_id, created_at DESC, id DESC);
