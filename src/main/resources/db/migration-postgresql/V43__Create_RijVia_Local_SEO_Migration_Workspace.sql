ALTER TABLE analytics_snapshots
    DROP CONSTRAINT IF EXISTS analytics_snapshots_source_check;

ALTER TABLE analytics_snapshots
    ADD CONSTRAINT analytics_snapshots_source_check
    CHECK (source IN ('GA4', 'SEARCH_CONSOLE', 'READYROAD', 'RIJVIA'));

ALTER TABLE seo_snapshots
    ADD COLUMN source_kind VARCHAR(24) NOT NULL DEFAULT 'LIVE_API'
        CHECK (source_kind IN ('LIVE_API', 'LOCAL_EXCEL'));

ALTER TABLE seo_snapshots
    DROP CONSTRAINT uq_seo_snapshots_window;

ALTER TABLE seo_snapshots
    ADD CONSTRAINT uq_seo_snapshots_window_source
    UNIQUE (site_url, period_start, period_end, source_kind);

CREATE TABLE search_console_import_snapshots (
    id BIGSERIAL PRIMARY KEY,
    source_file_name VARCHAR(255) NOT NULL,
    file_sha256 CHAR(64) NOT NULL UNIQUE,
    file_size_bytes BIGINT NOT NULL CHECK (file_size_bytes > 0),
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    seo_snapshot_id BIGINT NOT NULL UNIQUE REFERENCES seo_snapshots(id) ON DELETE RESTRICT,
    analytics_snapshot_id BIGINT NOT NULL UNIQUE REFERENCES analytics_snapshots(id) ON DELETE RESTRICT,
    sheet_counts JSONB NOT NULL DEFAULT '{}'::jsonb,
    summary JSONB NOT NULL DEFAULT '{}'::jsonb,
    report JSONB NOT NULL DEFAULT '{}'::jsonb,
    warnings JSONB NOT NULL DEFAULT '[]'::jsonb,
    ignored_row_count INTEGER NOT NULL DEFAULT 0 CHECK (ignored_row_count >= 0),
    status VARCHAR(24) NOT NULL CHECK (status IN ('COMPLETE', 'COMPLETE_WITH_WARNINGS')),
    imported_by VARCHAR(160) NOT NULL,
    imported_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (period_end >= period_start),
    CHECK (jsonb_typeof(sheet_counts) = 'object'),
    CHECK (jsonb_typeof(summary) = 'object'),
    CHECK (jsonb_typeof(report) = 'object'),
    CHECK (jsonb_typeof(warnings) = 'array')
);

CREATE INDEX idx_search_console_import_snapshots_imported_at
    ON search_console_import_snapshots (imported_at DESC, id DESC);

ALTER TABLE seo_query_snapshots
    DROP CONSTRAINT seo_query_snapshots_brand_classification_check;

ALTER TABLE seo_query_snapshots
    ADD CONSTRAINT seo_query_snapshots_brand_classification_check CHECK (
        brand_classification IN (
            'OWN_BRAND',
            'OWN_BRAND_RIJVIA',
            'OLD_BRAND_READYROAD',
            'NON_BRAND',
            'COMPETITOR_OR_AMBIGUOUS_BRAND'
        )
    ),
    ADD COLUMN source_import_id BIGINT
        REFERENCES search_console_import_snapshots(id) ON DELETE RESTRICT,
    ADD COLUMN classifications JSONB NOT NULL DEFAULT '[]'::jsonb,
    ADD COLUMN recommended_action_category VARCHAR(64),
    ADD COLUMN confidence_level VARCHAR(16)
        CHECK (confidence_level IS NULL OR confidence_level IN ('LOW', 'MEDIUM', 'HIGH')),
    ADD CONSTRAINT chk_seo_query_snapshots_classifications_array
        CHECK (jsonb_typeof(classifications) = 'array');

ALTER TABLE seo_page_snapshots
    ADD COLUMN source_import_id BIGINT
        REFERENCES search_console_import_snapshots(id) ON DELETE RESTRICT,
    ADD COLUMN recommended_action_category VARCHAR(64),
    ADD COLUMN confidence_level VARCHAR(16)
        CHECK (confidence_level IS NULL OR confidence_level IN ('LOW', 'MEDIUM', 'HIGH'));

ALTER TABLE seo_opportunities
    DROP CONSTRAINT seo_opportunities_state_check;

ALTER TABLE seo_opportunities
    ADD CONSTRAINT seo_opportunities_state_check CHECK (
        state IN (
            'DISCOVERING', 'EMERGING', 'OPPORTUNITY', 'ESTABLISHED', 'DECLINING',
            'MIGRATION_RISK', 'CTR_REPAIR', 'CONTENT_GAP', 'INTERNAL_LINK_GAP',
            'TECHNICAL_SEO_RISK', 'LOW_CONFIDENCE'
        )
    ),
    ADD COLUMN source_import_id BIGINT
        REFERENCES search_console_import_snapshots(id) ON DELETE RESTRICT,
    ADD COLUMN priority VARCHAR(2) CHECK (priority IS NULL OR priority IN ('P0', 'P1', 'P2', 'P3')),
    ADD COLUMN recommended_action_category VARCHAR(64),
    ADD COLUMN confidence_level VARCHAR(16)
        CHECK (confidence_level IS NULL OR confidence_level IN ('LOW', 'MEDIUM', 'HIGH')),
    ADD COLUMN classifications JSONB NOT NULL DEFAULT '[]'::jsonb,
    ADD CONSTRAINT chk_seo_opportunities_classifications_array
        CHECK (jsonb_typeof(classifications) = 'array');

CREATE INDEX idx_seo_opportunities_import_priority
    ON seo_opportunities (source_import_id, priority, impressions DESC);

CREATE TABLE marketing_draft_briefs (
    id BIGSERIAL PRIMARY KEY,
    source_import_id BIGINT NOT NULL
        REFERENCES search_console_import_snapshots(id) ON DELETE RESTRICT,
    source_opportunity_id BIGINT REFERENCES seo_opportunities(id) ON DELETE SET NULL,
    brief_key VARCHAR(128) NOT NULL,
    language VARCHAR(8) NOT NULL CHECK (language IN ('AR', 'NL', 'FR', 'EN')),
    working_title TEXT NOT NULL,
    purpose TEXT NOT NULL,
    target_queries JSONB NOT NULL DEFAULT '[]'::jsonb,
    supporting_pages JSONB NOT NULL DEFAULT '[]'::jsonb,
    content_pillar_key VARCHAR(96),
    icp_key VARCHAR(64),
    conversion_goal_key VARCHAR(96),
    status VARCHAR(32) NOT NULL DEFAULT 'WAITING_OWNER_REVIEW'
        CHECK (status IN ('WAITING_OWNER_REVIEW', 'APPROVED_LOCAL', 'REJECTED')),
    evidence JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_marketing_draft_briefs_import_key UNIQUE (source_import_id, brief_key),
    CONSTRAINT chk_marketing_draft_briefs_target_queries_array
        CHECK (jsonb_typeof(target_queries) = 'array'),
    CONSTRAINT chk_marketing_draft_briefs_supporting_pages_array
        CHECK (jsonb_typeof(supporting_pages) = 'array')
);

CREATE INDEX idx_marketing_draft_briefs_status_language
    ON marketing_draft_briefs (status, language, id);
