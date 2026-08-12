INSERT INTO agent_definitions (agent_type, display_name, description, enabled)
VALUES (
    'STRATEGY',
    'ReadyRoad Marketing Strategy Engine',
    'Provides the approved strategy context required by downstream marketing agents.',
    TRUE
)
ON CONFLICT (agent_type) DO NOTHING;

INSERT INTO marketing_positioning (
    statement,
    brand_identity,
    brand_voice,
    active,
    approved_by
)
VALUES (
    'ReadyRoad منصة تعليمية مستقلة تساعد المتعلمين على الاستعداد لامتحان السياقة النظري في بلجيكا من خلال الدروس، العلامات المرورية، التدريب والامتحانات.',
    '["موثوق", "تعليمي", "بسيط", "حديث", "هادئ", "مناسب للمبتدئ"]'::jsonb,
    '["بشري", "مباشر", "تعليمي", "بسيط", "غير آلي", "غير متعالٍ", "دون مبالغة تسويقية"]'::jsonb,
    TRUE,
    'MASTER_SPEC_V3'
);

INSERT INTO marketing_icp (id, name, language, country, primary_goal, active, approved_by)
VALUES
    ('ICP-AR-BEGINNER', 'Arabic-speaking learner in Belgium preparing for driving theory.', 'ar', 'Belgium', 'Prepare for the Belgian driving theory exam.', TRUE, 'MASTER_SPEC_V3'),
    ('ICP-NL-PRACTICE', 'Dutch-speaking learner looking for additional Belgian theory practice.', 'nl', 'Belgium', 'Find additional Belgian theory practice.', TRUE, 'MASTER_SPEC_V3'),
    ('ICP-FR-THEORY', 'French-speaking learner preparing for the Belgian theory exam.', 'fr', 'Belgium', 'Prepare for the Belgian theory exam.', TRUE, 'MASTER_SPEC_V3'),
    ('ICP-FAILED-EXAM', 'Learner who previously failed and needs targeted improvement.', NULL, 'Belgium', 'Improve weak areas after a failed exam.', TRUE, 'MASTER_SPEC_V3'),
    ('ICP-SIGN-SEARCH', 'User arriving from Google looking for a specific Belgian traffic sign or rule.', NULL, 'Belgium', 'Understand a specific Belgian traffic sign or rule.', TRUE, 'MASTER_SPEC_V3'),
    ('ICP-PRACTICAL-EXAM', 'Learner preparing for the practical driving examination.', NULL, 'Belgium', 'Prepare for the practical driving examination.', TRUE, 'MASTER_SPEC_V3');

INSERT INTO marketing_content_pillars (pillar_key, name, priority, active, approved_by)
VALUES
    ('THEORY_EXAM', 'الامتحان النظري', 1, TRUE, 'MASTER_SPEC_V3'),
    ('TRAFFIC_SIGNS', 'العلامات المرورية', 1, TRUE, 'MASTER_SPEC_V3'),
    ('TRAFFIC_RULES', 'قواعد المرور', 1, TRUE, 'MASTER_SPEC_V3'),
    ('PRIORITY_INTERSECTIONS', 'الأولوية والتقاطعات', 1, TRUE, 'MASTER_SPEC_V3'),
    ('SPEED_PARKING_STOPPING', 'السرعة والوقوف والتوقف', 1, TRUE, 'MASTER_SPEC_V3'),
    ('PRACTICAL_EXAM', 'الامتحان العملي', 1, TRUE, 'MASTER_SPEC_V3'),
    ('COMMON_EXAM_ERRORS', 'أخطاء الامتحان الشائعة', 1, TRUE, 'MASTER_SPEC_V3'),
    ('PREPARATION_TIPS', 'نصائح الاستعداد', 1, TRUE, 'MASTER_SPEC_V3'),
    ('BELGIAN_DRIVING_LICENCE', 'رخصة السياقة البلجيكية', 1, TRUE, 'MASTER_SPEC_V3'),
    ('BELGIAN_TRAFFIC_LAW_UPDATES', 'تحديثات قوانين السير البلجيكية', 1, TRUE, 'MASTER_SPEC_V3'),
    ('TRAINING_TESTS', 'التدريب والاختبارات', 1, TRUE, 'MASTER_SPEC_V3'),
    ('READYROAD_EDUCATIONAL_VIDEOS', 'فيديوهات ReadyRoad التعليمية', 1, TRUE, 'MASTER_SPEC_V3');

INSERT INTO marketing_funnel_stages (stage_key, sequence_number, active, approved_by)
VALUES
    ('AWARENESS', 1, TRUE, 'MASTER_SPEC_V3'),
    ('DISCOVERY', 2, TRUE, 'MASTER_SPEC_V3'),
    ('EDUCATION', 3, TRUE, 'MASTER_SPEC_V3'),
    ('PRACTICE', 4, TRUE, 'MASTER_SPEC_V3'),
    ('ACCOUNT_CONVERSION', 5, TRUE, 'MASTER_SPEC_V3'),
    ('EXAM_USAGE', 6, TRUE, 'MASTER_SPEC_V3'),
    ('RETENTION', 7, TRUE, 'MASTER_SPEC_V3'),
    ('PAID_CONVERSION', 8, TRUE, 'MASTER_SPEC_V3'),
    ('ADVOCACY', 9, TRUE, 'MASTER_SPEC_V3');

-- USP, conversion goals and social proof are intentionally not seeded.
-- Master v3 requires verified values and prohibits inventing missing strategy context.
