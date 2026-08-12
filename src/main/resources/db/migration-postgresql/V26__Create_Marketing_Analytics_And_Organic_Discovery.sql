ALTER TABLE agent_schedules
    ADD COLUMN interval_days SMALLINT CHECK (interval_days BETWEEN 1 AND 365);

CREATE TABLE analytics_snapshots (
    id BIGSERIAL PRIMARY KEY,
    source VARCHAR(32) NOT NULL CHECK (source IN ('GA4', 'SEARCH_CONSOLE', 'READYROAD')),
    snapshot_type VARCHAR(64) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    dimensions JSONB NOT NULL DEFAULT '{}'::jsonb,
    metrics JSONB NOT NULL DEFAULT '{}'::jsonb,
    source_record_count INTEGER NOT NULL DEFAULT 0 CHECK (source_record_count >= 0),
    status VARCHAR(16) NOT NULL CHECK (status IN ('COMPLETE', 'PARTIAL')),
    partial_failures JSONB NOT NULL DEFAULT '[]'::jsonb,
    quota_state JSONB NOT NULL DEFAULT '{}'::jsonb,
    source_snapshot_key VARCHAR(255) NOT NULL UNIQUE,
    task_id BIGINT REFERENCES agent_tasks(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (period_end >= period_start)
);

CREATE INDEX idx_analytics_snapshots_source_period
    ON analytics_snapshots (source, snapshot_type, period_end DESC);
CREATE INDEX idx_analytics_snapshots_created_at
    ON analytics_snapshots (created_at DESC);

CREATE TABLE seo_snapshots (
    id BIGSERIAL PRIMARY KEY,
    site_url VARCHAR(255) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    clicks NUMERIC(18,4) NOT NULL DEFAULT 0,
    impressions NUMERIC(18,4) NOT NULL DEFAULT 0,
    ctr NUMERIC(12,8) NOT NULL DEFAULT 0,
    average_position NUMERIC(12,6) NOT NULL DEFAULT 0,
    source_record_count INTEGER NOT NULL DEFAULT 0 CHECK (source_record_count >= 0),
    task_id BIGINT REFERENCES agent_tasks(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_seo_snapshots_window UNIQUE (site_url, period_start, period_end),
    CHECK (period_end >= period_start)
);

CREATE INDEX idx_seo_snapshots_period ON seo_snapshots (period_end DESC, period_start DESC);

CREATE TABLE seo_query_snapshots (
    id BIGSERIAL PRIMARY KEY,
    seo_snapshot_id BIGINT NOT NULL REFERENCES seo_snapshots(id) ON DELETE CASCADE,
    snapshot_date DATE NOT NULL,
    query TEXT NOT NULL,
    page VARCHAR(2048) NOT NULL DEFAULT '',
    language VARCHAR(8) NOT NULL CHECK (language IN ('AR', 'NL', 'FR', 'EN', 'UNKNOWN')),
    brand_classification VARCHAR(32) NOT NULL CHECK (
        brand_classification IN ('OWN_BRAND', 'NON_BRAND', 'COMPETITOR_OR_AMBIGUOUS_BRAND')
    ),
    long_tail BOOLEAN NOT NULL DEFAULT FALSE,
    search_intent VARCHAR(32) NOT NULL CHECK (
        search_intent IN ('INFORMATIONAL', 'NAVIGATIONAL', 'TRANSACTIONAL', 'UNKNOWN')
    ),
    clicks NUMERIC(18,4) NOT NULL DEFAULT 0,
    impressions NUMERIC(18,4) NOT NULL DEFAULT 0,
    ctr NUMERIC(12,8) NOT NULL DEFAULT 0,
    average_position NUMERIC(12,6) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_seo_query_snapshot UNIQUE (seo_snapshot_id, snapshot_date, query, page)
);

CREATE INDEX idx_seo_query_snapshots_query_period
    ON seo_query_snapshots (query, snapshot_date DESC);
CREATE INDEX idx_seo_query_snapshots_language_period
    ON seo_query_snapshots (language, snapshot_date DESC);

CREATE TABLE seo_page_snapshots (
    id BIGSERIAL PRIMARY KEY,
    seo_snapshot_id BIGINT NOT NULL REFERENCES seo_snapshots(id) ON DELETE CASCADE,
    snapshot_date DATE NOT NULL,
    page VARCHAR(2048) NOT NULL,
    language VARCHAR(8) NOT NULL CHECK (language IN ('AR', 'NL', 'FR', 'EN', 'UNKNOWN')),
    device VARCHAR(16) NOT NULL CHECK (device IN ('MOBILE', 'DESKTOP', 'TABLET', 'UNKNOWN')),
    clicks NUMERIC(18,4) NOT NULL DEFAULT 0,
    impressions NUMERIC(18,4) NOT NULL DEFAULT 0,
    ctr NUMERIC(12,8) NOT NULL DEFAULT 0,
    average_position NUMERIC(12,6) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_seo_page_snapshot UNIQUE (seo_snapshot_id, snapshot_date, page, device)
);

CREATE INDEX idx_seo_page_snapshots_page_period
    ON seo_page_snapshots (page, snapshot_date DESC);
CREATE INDEX idx_seo_page_snapshots_device_period
    ON seo_page_snapshots (device, snapshot_date DESC);

CREATE TABLE seo_opportunities (
    id BIGSERIAL PRIMARY KEY,
    opportunity_key VARCHAR(255) NOT NULL UNIQUE,
    query TEXT NOT NULL,
    page VARCHAR(2048) NOT NULL DEFAULT '',
    language VARCHAR(8) NOT NULL CHECK (language IN ('AR', 'NL', 'FR', 'EN', 'UNKNOWN')),
    state VARCHAR(32) NOT NULL CHECK (
        state IN ('DISCOVERING', 'EMERGING', 'OPPORTUNITY', 'ESTABLISHED', 'DECLINING')
    ),
    previous_state VARCHAR(32),
    brand_classification VARCHAR(32) NOT NULL,
    long_tail BOOLEAN NOT NULL DEFAULT FALSE,
    search_intent VARCHAR(32) NOT NULL,
    relevance BOOLEAN NOT NULL DEFAULT FALSE,
    cannibalization BOOLEAN NOT NULL DEFAULT FALSE,
    impressions NUMERIC(18,4) NOT NULL DEFAULT 0,
    clicks NUMERIC(18,4) NOT NULL DEFAULT 0,
    ctr NUMERIC(12,8) NOT NULL DEFAULT 0,
    average_position NUMERIC(12,6) NOT NULL DEFAULT 0,
    trend VARCHAR(24) NOT NULL CHECK (trend IN ('IMPROVING', 'STABLE', 'DECLINING', 'INSUFFICIENT_DATA')),
    evidence JSONB NOT NULL DEFAULT '{}'::jsonb,
    first_seen_at TIMESTAMPTZ NOT NULL,
    last_seen_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_seo_opportunities_state_updated
    ON seo_opportunities (state, updated_at DESC);
CREATE INDEX idx_seo_opportunities_language_state
    ON seo_opportunities (language, state, updated_at DESC);

CREATE TABLE seo_content_gaps (
    id BIGSERIAL PRIMARY KEY,
    gap_key VARCHAR(255) NOT NULL UNIQUE,
    query TEXT NOT NULL,
    language VARCHAR(8) NOT NULL CHECK (language IN ('AR', 'NL', 'FR', 'EN', 'UNKNOWN')),
    search_intent VARCHAR(32) NOT NULL,
    status VARCHAR(24) NOT NULL CHECK (status IN ('DISCOVERED', 'REVIEWED', 'DISMISSED', 'RESOLVED')),
    evidence JSONB NOT NULL DEFAULT '{}'::jsonb,
    first_seen_at TIMESTAMPTZ NOT NULL,
    last_seen_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_seo_content_gaps_status_updated
    ON seo_content_gaps (status, updated_at DESC);

INSERT INTO agent_definitions (agent_type, display_name, description, enabled)
VALUES (
    'ANALYTICS',
    'ReadyRoad Analytics and Organic Discovery',
    'Synchronizes official read-only analytics sources and detects organic search opportunities.',
    TRUE
)
ON CONFLICT (agent_type) DO NOTHING;

INSERT INTO agent_settings (agent_type, setting_key, setting_value, updated_by)
VALUES
    ('ANALYTICS', 'google.ga4',
     '{"accountId":"403159538","propertyId":"548176182","resourceName":"properties/548176182"}'::jsonb,
     'MASTER_SPEC_V3'),
    ('ANALYTICS', 'google.searchConsole',
     '{"siteUrl":"sc-domain:readyroad.be"}'::jsonb,
     'MASTER_SPEC_V3'),
    ('ANALYTICS', 'sync.policy',
     '{"initialBackfillDays":90,"intervalDays":3,"noDataDays":6,"sourceFailureHours":3}'::jsonb,
     'MASTER_SPEC_V3'),
    ('ANALYTICS', 'opportunity.thresholds',
     '{"windowDays":28,"emergingImpressions":20,"emergingPositionMin":11,"emergingPositionMax":30,"opportunityImpressions":50,"opportunityPositionMin":4,"opportunityPositionMax":20,"establishedPositionMax":10,"establishedClicks":10,"positionDecline":3,"clicksDeclinePercent":30,"ctrDeclinePercent":30,"stableWindows":2}'::jsonb,
     'MASTER_SPEC_V3')
ON CONFLICT (agent_type, setting_key) DO NOTHING;

INSERT INTO agent_schedules (
    agent_type, schedule_key, task_type, priority, cron_expression, interval_days, zone_id,
    payload, requires_approval, approval_mode, approval_source, enabled
)
VALUES
    ('ANALYTICS', 'analytics-full-sync', 'ANALYTICS_FULL_SYNC', 2, '0 0 3 * * *', 3,
     'Europe/Brussels', '{"mode":"SCHEDULED"}'::jsonb, FALSE,
     'STANDING_OWNER_AUTHORIZATION', 'MASTER_SPEC_V3', FALSE),
    ('ANALYTICS', 'analytics-weekly-report', 'ANALYTICS_WEEKLY_REPORT', 1, '0 15 4 * * MON', NULL,
     'Europe/Brussels', '{}'::jsonb, FALSE,
     'STANDING_OWNER_AUTHORIZATION', 'MASTER_SPEC_V3', FALSE),
    ('ANALYTICS', 'analytics-monthly-report', 'ANALYTICS_MONTHLY_REPORT', 1, '0 30 4 1 * *', NULL,
     'Europe/Brussels', '{}'::jsonb, FALSE,
     'STANDING_OWNER_AUTHORIZATION', 'MASTER_SPEC_V3', FALSE)
ON CONFLICT (agent_type, schedule_key) DO NOTHING;
