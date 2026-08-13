CREATE TABLE editorial_sources (
    id BIGSERIAL PRIMARY KEY,
    source_type VARCHAR(48) NOT NULL CHECK (
        source_type IN (
            'READYROAD_CORE_DATA',
            'OFFICIAL_LEGAL_SOURCE',
            'OFFICIAL_GOVERNMENT_SOURCE',
            'OFFICIAL_PUBLIC_AUTHORITY_SOURCE',
            'APPROVED_INTERNAL_SOURCE',
            'APPROVED_REFERENCE_SOURCE'
        )
    ),
    location_type VARCHAR(16) NOT NULL CHECK (location_type IN ('INTERNAL', 'EXTERNAL')),
    title TEXT NOT NULL,
    publisher VARCHAR(255) NOT NULL,
    canonical_url TEXT,
    internal_reference VARCHAR(512),
    jurisdiction VARCHAR(128),
    language VARCHAR(8) NOT NULL CHECK (language IN ('AR', 'NL', 'FR', 'EN', 'UNKNOWN')),
    verification_status VARCHAR(32) NOT NULL CHECK (
        verification_status IN ('UNVERIFIED', 'VERIFIED', 'REQUIRES_REVIEW', 'REJECTED', 'STALE')
    ),
    trust_status VARCHAR(32) NOT NULL CHECK (
        trust_status IN ('CORE_TRUSTED', 'OFFICIAL', 'APPROVED_REFERENCE', 'UNTRUSTED')
    ),
    legal_review_required BOOLEAN NOT NULL DEFAULT FALSE,
    legal_review_status VARCHAR(32) NOT NULL CHECK (
        legal_review_status IN ('NOT_REQUIRED', 'REQUIRES_REVIEW', 'VERIFIED', 'REJECTED', 'STALE')
    ),
    verified_at TIMESTAMPTZ,
    verified_by VARCHAR(160),
    last_checked_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    content_fingerprint VARCHAR(128),
    etag VARCHAR(512),
    last_modified VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_editorial_sources_location CHECK (
        (location_type = 'INTERNAL' AND internal_reference IS NOT NULL AND canonical_url IS NULL)
        OR
        (location_type = 'EXTERNAL' AND canonical_url IS NOT NULL AND internal_reference IS NULL)
    ),
    CONSTRAINT chk_editorial_sources_verifier CHECK (
        (verification_status = 'VERIFIED' AND verified_at IS NOT NULL AND verified_by IS NOT NULL)
        OR verification_status <> 'VERIFIED'
    )
);

CREATE UNIQUE INDEX uq_editorial_sources_canonical_url
    ON editorial_sources (canonical_url)
    WHERE location_type = 'EXTERNAL';

CREATE UNIQUE INDEX uq_editorial_sources_internal_identity
    ON editorial_sources (source_type, internal_reference)
    WHERE location_type = 'INTERNAL';

CREATE INDEX idx_editorial_sources_status
    ON editorial_sources (active, verification_status, trust_status);

CREATE TABLE editorial_source_versions (
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT NOT NULL REFERENCES editorial_sources(id) ON DELETE RESTRICT,
    version_number INTEGER NOT NULL CHECK (version_number > 0),
    content_fingerprint VARCHAR(128) NOT NULL,
    etag VARCHAR(512),
    last_modified VARCHAR(255),
    verification_status VARCHAR(32) NOT NULL CHECK (
        verification_status IN ('UNVERIFIED', 'VERIFIED', 'REQUIRES_REVIEW', 'REJECTED', 'STALE')
    ),
    trust_status VARCHAR(32) NOT NULL CHECK (
        trust_status IN ('CORE_TRUSTED', 'OFFICIAL', 'APPROVED_REFERENCE', 'UNTRUSTED')
    ),
    legal_review_status VARCHAR(32) NOT NULL CHECK (
        legal_review_status IN ('NOT_REQUIRED', 'REQUIRES_REVIEW', 'VERIFIED', 'REJECTED', 'STALE')
    ),
    checked_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verified_by VARCHAR(160),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_editorial_source_version UNIQUE (source_id, version_number),
    CONSTRAINT uq_editorial_source_fingerprint UNIQUE (source_id, content_fingerprint)
);

CREATE TABLE editorial_claims (
    id BIGSERIAL PRIMARY KEY,
    article_topic_id BIGINT NOT NULL REFERENCES article_topics(id) ON DELETE RESTRICT,
    brief_reference VARCHAR(255) NOT NULL,
    claim_key VARCHAR(128) NOT NULL,
    claim_text TEXT NOT NULL,
    claim_type VARCHAR(32) NOT NULL CHECK (
        claim_type IN ('FACTUAL', 'LEGAL', 'REGIONAL', 'DATE_SENSITIVE', 'STATISTIC', 'PRODUCT_FACT')
    ),
    language VARCHAR(8) NOT NULL CHECK (language IN ('AR', 'NL', 'FR', 'EN')),
    evidence_status VARCHAR(32) NOT NULL CHECK (
        evidence_status IN ('MISSING', 'SUPPORTED', 'REQUIRES_REVIEW', 'REJECTED')
    ),
    legal_review_required BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_editorial_claim_key UNIQUE (article_topic_id, claim_key),
    CONSTRAINT chk_editorial_claim_legal_review CHECK (
        claim_type <> 'LEGAL' OR legal_review_required
    )
);

CREATE INDEX idx_editorial_claims_topic_status
    ON editorial_claims (article_topic_id, evidence_status);

CREATE TABLE editorial_claim_sources (
    claim_id BIGINT NOT NULL REFERENCES editorial_claims(id) ON DELETE RESTRICT,
    source_id BIGINT NOT NULL REFERENCES editorial_sources(id) ON DELETE RESTRICT,
    source_version_id BIGINT REFERENCES editorial_source_versions(id) ON DELETE RESTRICT,
    relationship_status VARCHAR(32) NOT NULL CHECK (
        relationship_status IN ('SUPPORTS', 'REQUIRES_REVIEW', 'REJECTED')
    ),
    evidence_purpose VARCHAR(32) NOT NULL CHECK (
        evidence_purpose IN ('FACTUAL', 'LEGAL', 'CONTEXTUAL', 'PRODUCT', 'STATISTICAL')
    ),
    created_by VARCHAR(160) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (claim_id, source_id)
);

CREATE INDEX idx_editorial_claim_sources_source
    ON editorial_claim_sources (source_id, relationship_status);
