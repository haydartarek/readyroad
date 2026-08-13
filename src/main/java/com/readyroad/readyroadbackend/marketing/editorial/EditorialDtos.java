package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
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

    public record Priority(
            long topicId,
            String topicKey,
            int officialOrder,
            String title,
            BigDecimal finalScore,
            String priority,
            String priorityReason,
            BigDecimal searchConsoleScore,
            BigDecimal searchDemandScore,
            BigDecimal businessRelevanceScore,
            String evidenceStates,
            String triggerType,
            Instant calculatedAt) {

        static final Comparator<Priority> RANKING = Comparator
                .comparing(Priority::finalScore, Comparator.reverseOrder())
                .thenComparing(Priority::searchConsoleScore, Comparator.reverseOrder())
                .thenComparing(Priority::searchDemandScore, Comparator.reverseOrder())
                .thenComparing(Priority::businessRelevanceScore, Comparator.reverseOrder())
                .thenComparingInt(Priority::officialOrder);
    }

    public record RecalculateRequest(@NotBlank @Size(max = 255) String idempotencyKey) {}

    public record SettingsUpdateRequest(
            @NotNull JsonNode settings,
            @NotBlank @Size(max = 255) String idempotencyKey) {}
}
