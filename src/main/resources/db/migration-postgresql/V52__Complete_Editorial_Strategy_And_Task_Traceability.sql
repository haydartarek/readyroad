ALTER TABLE article_topics
    ADD COLUMN usp_id BIGINT REFERENCES marketing_usp(id) ON DELETE SET NULL,
    ADD COLUMN icp_id VARCHAR(64) REFERENCES marketing_icp(id) ON DELETE SET NULL;

ALTER TABLE article_briefs
    ADD COLUMN usp_id BIGINT REFERENCES marketing_usp(id) ON DELETE RESTRICT,
    ADD COLUMN source_task_id BIGINT REFERENCES agent_tasks(id) ON DELETE RESTRICT;

ALTER TABLE articles
    ADD COLUMN usp_id BIGINT REFERENCES marketing_usp(id) ON DELETE RESTRICT;

ALTER TABLE article_versions
    ADD COLUMN generated_by_task_id BIGINT REFERENCES agent_tasks(id) ON DELETE RESTRICT;

CREATE INDEX idx_article_topics_strategy_context
    ON article_topics (usp_id, icp_id, content_pillar_id, funnel_stage_id, conversion_goal_id);

CREATE UNIQUE INDEX uq_article_briefs_source_task
    ON article_briefs (source_task_id)
    WHERE source_task_id IS NOT NULL;

CREATE UNIQUE INDEX uq_article_versions_generated_task
    ON article_versions (generated_by_task_id)
    WHERE generated_by_task_id IS NOT NULL;

INSERT INTO marketing_usp (
    title, description, evidence_type, evidence_reference, active, priority, approved_by
)
SELECT
    'RijVia learning platform',
    'RijVia helps learners prepare for the Belgian driving theory exam with lessons, traffic signs, practice and exam simulation in Arabic, Dutch, French and English.',
    'PRODUCT_CAPABILITY',
    'https://rijvia.be',
    TRUE,
    1,
    'OWNER_APPROVAL_PHASE_5_STRATEGY_CONTEXT'
WHERE NOT EXISTS (
    SELECT 1
    FROM marketing_usp
    WHERE title = 'RijVia learning platform'
);

WITH approved_goals(stage_key, goal_key, name, description, primary_cta) AS (
    VALUES
        ('AWARENESS', 'DISCOVER_RIJVIA',
         'Discover RijVia',
         'Discover the RijVia learning platform.',
         'تعرّف على RijVia'),
        ('DISCOVERY', 'EXPLORE_EDUCATIONAL_CONTENT',
         'Explore relevant educational content',
         'Explore relevant lessons and traffic-sign learning content.',
         'اكتشف الدروس والعلامات المرورية'),
        ('EDUCATION', 'CONTINUE_TOPIC_LEARNING',
         'Continue learning the relevant rule or topic',
         'Continue learning the relevant Belgian driving theory rule or topic.',
         'تعلّم القاعدة بالتفصيل على RijVia'),
        ('PRACTICE', 'START_PRACTICE',
         'Start practice',
         'Start a relevant RijVia practice flow.',
         'ابدأ التدريب'),
        ('ACCOUNT_CONVERSION', 'CREATE_ACCOUNT_BEGIN_LEARNING',
         'Create an account and begin learning',
         'Create an account and begin learning with RijVia.',
         'أنشئ حسابك وابدأ التدريب'),
        ('EXAM_USAGE', 'START_THEORY_EXAM_SIMULATION',
         'Start a theory exam simulation',
         'Start a RijVia Belgian driving theory exam simulation.',
         'جرّب امتحانًا نظريًا'),
        ('RETENTION', 'RETURN_CONTINUE_TRAINING',
         'Return and continue training',
         'Return to RijVia and continue an existing learning journey.',
         'واصل تدريبك'),
        ('ADVOCACY', 'RECOMMEND_RIJVIA',
         'Recommend RijVia to another learner',
         'Recommend RijVia to another learner preparing for the theory exam.',
         'شارك RijVia مع شخص يستعد للامتحان')
)
INSERT INTO marketing_conversion_goals (
    goal_key, name, description, primary_cta, funnel_stage_id, active, approved_by
)
SELECT
    approved.goal_key,
    approved.name,
    approved.description,
    approved.primary_cta,
    stage.id,
    TRUE,
    'OWNER_APPROVAL_PHASE_5_STRATEGY_CONTEXT'
FROM approved_goals approved
JOIN marketing_funnel_stages stage ON stage.stage_key = approved.stage_key
ON CONFLICT (goal_key) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    primary_cta = EXCLUDED.primary_cta,
    funnel_stage_id = EXCLUDED.funnel_stage_id,
    active = TRUE,
    approved_by = EXCLUDED.approved_by,
    updated_at = CURRENT_TIMESTAMP,
    version = marketing_conversion_goals.version + 1;

INSERT INTO audit_logs (
    event_type, actor, entity_type, entity_id, correlation_id, safe_details
)
VALUES (
    'OWNER_APPROVED_EDITORIAL_STRATEGY_COMPLETED',
    'OWNER_APPROVAL_PHASE_5_STRATEGY_CONTEXT',
    'MARKETING_STRATEGY',
    'EDITORIAL_CONTENT',
    'owner-approved-editorial-strategy-v52',
    '{"activeNonPaidConversionGoals":8,"paidConversionGoalActivated":false,"publicBrand":"RijVia"}'::jsonb
);
