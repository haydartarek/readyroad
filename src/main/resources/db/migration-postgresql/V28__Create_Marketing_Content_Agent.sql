ALTER TABLE content_items
    DROP CONSTRAINT content_items_item_type_check;

ALTER TABLE content_items
    ADD CONSTRAINT content_items_item_type_check CHECK (
        item_type IN (
            'YOUTUBE_CONTENT_PACKAGE',
            'YOUTUBE_SOCIAL_DRAFT',
            'CONTENT_PACKAGE',
            'CONTENT_VARIANT'
        )
    );

ALTER TABLE content_items
    ADD COLUMN source_hash VARCHAR(64),
    ADD COLUMN content_fingerprint VARCHAR(64);

CREATE UNIQUE INDEX uq_content_variant_source_context
    ON content_items (
        source_type,
        source_id,
        language,
        source_hash,
        usp_id,
        content_pillar_id,
        icp_id,
        funnel_stage_id,
        conversion_goal_id,
        ((strategy_context ->> 'positioningId'))
    )
    WHERE item_type = 'CONTENT_VARIANT';

CREATE UNIQUE INDEX uq_content_variant_fingerprint
    ON content_items (language, content_fingerprint)
    WHERE item_type = 'CONTENT_VARIANT' AND content_fingerprint IS NOT NULL;

INSERT INTO agent_definitions (agent_type, display_name, description, enabled)
VALUES (
    'CONTENT',
    'ReadyRoad Content Agent',
    'Creates strategy-bound multilingual drafts from verified ReadyRoad Core Data.',
    TRUE
)
ON CONFLICT (agent_type) DO NOTHING;

INSERT INTO agent_settings (agent_type, setting_key, setting_value, updated_by)
VALUES (
    'CONTENT',
    'content.generation',
    '{"provider":"OPENAI","api":"RESPONSES","primaryModel":"gpt-5.6-terra","reviewModel":"gpt-5.6-sol","reasoningEffort":"medium","languages":["AR","NL","EN","FR"],"structuredOutput":true}'::jsonb,
    'OWNER_APPROVAL_2026_08_13'
)
ON CONFLICT (agent_type, setting_key) DO NOTHING;
