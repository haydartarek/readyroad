UPDATE agent_settings
SET setting_value = jsonb_set(
        setting_value,
        '{siteUrl}',
        '"sc-domain:rijvia.be"'::jsonb,
        TRUE
    ),
    updated_by = 'RIJVIA_CANONICAL_DOMAIN_2026_08_27',
    updated_at = CURRENT_TIMESTAMP
WHERE agent_type = 'ANALYTICS'
  AND setting_key = 'google.searchConsole';

UPDATE agent_settings
SET setting_value = (setting_value - 'sourceDomain' - 'candidateDomain') || jsonb_build_object(
        'currentDomain', 'rijvia.be',
        'canonicalDomain', 'rijvia.be'
    ),
    updated_by = 'RIJVIA_CANONICAL_DOMAIN_2026_08_27',
    updated_at = CURRENT_TIMESTAMP
WHERE agent_type = 'STRATEGY'
  AND setting_key = 'seo.migration';

UPDATE editorial_sources
SET source_type = 'RIJVIA_CORE_DATA',
    updated_at = CURRENT_TIMESTAMP
WHERE source_type = 'READYROAD_CORE_DATA';

ALTER TABLE editorial_sources
    DROP CONSTRAINT IF EXISTS editorial_sources_source_type_check;

ALTER TABLE editorial_sources
    ADD CONSTRAINT editorial_sources_source_type_check CHECK (source_type IN (
        'RIJVIA_CORE_DATA',
        'OFFICIAL_LEGAL_SOURCE',
        'OFFICIAL_GOVERNMENT_SOURCE',
        'OFFICIAL_PUBLIC_AUTHORITY_SOURCE',
        'APPROVED_INTERNAL_SOURCE',
        'APPROVED_REFERENCE_SOURCE'
    ));

UPDATE seo_query_snapshots
SET brand_classification = 'LEGACY_BRAND_QUERY'
WHERE brand_classification = 'OLD_BRAND_READYROAD';

UPDATE seo_opportunities
SET brand_classification = 'LEGACY_BRAND_QUERY'
WHERE brand_classification = 'OLD_BRAND_READYROAD';

ALTER TABLE seo_query_snapshots
    DROP CONSTRAINT IF EXISTS seo_query_snapshots_brand_classification_check;

ALTER TABLE seo_query_snapshots
    ADD CONSTRAINT seo_query_snapshots_brand_classification_check CHECK (
        brand_classification IN (
            'OWN_BRAND',
            'OWN_BRAND_RIJVIA',
            'LEGACY_BRAND_QUERY',
            'NON_BRAND',
            'COMPETITOR_OR_AMBIGUOUS_BRAND'
        )
    );

INSERT INTO audit_logs (
    event_type, actor, entity_type, entity_id, correlation_id, safe_details
)
VALUES (
    'RIJVIA_CANONICAL_DOMAIN_FINALIZED',
    'RIJVIA_CANONICAL_DOMAIN_2026_08_27',
    'MARKETING_PLATFORM',
    'RIJVIA',
    'rijvia-canonical-domain-finalized-v56',
    '{"canonicalDomain":"rijvia.be","activeSettingsUpdated":true,"historicalEvidenceChanged":false}'::jsonb
);
