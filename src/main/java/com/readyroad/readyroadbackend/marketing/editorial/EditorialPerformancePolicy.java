package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.analytics.AnalyticsSettings;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
class EditorialPerformancePolicy {

    Decision evaluate(
            List<EditorialPerformanceStore.SnapshotRow> snapshots,
            AnalyticsSettings settings) {
        List<String> reasons = new ArrayList<>();
        Map<String, Object> evidence = new LinkedHashMap<>();
        int sufficientlyObservedLocales = 0;

        for (EditorialPerformanceStore.SnapshotRow snapshot : snapshots) {
            EditorialPerformanceStore.Metrics current = snapshot.current();
            EditorialPerformanceStore.Metrics previous = snapshot.previous();
            if (previous.impressions() < settings.emergingImpressions()) {
                continue;
            }
            sufficientlyObservedLocales++;
            String language = snapshot.language();
            if (current.impressions() >= settings.emergingImpressions()
                    && current.averagePosition() - previous.averagePosition() >= settings.positionDecline()) {
                reasons.add(language + ":POSITION_DECLINE");
            }
            if (declinePercent(previous.clicks(), current.clicks()) >= settings.clicksDeclinePercent()) {
                reasons.add(language + ":CLICKS_DECLINE");
            }
            if (previous.ctr() > 0
                    && declinePercent(previous.ctr(), current.ctr()) >= settings.ctrDeclinePercent()) {
                reasons.add(language + ":CTR_DECLINE");
            }
        }

        evidence.put("observedLocales", snapshots.stream()
                .filter(snapshot -> snapshot.current().impressions() > 0)
                .map(EditorialPerformanceStore.SnapshotRow::language)
                .toList());
        evidence.put("sufficientlyObservedLocales", sufficientlyObservedLocales);
        evidence.put("minimumPreviousImpressions", settings.emergingImpressions());
        evidence.put("positionDeclineThreshold", settings.positionDecline());
        evidence.put("clicksDeclinePercentThreshold", settings.clicksDeclinePercent());
        evidence.put("ctrDeclinePercentThreshold", settings.ctrDeclinePercent());
        return new Decision(!reasons.isEmpty(), List.copyOf(reasons), Map.copyOf(evidence));
    }

    private static double declinePercent(double previous, double current) {
        if (previous <= 0 || current >= previous) {
            return 0;
        }
        return BigDecimal.valueOf((previous - current) * 100 / previous)
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
    }

    record Decision(boolean recommended, List<String> reasonCodes, Map<String, Object> evidence) {}
}
