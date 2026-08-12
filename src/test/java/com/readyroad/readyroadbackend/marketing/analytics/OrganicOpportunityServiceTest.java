package com.readyroad.readyroadbackend.marketing.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrganicOpportunityServiceTest {

    private final AnalyticsStore store = mock(AnalyticsStore.class);
    private final AnalyticsSettingsService settingsService = mock(AnalyticsSettingsService.class);
    private final OrganicDiscoveryClassifier classifier = mock(OrganicDiscoveryClassifier.class);
    private final OrganicOpportunityService service =
            new OrganicOpportunityService(store, settingsService, classifier);

    @BeforeEach
    void setUp() {
        when(settingsService.current()).thenReturn(AnalyticsSettings.defaults());
    }

    @Test
    void oneImpressionAtPositionEightIsNotEstablished() {
        when(classifier.relevant("belgian theory exam")).thenReturn(true);

        var decision = service.decide(query(
                "belgian theory exam", 0, 1, 0, 8,
                null, null, null, null, 1), AnalyticsSettings.defaults());

        assertThat(decision.state()).isEqualTo(AnalyticsModels.OpportunityState.DISCOVERING);
        assertThat(decision.trend()).isEqualTo(AnalyticsModels.Trend.INSUFFICIENT_DATA);
    }

    @Test
    void highVolumePositionSixteenCanBecomeAnOpportunity() {
        when(classifier.relevant("belgian driving theory questions")).thenReturn(true);

        var decision = service.decide(query(
                "belgian driving theory questions", 25, 500, 0.05, 16,
                20.0, 0.04, 17.0, "EMERGING", 1), AnalyticsSettings.defaults());

        assertThat(decision.state()).isEqualTo(AnalyticsModels.OpportunityState.OPPORTUNITY);
        assertThat(decision.relevance()).isTrue();
        assertThat(decision.cannibalization()).isFalse();
    }

    @Test
    void cannibalizationPreventsAutomaticOpportunityState() {
        when(classifier.relevant("belgian driving theory questions")).thenReturn(true);

        var decision = service.decide(query(
                "belgian driving theory questions", 25, 500, 0.05, 16,
                20.0, 0.04, 17.0, "EMERGING", 2), AnalyticsSettings.defaults());

        assertThat(decision.state()).isNotEqualTo(AnalyticsModels.OpportunityState.OPPORTUNITY);
        assertThat(decision.cannibalization()).isTrue();
    }

    @Test
    void stableTopTenQueryNeedsClicksAndHistoricalComparisonToBecomeEstablished() {
        when(classifier.relevant("traffic signs belgium")).thenReturn(true);

        var decision = service.decide(query(
                "traffic signs belgium", 12, 100, 0.12, 8,
                11.0, 0.11, 8.5, "OPPORTUNITY", 1), AnalyticsSettings.defaults());

        assertThat(decision.state()).isEqualTo(AnalyticsModels.OpportunityState.ESTABLISHED);
        assertThat(decision.trend()).isEqualTo(AnalyticsModels.Trend.STABLE);
    }

    @Test
    void qualifiedQueryBecomesDecliningWhenPositionDropsThreePlaces() {
        when(classifier.relevant("traffic signs belgium")).thenReturn(true);

        var decision = service.decide(query(
                "traffic signs belgium", 12, 100, 0.12, 12,
                15.0, 0.15, 9.0, "ESTABLISHED", 1), AnalyticsSettings.defaults());

        assertThat(decision.state()).isEqualTo(AnalyticsModels.OpportunityState.DECLINING);
        assertThat(decision.trend()).isEqualTo(AnalyticsModels.Trend.DECLINING);
    }

    private static AnalyticsModels.QueryAggregate query(
            String query,
            double clicks,
            double impressions,
            double ctr,
            double position,
            Double previousClicks,
            Double previousCtr,
            Double previousPosition,
            String previousState,
            int distinctPages) {
        return new AnalyticsModels.QueryAggregate(
                query, "https://readyroad.be/lessons", "EN", "NON_BRAND", true,
                "INFORMATIONAL", clicks, impressions, ctr, position,
                previousClicks, previousCtr, previousPosition, previousState, distinctPages);
    }
}
