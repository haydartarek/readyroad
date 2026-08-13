CREATE TABLE youtube_videos (
    id BIGSERIAL PRIMARY KEY,
    video_id VARCHAR(32) NOT NULL UNIQUE,
    channel_id VARCHAR(64) NOT NULL,
    channel_title VARCHAR(255) NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    published_at TIMESTAMPTZ NOT NULL,
    thumbnail_url TEXT NOT NULL,
    watch_url TEXT NOT NULL,
    embed_url TEXT NOT NULL,
    source_language VARCHAR(8) NOT NULL DEFAULT 'UNKNOWN' CHECK (
        source_language IN ('AR', 'NL', 'FR', 'EN', 'UNKNOWN')
    ),
    category_id VARCHAR(32),
    duration_seconds INTEGER CHECK (duration_seconds IS NULL OR duration_seconds >= 0),
    view_count BIGINT CHECK (view_count IS NULL OR view_count >= 0),
    like_count BIGINT CHECK (like_count IS NULL OR like_count >= 0),
    comment_count BIGINT CHECK (comment_count IS NULL OR comment_count >= 0),
    source_metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    source_hash VARCHAR(64) NOT NULL,
    first_seen_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_synced_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    task_id BIGINT REFERENCES agent_tasks(id) ON DELETE SET NULL
);

CREATE INDEX idx_youtube_videos_published
    ON youtube_videos (published_at DESC, id DESC);
CREATE INDEX idx_youtube_videos_views
    ON youtube_videos (view_count DESC NULLS LAST, published_at DESC);

CREATE TABLE content_items (
    id BIGSERIAL PRIMARY KEY,
    item_key VARCHAR(255) NOT NULL UNIQUE,
    item_type VARCHAR(64) NOT NULL CHECK (
        item_type IN ('YOUTUBE_CONTENT_PACKAGE', 'YOUTUBE_SOCIAL_DRAFT')
    ),
    source_type VARCHAR(64) NOT NULL,
    source_id VARCHAR(255) NOT NULL,
    parent_item_id BIGINT REFERENCES content_items(id) ON DELETE CASCADE,
    language VARCHAR(8) NOT NULL CHECK (language IN ('AR', 'NL', 'FR', 'EN', 'UNKNOWN')),
    platform VARCHAR(32) CHECK (
        platform IS NULL OR platform IN ('FACEBOOK', 'INSTAGRAM', 'TIKTOK', 'YOUTUBE_COMMUNITY')
    ),
    status VARCHAR(40) NOT NULL CHECK (status IN ('READY_FOR_CONTENT_AGENT', 'DRAFT')),
    title TEXT NOT NULL,
    body TEXT NOT NULL DEFAULT '',
    usp_id BIGINT NOT NULL REFERENCES marketing_usp(id),
    icp_id VARCHAR(64) NOT NULL REFERENCES marketing_icp(id),
    content_pillar_id BIGINT NOT NULL REFERENCES marketing_content_pillars(id),
    funnel_stage_id BIGINT NOT NULL REFERENCES marketing_funnel_stages(id),
    conversion_goal_id BIGINT NOT NULL REFERENCES marketing_conversion_goals(id),
    primary_cta VARCHAR(200) NOT NULL,
    strategy_context JSONB NOT NULL,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    task_id BIGINT REFERENCES agent_tasks(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_content_items_source
    ON content_items (source_type, source_id, item_type);
CREATE INDEX idx_content_items_status
    ON content_items (status, created_at DESC);

INSERT INTO agent_definitions (agent_type, display_name, description, enabled)
VALUES (
    'YOUTUBE',
    'ReadyRoad YouTube Agent',
    'Monitors the verified ReadyRoad channel read-only and prepares strategy-bound content handoffs.',
    TRUE
)
ON CONFLICT (agent_type) DO NOTHING;

INSERT INTO agent_settings (agent_type, setting_key, setting_value, updated_by)
VALUES
    ('YOUTUBE', 'youtube.channel',
     '{"handle":"@RijBewijsBe","channelId":"UCs_IDQXCz6zADuHIdfS2C2w","url":"https://www.youtube.com/@RijBewijsBe/featured"}'::jsonb,
     'OWNER_APPROVAL_2026_08_13'),
    ('YOUTUBE', 'youtube.monitoring',
     '{"intervalHours":24,"pageSize":13,"readOnly":true}'::jsonb,
     'OWNER_APPROVAL_2026_08_13')
ON CONFLICT (agent_type, setting_key) DO NOTHING;

INSERT INTO agent_schedules (
    agent_type, schedule_key, task_type, priority, cron_expression, interval_days, zone_id,
    payload, requires_approval, approval_mode, approval_source, enabled
)
VALUES (
    'YOUTUBE', 'youtube-channel-monitor', 'YOUTUBE_CHANNEL_SYNC', 1,
    '0 0 0 * * *', 1, 'Europe/Brussels', '{"mode":"SCHEDULED"}'::jsonb,
    FALSE, 'STANDING_OWNER_AUTHORIZATION', 'MASTER_SPEC_V3', FALSE
)
ON CONFLICT (agent_type, schedule_key) DO NOTHING;
