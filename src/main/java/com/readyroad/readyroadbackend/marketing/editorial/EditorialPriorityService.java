package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.analytics.AnalyticsSettingsService;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditorialPriorityService {

    private final EditorialPriorityStore store;
    private final EditorialPrioritySettingsService settingsService;
    private final AnalyticsSettingsService analyticsSettingsService;
    private final EditorialPriorityScorer scorer;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;

    @Transactional
    public int recalculate(Long taskId, String triggerType, String actor) {
        EditorialPriorityConfig config = settingsService.current();
        double demandTarget = analyticsSettingsService.current().opportunityImpressions();
        int calculated = 0;
        for (EditorialPriorityStore.TopicEvidence topic : store.loadEvidence()) {
            Map<String, EditorialPriorityScorer.EditorialFactorEvidence> evidence = evidence(topic, demandTarget);
            var score = scorer.score(config, evidence);
            String calculationKey = "task:" + taskId + ":topic:" + topic.id();
            String reason = reason(score);
            store.save(
                    topic,
                    score,
                    objectMapper.valueToTree(score.normalizedScores()),
                    objectMapper.valueToTree(score.evidenceStates()),
                    evidenceDetails(topic),
                    settingsService.raw(),
                    reason,
                    calculationKey,
                    taskId,
                    triggerType);
            calculated++;
        }
        auditService.recordEntityEvent(
                "EDITORIAL_PRIORITIES_RECALCULATED",
                actor,
                "EDITORIAL_BACKLOG",
                "OFFICIAL_STRATEGIC_BACKLOG",
                taskId,
                "editorial-priority:" + taskId,
                objectMapper.createObjectNode()
                        .put("topicsCalculated", calculated)
                        .put("triggerType", triggerType));
        return calculated;
    }

    @Transactional(readOnly = true)
    public List<EditorialDtos.Priority> priorities() {
        return store.currentPriorities();
    }

    private Map<String, EditorialPriorityScorer.EditorialFactorEvidence> evidence(
            EditorialPriorityStore.TopicEvidence topic,
            double demandTarget) {
        Map<String, EditorialPriorityScorer.EditorialFactorEvidence> evidence = new LinkedHashMap<>();
        if (topic.sourceOpportunityId() != null) {
            double impressions = topic.impressions() == null ? 0 : topic.impressions().doubleValue();
            double demand = demandTarget <= 0 ? 0 : Math.min(100, impressions * 100 / demandTarget);
            evidence.put(EditorialPriorityConfig.SEARCH_DEMAND, present(demand));
            boolean usableOpportunity = "OPPORTUNITY".equals(topic.opportunityState())
                    && Boolean.TRUE.equals(topic.relevance())
                    && !Boolean.TRUE.equals(topic.cannibalization())
                    && ("INFORMATIONAL".equals(topic.searchIntent())
                            || "TRANSACTIONAL".equals(topic.searchIntent()))
                    && topic.language() != null
                    && !"UNKNOWN".equals(topic.language());
            evidence.put(EditorialPriorityConfig.SEARCH_CONSOLE, present(usableOpportunity ? 100 : 0));
            evidence.put(EditorialPriorityConfig.LONG_TAIL, present(Boolean.TRUE.equals(topic.longTail()) ? 100 : 0));
            evidence.put(EditorialPriorityConfig.CONTENT_GAP, present(topic.contentGap() ? 100 : 0));
        }
        if (topic.activeConversionGoal()) {
            evidence.put(EditorialPriorityConfig.BUSINESS_RELEVANCE, present(100));
        }
        if (topic.activeContentPillar() && topic.activeFunnelStage()) {
            evidence.put(EditorialPriorityConfig.STRATEGIC_RELEVANCE, present(100));
        }
        if (nonEmptyArray(topic.supportingPagesJson())) {
            evidence.put(EditorialPriorityConfig.AUTHORITY, present(100));
        }
        if (nonEmptyArray(topic.internalLinkTargetsJson())) {
            evidence.put(EditorialPriorityConfig.INTERNAL_LINKING, present(100));
        }
        return Map.copyOf(evidence);
    }

    private boolean nonEmptyArray(String json) {
        try {
            if (json == null) return false;
            var value = objectMapper.readTree(json);
            return value.isArray() && !value.isEmpty();
        } catch (Exception ignored) {
            return false;
        }
    }

    private com.fasterxml.jackson.databind.JsonNode evidenceDetails(EditorialPriorityStore.TopicEvidence topic) {
        var details = objectMapper.createObjectNode();
        if (topic.sourceOpportunityId() != null) {
            var search = objectMapper.createObjectNode()
                    .put("sourceOpportunityId", topic.sourceOpportunityId())
                    .put("state", topic.opportunityState())
                    .put("impressions", topic.impressions())
                    .put("relevance", Boolean.TRUE.equals(topic.relevance()))
                    .put("cannibalization", Boolean.TRUE.equals(topic.cannibalization()))
                    .put("longTail", Boolean.TRUE.equals(topic.longTail()))
                    .put("searchIntent", topic.searchIntent())
                    .put("language", topic.language())
                    .put("contentGap", topic.contentGap());
            details.set("searchConsole", search);
        }
        var strategy = objectMapper.createObjectNode();
        if (topic.contentPillarId() != null) strategy.put("contentPillarId", topic.contentPillarId());
        if (topic.funnelStageId() != null) strategy.put("funnelStageId", topic.funnelStageId());
        if (topic.conversionGoalId() != null) strategy.put("conversionGoalId", topic.conversionGoalId());
        if (!strategy.isEmpty()) details.set("strategy", strategy);
        details.put("supportingPagesPresent", nonEmptyArray(topic.supportingPagesJson()));
        details.put("internalLinkTargetsPresent", nonEmptyArray(topic.internalLinkTargetsJson()));
        return details;
    }

    private static EditorialPriorityScorer.EditorialFactorEvidence present(double value) {
        return EditorialPriorityScorer.EditorialFactorEvidence.present(value);
    }

    private static String reason(EditorialPriorityScorer.EditorialPriorityScore score) {
        long missing = score.evidenceStates().values().stream().filter("MISSING"::equals).count();
        String reason = "Evidence-backed score " + score.finalScore().setScale(3, RoundingMode.HALF_UP)
                + "/100; missing factors=" + missing;
        return "MISSING".equals(score.evidenceStates().get(EditorialPriorityConfig.SEARCH_CONSOLE))
                ? reason + "; Search Console missing uses the approved neutral 50% component score."
                : reason + "; Search Console evidence is present.";
    }
}
