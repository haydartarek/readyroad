package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;

import com.readyroad.readyroadbackend.marketing.analytics.AnalyticsSettings;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class EditorialPerformancePolicyTest {

    private final EditorialPerformancePolicy policy = new EditorialPerformancePolicy();

    @Test
    void recommendsRefreshOnlyWhenApprovedDeclineThresholdsHaveEnoughEvidence() {
        var decision = policy.evaluate(List.of(snapshot(
                "EN",
                metrics(3, 100, 0.03, 15),
                metrics(10, 100, 0.10, 8))), AnalyticsSettings.defaults());

        assertThat(decision.recommended()).isTrue();
        assertThat(decision.reasonCodes()).containsExactly(
                "EN:POSITION_DECLINE", "EN:CLICKS_DECLINE", "EN:CTR_DECLINE");
    }

    @Test
    void ignoresLargePercentageChangesFromInsufficientPreviousImpressions() {
        var decision = policy.evaluate(List.of(snapshot(
                "AR",
                metrics(0, 0, 0, 0),
                metrics(1, 2, 0.5, 5))), AnalyticsSettings.defaults());

        assertThat(decision.recommended()).isFalse();
        assertThat(decision.reasonCodes()).isEmpty();
    }

    @Test
    void missingCurrentEvidenceIsPreservedAndCanExposeARealClickDrop() {
        var decision = policy.evaluate(List.of(snapshot(
                "NL",
                metrics(0, 0, 0, 0),
                metrics(12, 120, 0.10, 7))), AnalyticsSettings.defaults());

        assertThat(decision.recommended()).isTrue();
        assertThat(decision.reasonCodes()).containsExactly("NL:CLICKS_DECLINE", "NL:CTR_DECLINE");
        assertThat(decision.reasonCodes()).doesNotContain("NL:POSITION_DECLINE");
    }

    @Test
    void stablePerformanceDoesNotProduceARefreshRecommendation() {
        var decision = policy.evaluate(List.of(snapshot(
                "FR",
                metrics(11, 120, 0.0917, 7.5),
                metrics(10, 100, 0.10, 8))), AnalyticsSettings.defaults());

        assertThat(decision.recommended()).isFalse();
    }

    private static EditorialPerformanceStore.SnapshotRow snapshot(
            String language,
            EditorialPerformanceStore.Metrics current,
            EditorialPerformanceStore.Metrics previous) {
        return new EditorialPerformanceStore.SnapshotRow(
                1, 1, 1, language, "/blog/test",
                LocalDate.of(2026, 7, 29), LocalDate.of(2026, 8, 25),
                current, previous,
                current.impressions() > 0 ? "PRESENT" : "MISSING",
                current.impressions() > 0 ? "DISCOVERED" : "NO_DATA",
                Instant.parse("2026-08-25T00:00:00Z"));
    }

    private static EditorialPerformanceStore.Metrics metrics(
            double clicks, double impressions, double ctr, double position) {
        return new EditorialPerformanceStore.Metrics(clicks, impressions, ctr, position);
    }
}
