package com.readyroad.readyroadbackend.marketing.analytics;

public record AnalyticsSettings(
        int initialBackfillDays,
        int intervalDays,
        int noDataDays,
        int sourceFailureHours,
        int windowDays,
        double emergingImpressions,
        double emergingPositionMin,
        double emergingPositionMax,
        double opportunityImpressions,
        double opportunityPositionMin,
        double opportunityPositionMax,
        double establishedPositionMax,
        double establishedClicks,
        double positionDecline,
        double clicksDeclinePercent,
        double ctrDeclinePercent,
        int stableWindows) {

    public static AnalyticsSettings defaults() {
        return new AnalyticsSettings(
                90, 3, 6, 3, 28,
                20, 11, 30,
                50, 4, 20,
                10, 10,
                3, 30, 30, 2);
    }
}
