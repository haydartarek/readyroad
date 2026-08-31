package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialBriefStore {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    boolean createdByTask(long taskId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM article_briefs WHERE source_task_id = ?)",
                Boolean.class,
                taskId));
    }

    Topic topic(long topicId) {
        return jdbc.query("""
                SELECT id, topic_key, status, pillar, source_type, title_language,
                       keyword_cluster_id,
                       ARRAY(SELECT jsonb_array_elements_text(target_queries)) AS target_queries
                FROM article_topics
                WHERE id = ?
                """, this::mapTopic, topicId).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown article topic: " + topicId));
    }

    Topic lockTopic(long topicId) {
        return jdbc.query("""
                SELECT id, topic_key, status, pillar, source_type, title_language,
                       keyword_cluster_id,
                       ARRAY(SELECT jsonb_array_elements_text(target_queries)) AS target_queries
                FROM article_topics
                WHERE id = ?
                FOR UPDATE
                """, this::mapTopic, topicId).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown article topic: " + topicId));
    }

    void requireNoApprovedBrief(long topicId, String language) {
        boolean exists = Boolean.TRUE.equals(jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM article_briefs
                    WHERE article_topic_id = ? AND target_language = ? AND status = 'APPROVED'
                )
                """, Boolean.class, topicId, language));
        if (exists) {
            throw new IllegalStateException("An approved brief already exists for this topic and language");
        }
    }

    long createOrBindArticle(
            Topic topic,
            String language,
            MarketingStrategyContextRequest strategy) {
        Optional<ArticleContext> existing = articleByTopic(topic.id());
        if (existing.isPresent()) {
            ArticleContext article = existing.get();
            if (!List.of("PLANNED", "BRIEF_READY").contains(article.state())) {
                throw new IllegalStateException(
                        "Article strategy cannot change in lifecycle state " + article.state());
            }
            requireCompatible(article, language, strategy);
            jdbc.update("""
                    UPDATE articles
                    SET canonical_language = ?, usp_id = ?, icp_id = ?, content_pillar_id = ?,
                        funnel_stage_id = ?, conversion_goal_id = ?, updated_at = CURRENT_TIMESTAMP
                    WHERE id = ?
                    """,
                    language, strategy.uspId(), strategy.icpId(), strategy.contentPillarId(),
                    strategy.funnelStageId(), strategy.conversionGoalId(), article.id());
            return article.id();
        }

        Long articleId = jdbc.queryForObject("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language,
                    usp_id, icp_id, content_pillar_id, funnel_stage_id, conversion_goal_id
                ) VALUES (?, ?, 'PLANNED', ?, ?, ?, ?, ?, ?)
                RETURNING id
                """, Long.class,
                topic.id(), topic.topicKey(), language, strategy.uspId(), strategy.icpId(),
                strategy.contentPillarId(), strategy.funnelStageId(), strategy.conversionGoalId());
        if (articleId == null) {
            throw new IllegalStateException("Editorial article was not persisted");
        }
        return articleId;
    }

    long insertApprovedBrief(
            Topic topic,
            long taskId,
            String language,
            EditorialBriefDtos.CreateRequest request,
            String primaryCta) {
        MarketingStrategyContextRequest strategy = request.strategyContext();
        Long briefId = jdbc.queryForObject("""
                INSERT INTO article_briefs (
                    article_topic_id, keyword_cluster_id, target_language,
                    search_intent, working_title, purpose,
                    usp_id, icp_id, content_pillar_id, funnel_stage_id, conversion_goal_id,
                    primary_cta, target_queries, source_requirements, legal_review_required,
                    status, source_task_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?, 'APPROVED', ?)
                RETURNING id
                """, Long.class,
                topic.id(), topic.keywordClusterIdFor(language), language,
                request.searchIntent().trim(), request.workingTitle().trim(),
                request.purpose().trim(), strategy.uspId(), strategy.icpId(),
                strategy.contentPillarId(), strategy.funnelStageId(), strategy.conversionGoalId(),
                primaryCta, json(request.targetQueries()), json(request.sourceRequirements()),
                request.legalReviewRequired(), taskId);
        if (briefId == null) {
            throw new IllegalStateException("Editorial brief was not persisted");
        }
        return briefId;
    }

    void bindTopic(
            Topic topic,
            String language,
            EditorialBriefDtos.CreateRequest request) {
        MarketingStrategyContextRequest strategy = request.strategyContext();
        jdbc.update("""
                UPDATE article_topics
                SET primary_language = ?, usp_id = ?, icp_id = ?, content_pillar_id = ?,
                    funnel_stage_id = ?, conversion_goal_id = ?,
                    target_queries = CASE
                        WHEN title_language = ? THEN ?::jsonb
                        ELSE target_queries
                    END,
                    status = 'BRIEF_READY', updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """,
                language, strategy.uspId(), strategy.icpId(), strategy.contentPillarId(),
                strategy.funnelStageId(), strategy.conversionGoalId(),
                language, json(request.targetQueries()), topic.id());
    }

    private Optional<ArticleContext> articleByTopic(long topicId) {
        return jdbc.query("""
                SELECT id, lifecycle_state, canonical_language, usp_id, icp_id,
                       content_pillar_id, funnel_stage_id, conversion_goal_id
                FROM articles
                WHERE article_topic_id = ?
                FOR UPDATE
                """, this::articleContext, topicId).stream().findFirst();
    }

    private static void requireCompatible(
            ArticleContext article,
            String language,
            MarketingStrategyContextRequest strategy) {
        boolean compatible = nullableMatches(article.language(), language)
                && nullableMatches(article.uspId(), strategy.uspId())
                && nullableMatches(article.icpId(), strategy.icpId())
                && nullableMatches(article.pillarId(), strategy.contentPillarId())
                && nullableMatches(article.funnelId(), strategy.funnelStageId())
                && nullableMatches(article.goalId(), strategy.conversionGoalId());
        if (!compatible) {
            throw new IllegalStateException("Existing article has a different Strategy Context");
        }
    }

    private static boolean nullableMatches(Object current, Object requested) {
        return current == null || current.equals(requested);
    }

    private Topic mapTopic(ResultSet result, int rowNumber) throws SQLException {
        java.sql.Array queryArray = result.getArray("target_queries");
        String[] queryValues = queryArray == null ? new String[0] : (String[]) queryArray.getArray();
        return new Topic(
                result.getLong("id"), result.getString("topic_key"),
                result.getString("status"), result.getBoolean("pillar"),
                result.getString("source_type"), result.getString("title_language"),
                result.getObject("keyword_cluster_id", Long.class), List.of(queryValues));
    }

    private ArticleContext articleContext(ResultSet result, int rowNumber) throws SQLException {
        return new ArticleContext(
                result.getLong("id"), result.getString("lifecycle_state"),
                result.getString("canonical_language"), result.getObject("usp_id", Long.class),
                result.getString("icp_id"), result.getObject("content_pillar_id", Long.class),
                result.getObject("funnel_stage_id", Long.class),
                result.getObject("conversion_goal_id", Long.class));
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException error) {
            throw new IllegalArgumentException("Editorial brief list cannot be serialized", error);
        }
    }

    record Topic(
            long id,
            String topicKey,
            String status,
            boolean pillar,
            String sourceType,
            String titleLanguage,
            Long keywordClusterId,
            List<String> targetQueries) {

        boolean official() {
            return "OFFICIAL_STRATEGIC_BACKLOG".equals(sourceType);
        }

        Long keywordClusterIdFor(String language) {
            return titleLanguage.equals(language) ? keywordClusterId : null;
        }
    }

    private record ArticleContext(
            long id,
            String state,
            String language,
            Long uspId,
            String icpId,
            Long pillarId,
            Long funnelId,
            Long goalId) {}
}
