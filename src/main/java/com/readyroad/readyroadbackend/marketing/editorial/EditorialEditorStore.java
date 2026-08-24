package com.readyroad.readyroadbackend.marketing.editorial;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialEditorStore {

    private final JdbcTemplate jdbc;

    List<TopicRow> topics() {
        return jdbc.query("""
                SELECT t.id, t.topic_key, t.official_backlog_order, t.source_type,
                       t.working_title, t.title_language, t.primary_language,
                       t.article_priority, t.source_opportunity_id,
                       t.usp_id, t.icp_id, t.content_pillar_id, t.funnel_stage_id,
                       t.conversion_goal_id,
                       a.id AS article_id, a.lifecycle_state, a.canonical_language
                FROM article_topics t
                LEFT JOIN articles a ON a.article_topic_id = t.id
                ORDER BY t.official_backlog_order, t.id
                """, this::topic);
    }

    List<CurrentVersionRow> currentVersions() {
        return jdbc.query("""
                SELECT article_id, language, version_number, title, slug, status,
                       created_at, created_by
                FROM article_versions
                WHERE is_current
                ORDER BY article_id, language
                """, this::currentVersion);
    }

    TopicSeed lockTopic(long topicId) {
        return jdbc.query("""
                SELECT id, topic_key, status, title_language, primary_language,
                       usp_id, icp_id, content_pillar_id, funnel_stage_id, conversion_goal_id
                FROM article_topics
                WHERE id = ?
                FOR UPDATE
                """, this::topicSeed, topicId).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown article topic: " + topicId));
    }

    ArticleRow findOrCreateArticle(TopicSeed topic) {
        Optional<ArticleRow> existing = articleByTopic(topic.id());
        if (existing.isPresent()) {
            return existing.get();
        }
        String canonicalLanguage = topic.primaryLanguage() == null
                ? topic.titleLanguage()
                : topic.primaryLanguage();
        Long id = jdbc.queryForObject("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language,
                    usp_id, icp_id, content_pillar_id, funnel_stage_id, conversion_goal_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """, Long.class,
                topic.id(), topic.topicKey(), topic.status(), canonicalLanguage,
                topic.uspId(), topic.icpId(), topic.contentPillarId(),
                topic.funnelStageId(), topic.conversionGoalId());
        return articleById(id == null ? -1 : id)
                .orElseThrow(() -> new IllegalStateException("Editorial article was not persisted"));
    }

    AuthoringRow authoringStatus(long topicId) {
        return jdbc.query("""
                SELECT t.id AS topic_id, t.status AS topic_status,
                       a.id AS article_id, a.lifecycle_state,
                       brief.id AS brief_id, brief.status AS brief_status,
                       brief.target_language AS brief_language,
                       count(claim.id)::int AS claims_total,
                       count(claim.id) FILTER (WHERE claim.evidence_status = 'SUPPORTED')::int
                           AS claims_supported,
                       count(claim.id) FILTER (
                           WHERE claim.evidence_status IN ('REQUIRES_REVIEW', 'REJECTED')
                       )::int AS claims_requiring_review,
                       count(claim.id) FILTER (WHERE claim.evidence_status = 'MISSING')::int
                           AS claims_missing
                FROM article_topics t
                LEFT JOIN articles a ON a.article_topic_id = t.id
                LEFT JOIN LATERAL (
                    SELECT id, status, target_language
                    FROM article_briefs
                    WHERE article_topic_id = t.id AND status = 'APPROVED'
                    ORDER BY id DESC
                    LIMIT 1
                ) brief ON TRUE
                LEFT JOIN editorial_claims claim
                    ON claim.article_topic_id = t.id
                   AND (brief.target_language IS NULL OR claim.language = brief.target_language)
                WHERE t.id = ?
                GROUP BY t.id, t.status, a.id, a.lifecycle_state,
                         brief.id, brief.status, brief.target_language
                """, this::authoring, topicId).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown article topic: " + topicId));
    }

    String latestTaskStatus(String taskType, String sourceType, String sourceId) {
        return jdbc.query("""
                SELECT status
                FROM agent_tasks
                WHERE agent_type = 'EDITORIAL'
                  AND task_type = ?
                  AND source_type = ?
                  AND source_id = ?
                ORDER BY id DESC
                LIMIT 1
                """, (result, rowNumber) -> result.getString("status"), taskType, sourceType, sourceId)
                .stream().findFirst().orElse(null);
    }

    private Optional<ArticleRow> articleByTopic(long topicId) {
        return jdbc.query("""
                SELECT id, article_topic_id, lifecycle_state, canonical_language
                FROM articles
                WHERE article_topic_id = ?
                """, this::article, topicId).stream().findFirst();
    }

    private Optional<ArticleRow> articleById(long articleId) {
        return jdbc.query("""
                SELECT id, article_topic_id, lifecycle_state, canonical_language
                FROM articles
                WHERE id = ?
                """, this::article, articleId).stream().findFirst();
    }

    private TopicRow topic(ResultSet result, int rowNumber) throws SQLException {
        Long opportunityId = result.getObject("source_opportunity_id", Long.class);
        Long uspId = result.getObject("usp_id", Long.class);
        String icpId = result.getString("icp_id");
        Long pillarId = result.getObject("content_pillar_id", Long.class);
        Long funnelId = result.getObject("funnel_stage_id", Long.class);
        Long goalId = result.getObject("conversion_goal_id", Long.class);
        String primaryLanguage = result.getString("primary_language");
        return new TopicRow(
                result.getLong("id"), result.getString("topic_key"),
                result.getInt("official_backlog_order"), result.getString("source_type"),
                result.getString("working_title"), result.getString("title_language"),
                primaryLanguage, result.getString("article_priority"),
                uspId != null && icpId != null && pillarId != null && funnelId != null
                        && goalId != null && primaryLanguage != null,
                uspId, icpId, pillarId, funnelId, goalId,
                result.getObject("article_id", Long.class), result.getString("lifecycle_state"),
                result.getString("canonical_language"));
    }

    private AuthoringRow authoring(ResultSet result, int rowNumber) throws SQLException {
        return new AuthoringRow(
                result.getLong("topic_id"), result.getString("topic_status"),
                result.getObject("article_id", Long.class), result.getString("lifecycle_state"),
                result.getObject("brief_id", Long.class), result.getString("brief_status"),
                result.getString("brief_language"), result.getInt("claims_total"),
                result.getInt("claims_supported"), result.getInt("claims_requiring_review"),
                result.getInt("claims_missing"));
    }

    private CurrentVersionRow currentVersion(ResultSet result, int rowNumber) throws SQLException {
        OffsetDateTime createdAt = result.getObject("created_at", OffsetDateTime.class);
        return new CurrentVersionRow(
                result.getLong("article_id"), result.getString("language"),
                result.getInt("version_number"), result.getString("title"),
                result.getString("slug"), result.getString("status"), createdAt.toInstant(),
                result.getString("created_by"));
    }

    private TopicSeed topicSeed(ResultSet result, int rowNumber) throws SQLException {
        return new TopicSeed(
                result.getLong("id"), result.getString("topic_key"), result.getString("status"),
                result.getString("title_language"), result.getString("primary_language"),
                result.getObject("usp_id", Long.class), result.getString("icp_id"),
                result.getObject("content_pillar_id", Long.class),
                result.getObject("funnel_stage_id", Long.class),
                result.getObject("conversion_goal_id", Long.class));
    }

    private ArticleRow article(ResultSet result, int rowNumber) throws SQLException {
        return new ArticleRow(
                result.getLong("id"), result.getLong("article_topic_id"),
                result.getString("lifecycle_state"), result.getString("canonical_language"));
    }

    record TopicRow(
            long id,
            String topicKey,
            int order,
            String sourceType,
            String title,
            String titleLanguage,
            String primaryLanguage,
            String priority,
            boolean strategyContextResolved,
            Long uspId,
            String icpId,
            Long contentPillarId,
            Long funnelStageId,
            Long conversionGoalId,
            Long articleId,
            String lifecycleState,
            String canonicalLanguage) {}

    record AuthoringRow(
            long topicId,
            String topicStatus,
            Long articleId,
            String lifecycleState,
            Long briefId,
            String briefStatus,
            String briefLanguage,
            int claimsTotal,
            int claimsSupported,
            int claimsRequiringReview,
            int claimsMissing) {}

    record CurrentVersionRow(
            long articleId,
            String language,
            int versionNumber,
            String title,
            String slug,
            String status,
            java.time.Instant createdAt,
            String createdBy) {}

    record TopicSeed(
            long id,
            String topicKey,
            String status,
            String titleLanguage,
            String primaryLanguage,
            Long uspId,
            String icpId,
            Long contentPillarId,
            Long funnelStageId,
            Long conversionGoalId) {

        boolean strategyContextResolved() {
            return primaryLanguage != null
                    && uspId != null
                    && icpId != null
                    && contentPillarId != null
                    && funnelStageId != null
                    && conversionGoalId != null;
        }
    }

    record ArticleRow(
            long id,
            long topicId,
            String lifecycleState,
            String canonicalLanguage) {}
}
