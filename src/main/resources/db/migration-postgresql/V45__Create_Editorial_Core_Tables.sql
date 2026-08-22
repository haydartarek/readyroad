CREATE TABLE article_keyword_clusters (
    id BIGSERIAL PRIMARY KEY,
    cluster_key VARCHAR(128) NOT NULL UNIQUE,
    primary_query TEXT NOT NULL,
    search_intent VARCHAR(64) NOT NULL,
    primary_language VARCHAR(8) NOT NULL CHECK (primary_language IN ('AR', 'NL', 'FR', 'EN')),
    source_opportunity_id BIGINT REFERENCES seo_opportunities(id) ON DELETE RESTRICT,
    content_pillar_id BIGINT REFERENCES marketing_content_pillars(id) ON DELETE RESTRICT,
    funnel_stage_id BIGINT REFERENCES marketing_funnel_stages(id) ON DELETE RESTRICT,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_article_keyword_clusters_language_status
    ON article_keyword_clusters (primary_language, status, id);

CREATE TABLE article_briefs (
    id BIGSERIAL PRIMARY KEY,
    article_topic_id BIGINT NOT NULL REFERENCES article_topics(id) ON DELETE RESTRICT,
    keyword_cluster_id BIGINT REFERENCES article_keyword_clusters(id) ON DELETE RESTRICT,
    target_language VARCHAR(8) NOT NULL CHECK (target_language IN ('AR', 'NL', 'FR', 'EN')),
    search_intent VARCHAR(64) NOT NULL,
    working_title TEXT NOT NULL,
    purpose TEXT NOT NULL,
    icp_id VARCHAR(64) REFERENCES marketing_icp(id) ON DELETE RESTRICT,
    content_pillar_id BIGINT REFERENCES marketing_content_pillars(id) ON DELETE RESTRICT,
    funnel_stage_id BIGINT REFERENCES marketing_funnel_stages(id) ON DELETE RESTRICT,
    conversion_goal_id BIGINT REFERENCES marketing_conversion_goals(id) ON DELETE RESTRICT,
    primary_cta VARCHAR(200) NOT NULL,
    target_queries JSONB NOT NULL DEFAULT '[]'::jsonb,
    source_requirements JSONB NOT NULL DEFAULT '[]'::jsonb,
    legal_review_required BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_article_briefs_target_queries_array
        CHECK (jsonb_typeof(target_queries) = 'array'),
    CONSTRAINT chk_article_briefs_source_requirements_array
        CHECK (jsonb_typeof(source_requirements) = 'array')
);

CREATE INDEX idx_article_briefs_status_language
    ON article_briefs (status, target_language, id);

CREATE TABLE articles (
    id BIGSERIAL PRIMARY KEY,
    article_topic_id BIGINT NOT NULL UNIQUE REFERENCES article_topics(id) ON DELETE RESTRICT,
    canonical_key VARCHAR(128) NOT NULL UNIQUE,
    lifecycle_state VARCHAR(32) NOT NULL CHECK (
        lifecycle_state IN (
            'IDEA', 'PLANNED', 'BRIEF_READY', 'DRAFTING', 'DRAFT_READY',
            'FACT_CHECK_REQUIRED', 'LEGAL_REVIEW_REQUIRED', 'TRANSLATION_REQUIRED',
            'IMAGE_REQUIRED', 'WAITING_APPROVAL', 'APPROVED', 'SCHEDULED',
            'PUBLISHED', 'UPDATE_RECOMMENDED', 'ARCHIVED', 'REJECTED'
        )
    ),
    canonical_language VARCHAR(8) NOT NULL CHECK (canonical_language IN ('AR', 'NL', 'FR', 'EN')),
    icp_id VARCHAR(64) REFERENCES marketing_icp(id) ON DELETE RESTRICT,
    content_pillar_id BIGINT REFERENCES marketing_content_pillars(id) ON DELETE RESTRICT,
    funnel_stage_id BIGINT REFERENCES marketing_funnel_stages(id) ON DELETE RESTRICT,
    conversion_goal_id BIGINT REFERENCES marketing_conversion_goals(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_articles_lifecycle
    ON articles (lifecycle_state, canonical_language, id);

CREATE TABLE article_versions (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE RESTRICT,
    version_number INTEGER NOT NULL CHECK (version_number > 0),
    language VARCHAR(8) NOT NULL CHECK (language IN ('AR', 'NL', 'FR', 'EN')),
    title TEXT NOT NULL,
    slug VARCHAR(255),
    summary TEXT,
    body TEXT NOT NULL,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    generation_metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    status VARCHAR(32) NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(160),
    CONSTRAINT uq_article_versions_identity UNIQUE (article_id, language, version_number),
    CONSTRAINT chk_article_versions_metadata_object
        CHECK (jsonb_typeof(metadata) = 'object'),
    CONSTRAINT chk_article_versions_generation_metadata_object
        CHECK (jsonb_typeof(generation_metadata) = 'object')
);

CREATE INDEX idx_article_versions_history
    ON article_versions (article_id, language, version_number DESC);

CREATE UNIQUE INDEX uq_article_versions_current_language
    ON article_versions (article_id, language)
    WHERE is_current = TRUE;

CREATE UNIQUE INDEX uq_article_versions_current_locale_slug
    ON article_versions (language, lower(slug))
    WHERE is_current = TRUE AND slug IS NOT NULL;
