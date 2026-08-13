package com.readyroad.readyroadbackend.marketing.editorial;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialOpportunityStore {

    private final JdbcTemplate jdbc;

    List<OpportunityEvidence> eligibleOpportunities() {
        return jdbc.query("""
                SELECT o.id, o.opportunity_key, o.query, o.page, o.language,
                       o.search_intent, o.impressions, o.clicks, o.ctr,
                       o.average_position, o.long_tail, o.evidence, o.updated_at
                FROM seo_opportunities o
                WHERE o.state = 'OPPORTUNITY'
                  AND o.relevance
                  AND NOT o.cannibalization
                  AND o.search_intent IN ('INFORMATIONAL', 'TRANSACTIONAL')
                  AND o.language IN ('AR', 'NL', 'FR', 'EN')
                  AND o.page <> ''
                  AND EXISTS (
                      SELECT 1 FROM seo_content_gaps g
                      WHERE g.query = o.query
                        AND g.language = o.language
                        AND g.status IN ('DISCOVERED', 'REVIEWED')
                  )
                  AND NOT EXISTS (
                      SELECT 1 FROM article_topics topic
                      WHERE topic.source_opportunity_id = o.id
                  )
                  AND NOT EXISTS (
                      SELECT 1 FROM agent_tasks task
                      WHERE task.agent_type = 'EDITORIAL'
                        AND task.task_type = 'ARTICLE_OPPORTUNITY_DISCOVERY'
                        AND task.source_type = 'SEARCH_CONSOLE_OPPORTUNITY'
                        AND task.source_id = o.id::text
                  )
                ORDER BY o.impressions DESC, o.updated_at ASC, o.id ASC
                """, (result, rowNumber) -> new OpportunityEvidence(
                result.getLong("id"),
                result.getString("opportunity_key"),
                result.getString("query"),
                result.getString("page"),
                result.getString("language"),
                result.getString("search_intent"),
                result.getBigDecimal("impressions"),
                result.getBigDecimal("clicks"),
                result.getBigDecimal("ctr"),
                result.getBigDecimal("average_position"),
                result.getBoolean("long_tail"),
                result.getString("evidence"),
                result.getObject("updated_at", OffsetDateTime.class)));
    }

    List<String> existingTitlesAndQueries(String language) {
        return jdbc.queryForList("""
                SELECT value
                FROM (
                    SELECT working_title AS value
                    FROM article_topics
                    WHERE title_language = ?
                    UNION ALL
                    SELECT query.value
                    FROM article_topics topic
                    CROSS JOIN LATERAL jsonb_array_elements_text(topic.target_queries) query(value)
                    WHERE topic.title_language = ?
                ) existing
                """, String.class, language, language);
    }

    OpportunityEvidence requireEligible(long opportunityId) {
        return jdbc.query("""
                SELECT o.id, o.opportunity_key, o.query, o.page, o.language,
                       o.search_intent, o.impressions, o.clicks, o.ctr,
                       o.average_position, o.long_tail, o.evidence, o.updated_at
                FROM seo_opportunities o
                WHERE o.id = ?
                  AND o.state = 'OPPORTUNITY'
                  AND o.relevance
                  AND NOT o.cannibalization
                  AND o.search_intent IN ('INFORMATIONAL', 'TRANSACTIONAL')
                  AND o.language IN ('AR', 'NL', 'FR', 'EN')
                  AND o.page <> ''
                  AND EXISTS (
                      SELECT 1 FROM seo_content_gaps g
                      WHERE g.query = o.query
                        AND g.language = o.language
                        AND g.status IN ('DISCOVERED', 'REVIEWED')
                  )
                """, (result, rowNumber) -> new OpportunityEvidence(
                result.getLong("id"), result.getString("opportunity_key"),
                result.getString("query"), result.getString("page"),
                result.getString("language"), result.getString("search_intent"),
                result.getBigDecimal("impressions"), result.getBigDecimal("clicks"),
                result.getBigDecimal("ctr"), result.getBigDecimal("average_position"),
                result.getBoolean("long_tail"), result.getString("evidence"),
                result.getObject("updated_at", OffsetDateTime.class)), opportunityId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Search Console opportunity is no longer eligible: " + opportunityId));
    }

    long createTopic(OpportunityEvidence opportunity) {
        jdbc.execute("LOCK TABLE article_topics IN SHARE ROW EXCLUSIVE MODE");
        List<Long> existing = jdbc.queryForList(
                "SELECT id FROM article_topics WHERE source_opportunity_id = ?",
                Long.class,
                opportunity.id());
        if (!existing.isEmpty()) {
            return existing.getFirst();
        }
        Integer order = jdbc.queryForObject(
                "SELECT GREATEST(COALESCE(MAX(official_backlog_order), 40), 40) + 1 FROM article_topics",
                Integer.class);
        return jdbc.queryForObject("""
                INSERT INTO article_topics (
                    topic_key, official_backlog_order, cluster_order, cluster_key, cluster_name,
                    working_title, title_language, primary_language, pillar, status,
                    source_type, source_opportunity_id, target_queries
                ) VALUES (?, ?, NULL, NULL, NULL, ?, ?, ?, FALSE, 'PLANNED',
                          'SEARCH_CONSOLE_OPPORTUNITY', ?, jsonb_build_array(?::text))
                RETURNING id
                """, Long.class,
                "DISCOVERED-" + opportunity.id(), order, opportunity.query(),
                opportunity.language(), opportunity.language(), opportunity.id(), opportunity.query());
    }

    record OpportunityEvidence(
            long id,
            String opportunityKey,
            String query,
            String page,
            String language,
            String searchIntent,
            BigDecimal impressions,
            BigDecimal clicks,
            BigDecimal ctr,
            BigDecimal averagePosition,
            boolean longTail,
            String evidenceJson,
            OffsetDateTime updatedAt) {}
}
