package com.readyroad.readyroadbackend.marketing.editorial;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialBacklogStore {

    private final JdbcTemplate jdbc;

    List<EditorialDtos.Topic> topics() {
        return jdbc.query("""
                SELECT id, topic_key, official_backlog_order, cluster_order, cluster_key,
                       cluster_name, working_title, title_language, primary_language,
                       pillar, status, article_priority, priority_reason,
                       source_opportunity_id, usp_id, icp_id, content_pillar_id, funnel_stage_id,
                       conversion_goal_id
                FROM article_topics
                WHERE source_type = 'OFFICIAL_STRATEGIC_BACKLOG'
                ORDER BY official_backlog_order
                """, (result, rowNumber) -> {
            Long opportunityId = result.getObject("source_opportunity_id", Long.class);
            Long uspId = result.getObject("usp_id", Long.class);
            String icpId = result.getString("icp_id");
            Long pillarId = result.getObject("content_pillar_id", Long.class);
            Long funnelId = result.getObject("funnel_stage_id", Long.class);
            Long goalId = result.getObject("conversion_goal_id", Long.class);
            String primaryLanguage = result.getString("primary_language");
            boolean strategyResolved = uspId != null
                    && icpId != null
                    && pillarId != null
                    && funnelId != null
                    && goalId != null
                    && primaryLanguage != null;
            return new EditorialDtos.Topic(
                    result.getLong("id"),
                    result.getString("topic_key"),
                    result.getInt("official_backlog_order"),
                    result.getInt("cluster_order"),
                    result.getString("cluster_key"),
                    result.getString("cluster_name"),
                    result.getString("working_title"),
                    result.getString("title_language"),
                    primaryLanguage,
                    result.getBoolean("pillar"),
                    result.getString("status"),
                    result.getString("article_priority"),
                    result.getString("priority_reason"),
                    opportunityId,
                    uspId,
                    icpId,
                    pillarId,
                    funnelId,
                    goalId,
                    strategyResolved);
        });
    }
}
