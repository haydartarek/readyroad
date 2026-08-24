-- Forward-only cleanup for active, user-facing marketing identity.
-- Historical audit data and legacy Search Console provenance remain unchanged.

UPDATE marketing_usp
SET title = regexp_replace(title, 'ReadyRoad', 'RijVia', 'gi'),
    description = regexp_replace(description, 'ReadyRoad', 'RijVia', 'gi'),
    evidence_type = CASE
        WHEN evidence_type ILIKE 'READYROAD_FEATURE' THEN 'RIJVIA_PRODUCT_CAPABILITY'
        ELSE regexp_replace(evidence_type, 'ReadyRoad', 'RijVia', 'gi')
    END,
    evidence_reference = regexp_replace(
        regexp_replace(evidence_reference, 'readyroad\.be', 'rijvia.be', 'gi'),
        'ReadyRoad', 'RijVia', 'gi'
    ),
    approved_by = 'RIJVIA_BRAND_IDENTITY_2026_08_25',
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
WHERE active = TRUE
  AND concat_ws(' ', title, description, evidence_type, evidence_reference) ~* 'ReadyRoad';

UPDATE marketing_positioning
SET statement = regexp_replace(statement, 'ReadyRoad', 'RijVia', 'gi'),
    approved_by = 'RIJVIA_BRAND_IDENTITY_2026_08_25',
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
WHERE active = TRUE
  AND statement ~* 'ReadyRoad';

UPDATE marketing_content_pillars
SET name = regexp_replace(name, 'ReadyRoad', 'RijVia', 'gi'),
    approved_by = 'RIJVIA_BRAND_IDENTITY_2026_08_25',
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
WHERE active = TRUE
  AND name ~* 'ReadyRoad';

UPDATE marketing_icp
SET name = regexp_replace(name, 'ReadyRoad', 'RijVia', 'gi'),
    primary_goal = CASE WHEN primary_goal IS NULL THEN NULL
        ELSE regexp_replace(primary_goal, 'ReadyRoad', 'RijVia', 'gi') END,
    main_problem = CASE WHEN main_problem IS NULL THEN NULL
        ELSE regexp_replace(main_problem, 'ReadyRoad', 'RijVia', 'gi') END,
    approved_by = 'RIJVIA_BRAND_IDENTITY_2026_08_25',
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
WHERE active = TRUE
  AND concat_ws(' ', name, primary_goal, main_problem) ~* 'ReadyRoad';

UPDATE marketing_conversion_goals
SET name = regexp_replace(name, 'ReadyRoad', 'RijVia', 'gi'),
    description = CASE WHEN description IS NULL THEN NULL
        ELSE regexp_replace(description, 'ReadyRoad', 'RijVia', 'gi') END,
    primary_cta = regexp_replace(primary_cta, 'ReadyRoad', 'RijVia', 'gi'),
    approved_by = 'RIJVIA_BRAND_IDENTITY_2026_08_25',
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
WHERE active = TRUE
  AND concat_ws(' ', name, description, primary_cta) ~* 'ReadyRoad';

UPDATE social_proof_items
SET claim = regexp_replace(claim, 'ReadyRoad', 'RijVia', 'gi'),
    evidence_reference = regexp_replace(
        regexp_replace(evidence_reference, 'readyroad\.be', 'rijvia.be', 'gi'),
        'ReadyRoad', 'RijVia', 'gi'
    ),
    approved_by = 'RIJVIA_BRAND_IDENTITY_2026_08_25',
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
WHERE active = TRUE
  AND concat_ws(' ', claim, evidence_reference) ~* 'ReadyRoad';

UPDATE agent_definitions
SET display_name = regexp_replace(display_name, 'ReadyRoad', 'RijVia', 'gi'),
    description = CASE WHEN description IS NULL THEN NULL
        ELSE regexp_replace(description, 'ReadyRoad', 'RijVia', 'gi') END,
    updated_at = CURRENT_TIMESTAMP
WHERE concat_ws(' ', display_name, description) ~* 'ReadyRoad';

UPDATE editorial_sources
SET title = regexp_replace(title, 'ReadyRoad', 'RijVia', 'gi'),
    publisher = regexp_replace(publisher, 'ReadyRoad', 'RijVia', 'gi'),
    updated_at = CURRENT_TIMESTAMP
WHERE active = TRUE
  AND concat_ws(' ', title, publisher) ~* 'ReadyRoad';

INSERT INTO audit_logs (
    event_type, actor, entity_type, entity_id, correlation_id, safe_details
)
VALUES (
    'RIJVIA_MARKETING_BRAND_IDENTITY_COMPLETED',
    'RIJVIA_BRAND_IDENTITY_2026_08_25',
    'MARKETING_STRATEGY',
    'RIJVIA',
    'rijvia-marketing-brand-identity-2026-08-25',
    '{"activePresentationDataUpdated":true,"historicalEvidenceChanged":false,"searchConsoleProvenanceChanged":false}'::jsonb
);
