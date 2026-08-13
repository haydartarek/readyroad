package com.readyroad.readyroadbackend.marketing.editorial;

import java.util.List;

public final class EditorialDtos {

    private EditorialDtos() {
    }

    public record Backlog(
            int total,
            int pillars,
            int unresolvedStrategyContext,
            List<Topic> topics) {
    }

    public record Topic(
            long id,
            String topicKey,
            int officialOrder,
            int clusterOrder,
            String clusterKey,
            String clusterName,
            String title,
            String titleLanguage,
            String primaryLanguage,
            boolean pillar,
            String status,
            String articlePriority,
            String priorityReason,
            Long sourceOpportunityId,
            Long contentPillarId,
            Long funnelStageId,
            Long conversionGoalId,
            boolean strategyContextResolved) {
    }
}
