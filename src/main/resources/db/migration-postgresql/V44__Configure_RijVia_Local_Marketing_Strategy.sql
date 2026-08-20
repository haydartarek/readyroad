UPDATE marketing_positioning
SET statement = 'RijVia is an independent educational platform that helps learners prepare for the Belgian driving theory exam through lessons, traffic signs, practice, exam simulation, explanations, and progress insights.',
    brand_identity = '["trustworthy","educational","clear","modern","calm","beginner-friendly"]'::jsonb,
    brand_voice = '["human","clear","educational","beginner-friendly","calm","non-aggressive"]'::jsonb,
    approved_by = 'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20',
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
WHERE active = TRUE;

UPDATE marketing_content_pillars
SET pillar_key = 'RIJVIA_EDUCATIONAL_VIDEOS',
    name = 'فيديوهات RijVia التعليمية',
    approved_by = 'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20',
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
WHERE pillar_key = 'READYROAD_EDUCATIONAL_VIDEOS';

INSERT INTO marketing_usp (
    title, description, evidence_type, evidence_reference, active, priority, approved_by
)
SELECT seed.title, seed.description, 'PRODUCT_CAPABILITY', seed.evidence_reference, TRUE, 1,
       'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20'
FROM (VALUES
    ('Four-language support', 'Learning flows are available in Arabic, Dutch, French and English.', '/ar|/nl|/fr|/'),
    ('Belgian driving theory focus', 'The learning experience is focused on Belgian category B driving theory.', '/lessons'),
    ('Traffic-sign learning', 'Learners can study Belgian traffic signs by category and sign.', '/traffic-signs'),
    ('Exam simulation', 'Learners can complete a timed theory-exam simulation.', '/exam'),
    ('Wrong-answer explanations', 'Completed exam reviews show the selected answer, correct answer and explanation.', '/exam/results'),
    ('Learner progress tracking', 'Authenticated learners can review progress and activity.', '/dashboard'),
    ('Category and weakness analysis', 'Learning analytics identify category performance and weak areas.', '/analytics/weak-areas'),
    ('Structured lessons', 'Theory content is organized into navigable lessons and pages.', '/lessons'),
    ('Mobile-first experience', 'Primary learning routes include dedicated mobile responsive validation.', 'tests/e2e/mobile-visual-identity.spec.ts')
) AS seed(title, description, evidence_reference)
WHERE NOT EXISTS (
    SELECT 1 FROM marketing_usp existing WHERE existing.title = seed.title
);

INSERT INTO agent_settings (agent_type, setting_key, setting_value, updated_by)
VALUES
    ('STRATEGY', 'brand.identity',
     '{
       "brandName":"RijVia",
       "productType":"Belgian driving theory learning platform",
       "countryFocus":"Belgium",
       "languageSupport":["AR","NL","FR","EN"],
       "tone":["human","clear","educational","beginner-friendly","calm","non-aggressive"],
       "claimsPolicy":["NO_GOVERNMENT_AFFILIATION","NO_GUARANTEED_SUCCESS","NO_FAKE_REVIEWS","NO_FAKE_STUDENT_COUNT"],
       "visualPolicy":"APPROVED_RIJVIA_ASSETS_ONLY"
     }'::jsonb,
     'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20'),
    ('STRATEGY', 'seo.migration',
     '{
       "sourceDomain":"readyroad.be",
       "candidateDomain":"rijvia.be",
       "activationStatus":"OWNER_CONFIRMED_PENDING_RELEASE",
       "mappingPolicy":"PAGE_TO_PAGE_PRESERVE_PATH",
       "changeOfAddress":"RELEASE_ONLY",
       "sitemapSubmission":"RELEASE_ONLY"
     }'::jsonb,
     'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20'),
    ('STRATEGY', 'seo.language.ar',
     '{"priority":"HIGH","voice":"natural learner-friendly Arabic","focus":["traffic signs","Belgian context","lessons and practice links"],"literalTranslation":false}'::jsonb,
     'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20'),
    ('STRATEGY', 'seo.language.nl',
     '{"priority":"HIGH","voice":"natural Belgian Dutch learner language","focus":["autoweg","autosnelweg","verkeersbord","voorrang","rijstrook","noodstopstrook"]}'::jsonb,
     'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20'),
    ('STRATEGY', 'seo.language.fr',
     '{"priority":"HIGH","voice":"clear concise Belgium-specific French","focus":["landing CTR","FAQ","traffic signs"],"literalTranslation":false}'::jsonb,
     'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20'),
    ('STRATEGY', 'seo.language.en',
     '{"priority":"SUPPORT","voice":"clear support language","focus":["completeness","canonical","hreflang"]}'::jsonb,
     'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20'),
    ('STRATEGY', 'publishing.safety',
     '{"contentPublishing":false,"socialPublishing":false,"outreachSending":false,"deployment":false,"sitemapSubmission":false}'::jsonb,
     'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20'),
    ('STRATEGY', 'backlink.policy',
     '{"mode":"FREE_OR_EARNED_ONLY","paidLinks":false,"pbn":false,"linkFarms":false,"automatedOutreach":false,"status":"OWNER_REVIEW_REQUIRED"}'::jsonb,
     'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20')
ON CONFLICT (agent_type, setting_key) DO UPDATE SET
    setting_value = EXCLUDED.setting_value,
    updated_by = EXCLUDED.updated_by,
    updated_at = CURRENT_TIMESTAMP;

UPDATE agent_settings
SET setting_value = jsonb_set(
        setting_value #- '{weights,existingReadyRoadAuthority}',
        '{weights,existingRijViaAuthority}',
        COALESCE(setting_value #> '{weights,existingReadyRoadAuthority}', '5'::jsonb),
        TRUE
    ),
    updated_by = 'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20',
    updated_at = CURRENT_TIMESTAMP
WHERE agent_type = 'EDITORIAL'
  AND setting_key = 'priority.scoring'
  AND setting_value #> '{weights,existingReadyRoadAuthority}' IS NOT NULL;

INSERT INTO audit_logs (
    event_type, actor, entity_type, entity_id, correlation_id, safe_details
)
VALUES (
    'RIJVIA_LOCAL_MARKETING_STRATEGY_CONFIGURED',
    'RIJVIA_LOCAL_MARKETING_PLAN_2026_08_20',
    'MARKETING_STRATEGY',
    'RIJVIA',
    'rijvia-local-marketing-plan-2026-08-20',
    '{"productionMutation":false,"canonicalActivation":false,"publishing":false}'::jsonb
);
