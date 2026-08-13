CREATE TABLE article_priorities (
    id BIGSERIAL PRIMARY KEY,
    article_topic_id BIGINT NOT NULL REFERENCES article_topics(id) ON DELETE CASCADE,
    final_score NUMERIC(7,3) NOT NULL CHECK (final_score BETWEEN 0 AND 100),
    priority VARCHAR(2) NOT NULL CHECK (priority IN ('P0', 'P1', 'P2', 'P3')),
    priority_reason TEXT NOT NULL,
    factor_scores JSONB NOT NULL,
    evidence_states JSONB NOT NULL,
    evidence_details JSONB NOT NULL,
    scoring_config JSONB NOT NULL,
    search_console_score NUMERIC(7,3) NOT NULL CHECK (search_console_score BETWEEN 0 AND 100),
    search_demand_score NUMERIC(7,3) NOT NULL CHECK (search_demand_score BETWEEN 0 AND 100),
    business_relevance_score NUMERIC(7,3) NOT NULL CHECK (business_relevance_score BETWEEN 0 AND 100),
    calculation_key VARCHAR(255) NOT NULL UNIQUE,
    source_task_id BIGINT REFERENCES agent_tasks(id) ON DELETE SET NULL,
    trigger_type VARCHAR(64) NOT NULL,
    calculated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_article_priority_factor_scores_object
        CHECK (jsonb_typeof(factor_scores) = 'object'),
    CONSTRAINT chk_article_priority_evidence_states_object
        CHECK (jsonb_typeof(evidence_states) = 'object'),
    CONSTRAINT chk_article_priority_evidence_details_object
        CHECK (jsonb_typeof(evidence_details) = 'object'),
    CONSTRAINT chk_article_priority_scoring_config_object
        CHECK (jsonb_typeof(scoring_config) = 'object')
);

CREATE INDEX idx_article_priorities_topic_calculated
    ON article_priorities (article_topic_id, calculated_at DESC);

CREATE INDEX idx_article_priorities_rank
    ON article_priorities (
        final_score DESC,
        search_console_score DESC,
        search_demand_score DESC,
        business_relevance_score DESC,
        article_topic_id ASC
    );

INSERT INTO agent_definitions (agent_type, display_name, description, enabled)
VALUES (
    'EDITORIAL',
    'ReadyRoad Editorial Content Engine',
    'Maintains the evidence-backed dynamic editorial backlog and priority history.',
    TRUE
)
ON CONFLICT (agent_type) DO NOTHING;

INSERT INTO agent_settings (agent_type, setting_key, setting_value, updated_by)
VALUES (
    'EDITORIAL',
    'priority.scoring',
    '{
      "weights": {
        "searchDemand": 20,
        "searchConsoleOpportunity": 20,
        "businessConversionRelevance": 15,
        "contentGap": 10,
        "strategicIcpRelevance": 10,
        "existingReadyRoadAuthority": 5,
        "longTailOpportunity": 5,
        "multilingualOpportunity": 5,
        "contentFreshnessNeed": 5,
        "internalLinkingPotential": 5
      },
      "thresholds": {"p0": 80, "p1": 60, "p2": 40, "p3": 0},
      "missingSearchConsolePercent": 50,
      "tieBreak": [
        "searchConsoleOpportunity",
        "searchDemand",
        "businessConversionRelevance",
        "officialBacklogOrder"
      ]
    }'::jsonb,
    'OWNER_APPROVAL_PHASE_7_TASK_4'
)
ON CONFLICT (agent_type, setting_key) DO NOTHING;

INSERT INTO audit_logs (
    event_type, actor, entity_type, entity_id, correlation_id, safe_details
)
VALUES (
    'EDITORIAL_PRIORITY_SETTINGS_SEEDED',
    'OWNER_APPROVAL_PHASE_7_TASK_4',
    'AGENT_SETTING',
    'priority.scoring',
    'phase-7-task-4-owner-approval',
    '{"weightsTotal":100,"thresholds":"P0-P3","secretData":false}'::jsonb
);
