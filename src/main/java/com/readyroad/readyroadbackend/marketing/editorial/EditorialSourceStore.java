package com.readyroad.readyroadbackend.marketing.editorial;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialSourceStore {

    private final JdbcTemplate jdbc;

    boolean collectionCompleted(long taskId) {
        Integer count = jdbc.queryForObject("""
                SELECT count(*) FROM audit_logs
                WHERE task_id = ? AND event_type = 'EDITORIAL_SOURCE_COLLECTION_COMPLETED'
                """, Integer.class, taskId);
        return count != null && count > 0;
    }

    void requireTopic(long topicId) {
        Integer count = jdbc.queryForObject(
                "SELECT count(*) FROM article_topics WHERE id = ?", Integer.class, topicId);
        if (count == null || count == 0) {
            throw new IllegalArgumentException("Unknown article topic: " + topicId);
        }
    }

    StoredSource registerOrRefresh(
            EditorialSourceDtos.SourceInput input,
            String approvedBy,
            EditorialSourcePolicy policy) {
        String sourceType = EditorialSourcePolicy.upper(input.sourceType());
        String locationType = EditorialSourcePolicy.upper(input.locationType());
        String canonicalUrl = "EXTERNAL".equals(locationType) ? policy.canonicalUrl(input.url()) : null;
        String internalReference = "INTERNAL".equals(locationType) ? input.internalReference().trim() : null;
        String verification = EditorialSourcePolicy.upper(input.verificationStatus());
        String trust = EditorialSourcePolicy.upper(input.trustStatus());
        String legal = EditorialSourcePolicy.upper(input.legalReviewStatus());
        String fingerprint = blankToNull(input.fingerprint());

        Long insertedId = insertIfMissing(
                input, approvedBy, sourceType, locationType, canonicalUrl, internalReference,
                verification, trust, legal, fingerprint);
        StoredSource source = insertedId == null
                ? lockExisting(sourceType, locationType, canonicalUrl, internalReference)
                : requireById(insertedId);
        if (insertedId != null) {
            Long versionId = ensureVersion(source, fingerprint, approvedBy);
            return source.withVersion(versionId, false);
        }

        boolean fingerprintChanged = source.contentFingerprint() != null
                && fingerprint != null
                && !source.contentFingerprint().equals(fingerprint);
        String effectiveVerification = fingerprintChanged ? "STALE" : verification;
        String effectiveLegal = fingerprintChanged && (source.legalReviewRequired() || input.legalReviewRequired())
                ? "STALE"
                : legal;
        String effectiveFingerprint = fingerprint == null ? source.contentFingerprint() : fingerprint;
        boolean verified = "VERIFIED".equals(effectiveVerification);
        jdbc.update("""
                UPDATE editorial_sources
                SET title = ?, publisher = ?, jurisdiction = ?, language = ?,
                    verification_status = ?, trust_status = ?, legal_review_required = ?,
                    legal_review_status = ?, verified_at = CASE WHEN ? THEN CURRENT_TIMESTAMP ELSE NULL END,
                    verified_by = CASE WHEN ? THEN ? ELSE NULL END,
                    last_checked_at = CURRENT_TIMESTAMP, content_fingerprint = ?, etag = ?,
                    last_modified = ?, active = TRUE, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """,
                input.title().trim(), input.publisher().trim(), blankToNull(input.jurisdiction()),
                EditorialSourcePolicy.upper(input.language()), effectiveVerification, trust,
                input.legalReviewRequired(), effectiveLegal, verified, verified, approvedBy,
                effectiveFingerprint, blankToNull(input.etag()), blankToNull(input.lastModified()), source.id());
        StoredSource refreshed = requireById(source.id());
        Long versionId = ensureVersion(refreshed, fingerprint, approvedBy);
        if (fingerprintChanged) {
            jdbc.update("""
                    UPDATE editorial_claim_sources
                    SET relationship_status = 'REQUIRES_REVIEW', updated_at = CURRENT_TIMESTAMP
                    WHERE source_id = ?
                    """, source.id());
            jdbc.update("""
                    UPDATE editorial_claims claim
                    SET evidence_status = CASE
                            WHEN EXISTS (
                                SELECT 1 FROM editorial_claim_sources link
                                WHERE link.claim_id = claim.id AND link.relationship_status = 'SUPPORTS'
                            ) THEN 'SUPPORTED'
                            ELSE 'REQUIRES_REVIEW'
                        END,
                        updated_at = CURRENT_TIMESTAMP
                    WHERE EXISTS (
                        SELECT 1 FROM editorial_claim_sources link
                        WHERE link.claim_id = claim.id AND link.source_id = ?
                    )
                    """, source.id());
        }
        return refreshed.withVersion(versionId, fingerprintChanged);
    }

    long upsertClaim(long topicId, String briefReference, EditorialSourceDtos.ClaimInput claim) {
        return jdbc.queryForObject("""
                INSERT INTO editorial_claims (
                    article_topic_id, brief_reference, claim_key, claim_text,
                    claim_type, language, evidence_status, legal_review_required
                ) VALUES (?, ?, ?, ?, ?, ?, 'MISSING', ?)
                ON CONFLICT (article_topic_id, claim_key) DO UPDATE SET
                    brief_reference = EXCLUDED.brief_reference,
                    claim_text = EXCLUDED.claim_text,
                    claim_type = EXCLUDED.claim_type,
                    language = EXCLUDED.language,
                    legal_review_required = EXCLUDED.legal_review_required,
                    updated_at = CURRENT_TIMESTAMP
                RETURNING id
                """, Long.class,
                topicId, briefReference.trim(), claim.claimKey().trim(), claim.claimText().trim(),
                EditorialSourcePolicy.upper(claim.claimType()), EditorialSourcePolicy.upper(claim.language()),
                claim.legalReviewRequired());
    }

    void linkClaim(
            long claimId,
            StoredSource source,
            String relationshipStatus,
            String evidencePurpose,
            String actor) {
        jdbc.update("""
                INSERT INTO editorial_claim_sources (
                    claim_id, source_id, source_version_id, relationship_status,
                    evidence_purpose, created_by
                ) VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (claim_id, source_id) DO UPDATE SET
                    source_version_id = EXCLUDED.source_version_id,
                    relationship_status = EXCLUDED.relationship_status,
                    evidence_purpose = EXCLUDED.evidence_purpose,
                    updated_at = CURRENT_TIMESTAMP
                """, claimId, source.id(), source.versionId(), relationshipStatus, evidencePurpose, actor);
    }

    String refreshClaimEvidence(long claimId) {
        String status = jdbc.queryForObject("""
                SELECT CASE
                    WHEN count(*) = 0 THEN 'MISSING'
                    WHEN count(*) FILTER (WHERE relationship_status = 'SUPPORTS') > 0 THEN 'SUPPORTED'
                    WHEN count(*) FILTER (WHERE relationship_status <> 'REJECTED') = 0 THEN 'REJECTED'
                    ELSE 'REQUIRES_REVIEW'
                END
                FROM editorial_claim_sources
                WHERE claim_id = ?
                """, String.class, claimId);
        jdbc.update("""
                UPDATE editorial_claims
                SET evidence_status = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """, status, claimId);
        return status;
    }

    List<EditorialSourceDtos.Source> list(Long articleTopicId) {
        String filter = articleTopicId == null ? "" : "WHERE claim.article_topic_id = ? ";
        Object[] parameters = articleTopicId == null ? new Object[0] : new Object[] {articleTopicId};
        return jdbc.query("""
                SELECT source.id, source.source_type, source.location_type, source.title,
                       source.publisher, source.canonical_url, source.internal_reference,
                       source.jurisdiction, source.language, source.verification_status,
                       source.trust_status, source.legal_review_required,
                       source.legal_review_status, source.verified_at, source.verified_by,
                       source.last_checked_at, source.content_fingerprint, source.active,
                       count(DISTINCT link.claim_id) AS claim_count
                FROM editorial_sources source
                LEFT JOIN editorial_claim_sources link ON link.source_id = source.id
                LEFT JOIN editorial_claims claim ON claim.id = link.claim_id
                """ + filter + """
                GROUP BY source.id
                ORDER BY source.active DESC, source.last_checked_at DESC, source.id DESC
                """, (result, rowNumber) -> new EditorialSourceDtos.Source(
                result.getLong("id"), result.getString("source_type"), result.getString("location_type"),
                result.getString("title"), result.getString("publisher"), result.getString("canonical_url"),
                result.getString("internal_reference"), result.getString("jurisdiction"),
                result.getString("language"), result.getString("verification_status"),
                result.getString("trust_status"), result.getBoolean("legal_review_required"),
                result.getString("legal_review_status"), instant(result, "verified_at"),
                result.getString("verified_by"), instant(result, "last_checked_at"),
                result.getString("content_fingerprint"), result.getBoolean("active"),
                result.getLong("claim_count")), parameters);
    }

    private Long insertIfMissing(
            EditorialSourceDtos.SourceInput input,
            String approvedBy,
            String sourceType,
            String locationType,
            String canonicalUrl,
            String internalReference,
            String verification,
            String trust,
            String legal,
            String fingerprint) {
        boolean verified = "VERIFIED".equals(verification);
        String conflict = "EXTERNAL".equals(locationType)
                ? "ON CONFLICT (canonical_url) WHERE location_type = 'EXTERNAL' DO NOTHING"
                : "ON CONFLICT (source_type, internal_reference) WHERE location_type = 'INTERNAL' DO NOTHING";
        List<Long> inserted = jdbc.query("""
                INSERT INTO editorial_sources (
                    source_type, location_type, title, publisher, canonical_url,
                    internal_reference, jurisdiction, language, verification_status,
                    trust_status, legal_review_required, legal_review_status,
                    verified_at, verified_by, content_fingerprint, etag, last_modified
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                          CASE WHEN ? THEN CURRENT_TIMESTAMP ELSE NULL END,
                          CASE WHEN ? THEN ? ELSE NULL END, ?, ?, ?)
                """ + conflict + " RETURNING id",
                (result, rowNumber) -> result.getLong("id"),
                sourceType, locationType, input.title().trim(), input.publisher().trim(), canonicalUrl,
                internalReference, blankToNull(input.jurisdiction()),
                EditorialSourcePolicy.upper(input.language()), verification, trust,
                input.legalReviewRequired(), legal, verified, verified, approvedBy, fingerprint,
                blankToNull(input.etag()), blankToNull(input.lastModified()));
        return inserted.isEmpty() ? null : inserted.getFirst();
    }

    private StoredSource lockExisting(
            String sourceType, String locationType, String canonicalUrl, String internalReference) {
        String sql = "EXTERNAL".equals(locationType)
                ? "SELECT * FROM editorial_sources WHERE canonical_url = ? FOR UPDATE"
                : "SELECT * FROM editorial_sources WHERE source_type = ? AND internal_reference = ? FOR UPDATE";
        Object[] parameters = "EXTERNAL".equals(locationType)
                ? new Object[] {canonicalUrl}
                : new Object[] {sourceType, internalReference};
        return jdbc.query(sql, EditorialSourceStore::storedSource, parameters).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Source identity disappeared during registration"));
    }

    private StoredSource requireById(long sourceId) {
        return jdbc.query("SELECT * FROM editorial_sources WHERE id = ?", EditorialSourceStore::storedSource, sourceId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown source: " + sourceId));
    }

    private Long ensureVersion(StoredSource source, String fingerprint, String approvedBy) {
        if (fingerprint == null) {
            return null;
        }
        List<Long> existing = jdbc.query("""
                SELECT id FROM editorial_source_versions
                WHERE source_id = ? AND content_fingerprint = ?
                """, (result, rowNumber) -> result.getLong("id"), source.id(), fingerprint);
        if (!existing.isEmpty()) {
            return existing.getFirst();
        }
        return jdbc.queryForObject("""
                INSERT INTO editorial_source_versions (
                    source_id, version_number, content_fingerprint, etag, last_modified,
                    verification_status, trust_status, legal_review_status, verified_by
                ) VALUES (
                    ?, COALESCE((SELECT max(version_number) + 1 FROM editorial_source_versions WHERE source_id = ?), 1),
                    ?, ?, ?, ?, ?, ?, ?
                )
                ON CONFLICT (source_id, content_fingerprint) DO UPDATE
                    SET checked_at = CURRENT_TIMESTAMP
                RETURNING id
                """, Long.class, source.id(), source.id(), fingerprint, source.etag(), source.lastModified(),
                source.verificationStatus(), source.trustStatus(), source.legalReviewStatus(), approvedBy);
    }

    private static StoredSource storedSource(ResultSet result, int rowNumber) throws SQLException {
        return new StoredSource(
                result.getLong("id"), result.getString("source_type"), result.getString("location_type"),
                result.getString("verification_status"), result.getString("trust_status"),
                result.getBoolean("legal_review_required"), result.getString("legal_review_status"),
                result.getString("content_fingerprint"), result.getString("etag"),
                result.getString("last_modified"), result.getBoolean("active"), null, false);
    }

    private static java.time.Instant instant(ResultSet result, String column) throws SQLException {
        OffsetDateTime value = result.getObject(column, OffsetDateTime.class);
        return value == null ? null : value.toInstant();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    record StoredSource(
            long id,
            String sourceType,
            String locationType,
            String verificationStatus,
            String trustStatus,
            boolean legalReviewRequired,
            String legalReviewStatus,
            String contentFingerprint,
            String etag,
            String lastModified,
            boolean active,
            Long versionId,
            boolean fingerprintChanged) {

        StoredSource withVersion(Long value, boolean changed) {
            return new StoredSource(
                    id, sourceType, locationType, verificationStatus, trustStatus,
                    legalReviewRequired, legalReviewStatus, contentFingerprint,
                    etag, lastModified, active, value, changed);
        }
    }
}
