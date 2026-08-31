package com.readyroad.readyroadbackend.marketing.editorial;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialDraftStore {

    private final JdbcTemplate jdbc;

    void requireArticle(long articleId) {
        boolean exists = Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM articles WHERE id = ?)", Boolean.class, articleId));
        if (!exists) {
            throw new IllegalArgumentException("Unknown article: " + articleId);
        }
    }

    Optional<Long> versionCreatedByTask(long taskId) {
        return jdbc.query("""
                SELECT id FROM article_versions WHERE generated_by_task_id = ?
                """, (result, rowNumber) -> result.getLong("id"), taskId).stream().findFirst();
    }

    DraftContext lockContext(long articleId) {
        return jdbc.query("""
                SELECT a.id AS article_id, a.article_topic_id, a.lifecycle_state,
                       a.canonical_language, a.usp_id, a.icp_id, a.content_pillar_id,
                       a.funnel_stage_id, a.conversion_goal_id,
                       t.pillar, b.id AS brief_id, b.working_title, b.purpose,
                       b.search_intent, b.primary_cta,
                       b.target_queries ->> 0 AS focus_keyword, b.legal_review_required
                FROM articles a
                JOIN article_topics t ON t.id = a.article_topic_id
                JOIN article_briefs b ON b.article_topic_id = a.article_topic_id
                    AND b.target_language = a.canonical_language
                    AND b.status = 'APPROVED'
                WHERE a.id = ?
                ORDER BY b.id DESC
                LIMIT 1
                FOR UPDATE OF a
                """, this::context, articleId).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "An approved canonical-language brief is required before draft creation"));
    }

    ClaimSummary claimSummary(long topicId, String language) {
        return jdbc.queryForObject("""
                SELECT count(*) AS total,
                       count(*) FILTER (WHERE evidence_status = 'SUPPORTED') AS supported
                FROM editorial_claims
                WHERE article_topic_id = ? AND language = ?
                """, (result, rowNumber) -> new ClaimSummary(
                        result.getInt("total"), result.getInt("supported")),
                topicId, language);
    }

    List<EvidenceRow> verifiedEvidence(long topicId, String language) {
        return jdbc.query("""
                SELECT claim.id AS claim_id, claim.claim_key, claim.claim_text, claim.claim_type,
                       source.source_type, source.title AS source_title,
                       COALESCE(source.internal_reference, source.canonical_url) AS source_reference,
                       source.trust_status, source.legal_review_status
                FROM editorial_claims claim
                JOIN editorial_claim_sources relation ON relation.claim_id = claim.id
                    AND relation.relationship_status = 'SUPPORTS'
                JOIN editorial_sources source ON source.id = relation.source_id
                    AND source.active
                    AND source.verification_status = 'VERIFIED'
                    AND source.trust_status IN ('CORE_TRUSTED', 'OFFICIAL', 'APPROVED_REFERENCE')
                LEFT JOIN editorial_source_versions source_version
                    ON source_version.id = relation.source_version_id
                WHERE claim.article_topic_id = ?
                  AND claim.language = ?
                  AND claim.evidence_status = 'SUPPORTED'
                  AND (
                      relation.source_version_id IS NULL
                      OR source_version.verification_status = 'VERIFIED'
                  )
                ORDER BY claim.id, source.id
                """, this::evidence, topicId, language);
    }

    boolean duplicateFingerprint(long articleId, String fingerprint) {
        return Boolean.TRUE.equals(jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM article_versions
                    WHERE article_id <> ?
                      AND generation_metadata ->> 'contentFingerprint' = ?
                )
                """, Boolean.class, articleId, fingerprint));
    }

    void bindGeneratedTask(long versionId, long taskId) {
        int updated = jdbc.update("""
                UPDATE article_versions
                SET generated_by_task_id = ?
                WHERE id = ? AND generated_by_task_id IS NULL
                """, taskId, versionId);
        if (updated != 1) {
            throw new IllegalStateException("Generated article version task trace could not be stored");
        }
    }

    private DraftContext context(ResultSet result, int rowNumber) throws SQLException {
        return new DraftContext(
                result.getLong("article_id"), result.getLong("article_topic_id"),
                result.getString("lifecycle_state"), result.getString("canonical_language"),
                result.getObject("usp_id", Long.class), result.getString("icp_id"),
                result.getObject("content_pillar_id", Long.class),
                result.getObject("funnel_stage_id", Long.class),
                result.getObject("conversion_goal_id", Long.class), result.getBoolean("pillar"),
                result.getLong("brief_id"), result.getString("working_title"),
                result.getString("purpose"), result.getString("search_intent"),
                result.getString("primary_cta"), result.getString("focus_keyword"),
                result.getBoolean("legal_review_required"));
    }

    private EvidenceRow evidence(ResultSet result, int rowNumber) throws SQLException {
        return new EvidenceRow(
                result.getLong("claim_id"), result.getString("claim_key"),
                result.getString("claim_text"), result.getString("claim_type"),
                result.getString("source_type"), result.getString("source_title"),
                result.getString("source_reference"), result.getString("trust_status"),
                result.getString("legal_review_status"));
    }

    record DraftContext(
            long articleId,
            long topicId,
            String state,
            String language,
            Long uspId,
            String icpId,
            Long pillarId,
            Long funnelId,
            Long goalId,
            boolean pillar,
            long briefId,
            String workingTitle,
            String purpose,
            String searchIntent,
            String primaryCta,
            String focusKeyword,
            boolean legalReviewRequired) {}

    record ClaimSummary(int total, int supported) {}

    record EvidenceRow(
            long claimId,
            String claimKey,
            String claimText,
            String claimType,
            String sourceType,
            String sourceTitle,
            String sourceReference,
            String trustStatus,
            String legalReviewStatus) {}
}
