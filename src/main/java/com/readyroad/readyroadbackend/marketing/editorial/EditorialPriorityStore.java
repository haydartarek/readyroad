package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialPriorityStore {

    private final JdbcTemplate jdbc;

    List<TopicEvidence> loadEvidence() {
        return jdbc.query("""
                SELECT t.id, t.topic_key, t.official_backlog_order,
                       t.source_opportunity_id, t.content_pillar_id, t.funnel_stage_id,
                       t.conversion_goal_id, t.supporting_pages, t.internal_link_targets,
                       EXISTS (
                           SELECT 1 FROM marketing_content_pillars p
                           WHERE p.id = t.content_pillar_id AND p.active
                       ) AS active_content_pillar,
                       EXISTS (
                           SELECT 1 FROM marketing_funnel_stages f
                           WHERE f.id = t.funnel_stage_id AND f.active
                       ) AS active_funnel_stage,
                       EXISTS (
                           SELECT 1 FROM marketing_conversion_goals c
                           WHERE c.id = t.conversion_goal_id AND c.active
                       ) AS active_conversion_goal,
                       o.state, o.impressions, o.relevance, o.cannibalization,
                       o.long_tail, o.search_intent, o.language,
                       EXISTS (
                           SELECT 1 FROM seo_content_gaps g
                           WHERE g.query = o.query AND g.language = o.language
                             AND g.status IN ('DISCOVERED', 'REVIEWED')
                       ) AS content_gap
                FROM article_topics t
                LEFT JOIN seo_opportunities o ON o.id = t.source_opportunity_id
                ORDER BY t.official_backlog_order
                """, (result, rowNumber) -> new TopicEvidence(
                result.getLong("id"),
                result.getString("topic_key"),
                result.getInt("official_backlog_order"),
                result.getObject("source_opportunity_id", Long.class),
                result.getObject("content_pillar_id", Long.class),
                result.getObject("funnel_stage_id", Long.class),
                result.getObject("conversion_goal_id", Long.class),
                result.getString("supporting_pages"),
                result.getString("internal_link_targets"),
                result.getBoolean("active_content_pillar"),
                result.getBoolean("active_funnel_stage"),
                result.getBoolean("active_conversion_goal"),
                result.getString("state"),
                result.getBigDecimal("impressions"),
                result.getObject("relevance", Boolean.class),
                result.getObject("cannibalization", Boolean.class),
                result.getObject("long_tail", Boolean.class),
                result.getString("search_intent"),
                result.getString("language"),
                result.getBoolean("content_gap")));
    }

    void save(
            TopicEvidence topic,
            EditorialPriorityScorer.EditorialPriorityScore score,
            JsonNode factors,
            JsonNode states,
            JsonNode details,
            JsonNode config,
            String reason,
            String calculationKey,
            Long sourceTaskId,
            String triggerType) {
        jdbc.update("""
                INSERT INTO article_priorities (
                    article_topic_id, final_score, priority, priority_reason,
                    factor_scores, evidence_states, evidence_details, scoring_config,
                    search_console_score, search_demand_score, business_relevance_score,
                    calculation_key, source_task_id, trigger_type
                ) VALUES (?, ?, ?, ?, ?::jsonb, ?::jsonb, ?::jsonb, ?::jsonb, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (calculation_key) DO UPDATE SET
                    final_score = EXCLUDED.final_score,
                    priority = EXCLUDED.priority,
                    priority_reason = EXCLUDED.priority_reason,
                    factor_scores = EXCLUDED.factor_scores,
                    evidence_states = EXCLUDED.evidence_states,
                    evidence_details = EXCLUDED.evidence_details,
                    scoring_config = EXCLUDED.scoring_config,
                    search_console_score = EXCLUDED.search_console_score,
                    search_demand_score = EXCLUDED.search_demand_score,
                    business_relevance_score = EXCLUDED.business_relevance_score,
                    calculated_at = CURRENT_TIMESTAMP
                """,
                topic.id(), score.finalScore(), score.priority(), reason,
                factors.toString(), states.toString(), details.toString(), config.toString(),
                score.normalizedScores().get(EditorialPriorityConfig.SEARCH_CONSOLE),
                score.normalizedScores().get(EditorialPriorityConfig.SEARCH_DEMAND),
                score.normalizedScores().get(EditorialPriorityConfig.BUSINESS_RELEVANCE),
                calculationKey, sourceTaskId, triggerType);
        jdbc.update("""
                UPDATE article_topics
                SET article_priority = ?, priority_reason = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """, score.priority(), reason, topic.id());
    }

    List<EditorialDtos.Priority> currentPriorities() {
        return jdbc.query("""
                SELECT DISTINCT ON (p.article_topic_id)
                       t.id, t.topic_key, t.official_backlog_order, t.working_title,
                       p.final_score, p.priority, p.priority_reason,
                       p.search_console_score, p.search_demand_score,
                       p.business_relevance_score, p.evidence_states,
                       p.trigger_type, p.calculated_at
                FROM article_priorities p
                JOIN article_topics t ON t.id = p.article_topic_id
                ORDER BY p.article_topic_id, p.calculated_at DESC, p.id DESC
                """, (result, rowNumber) -> new EditorialDtos.Priority(
                result.getLong("id"),
                result.getString("topic_key"),
                result.getInt("official_backlog_order"),
                result.getString("working_title"),
                result.getBigDecimal("final_score"),
                result.getString("priority"),
                result.getString("priority_reason"),
                result.getBigDecimal("search_console_score"),
                result.getBigDecimal("search_demand_score"),
                result.getBigDecimal("business_relevance_score"),
                result.getString("evidence_states"),
                result.getString("trigger_type"),
                result.getObject("calculated_at", java.time.OffsetDateTime.class).toInstant()))
                .stream()
                .sorted(EditorialDtos.Priority.RANKING)
                .toList();
    }

    record TopicEvidence(
            long id,
            String topicKey,
            int officialOrder,
            Long sourceOpportunityId,
            Long contentPillarId,
            Long funnelStageId,
            Long conversionGoalId,
            String supportingPagesJson,
            String internalLinkTargetsJson,
            boolean activeContentPillar,
            boolean activeFunnelStage,
            boolean activeConversionGoal,
            String opportunityState,
            BigDecimal impressions,
            Boolean relevance,
            Boolean cannibalization,
            Boolean longTail,
            String searchIntent,
            String language,
            boolean contentGap) {}
}
