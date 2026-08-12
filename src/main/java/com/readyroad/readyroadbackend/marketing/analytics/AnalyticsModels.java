package com.readyroad.readyroadbackend.marketing.analytics;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class AnalyticsModels {

    private AnalyticsModels() {}

    public enum OpportunityState {
        DISCOVERING, EMERGING, OPPORTUNITY, ESTABLISHED, DECLINING
    }

    public enum Trend {
        IMPROVING, STABLE, DECLINING, INSUFFICIENT_DATA
    }

    public enum BrandClassification {
        OWN_BRAND, NON_BRAND, COMPETITOR_OR_AMBIGUOUS_BRAND
    }

    public enum SearchIntent {
        INFORMATIONAL, NAVIGATIONAL, TRANSACTIONAL, UNKNOWN
    }

    public record MetricRow(LocalDate date, Map<String, String> dimensions, Map<String, Double> metrics) {}

    public record SearchRow(
            LocalDate date,
            String query,
            String page,
            String device,
            double clicks,
            double impressions,
            double ctr,
            double position) {}

    public record SearchConsoleData(
            List<SearchRow> propertyTotals,
            List<SearchRow> queries,
            List<SearchRow> pages,
            Map<String, Object> quotaState) {}

    public record Ga4Data(
            List<MetricRow> totals,
            List<MetricRow> languages,
            List<MetricRow> devices,
            Map<String, Object> quotaState) {}

    public record QueryAggregate(
            String query,
            String page,
            String language,
            String brandClassification,
            boolean longTail,
            String searchIntent,
            double clicks,
            double impressions,
            double ctr,
            double averagePosition,
            Double previousClicks,
            Double previousCtr,
            Double previousPosition,
            String previousState,
            int distinctPages) {}

    public record OpportunityDecision(
            OpportunityState state,
            Trend trend,
            boolean relevance,
            boolean cannibalization,
            Map<String, Object> evidence) {}
}
