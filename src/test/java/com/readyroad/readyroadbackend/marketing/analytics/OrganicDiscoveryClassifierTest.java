package com.readyroad.readyroadbackend.marketing.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyReadService;
import org.junit.jupiter.api.Test;

class OrganicDiscoveryClassifierTest {

    private final OrganicDiscoveryClassifier classifier =
            new OrganicDiscoveryClassifier(mock(MarketingStrategyReadService.class));

    @Test
    void followsTheApprovedBrandClassificationExamples() {
        assertThat(classifier.brand("rijvia"))
                .isEqualTo(AnalyticsModels.BrandClassification.OWN_BRAND_RIJVIA);
        assertThat(classifier.brand("Rij Via"))
                .isEqualTo(AnalyticsModels.BrandClassification.OWN_BRAND_RIJVIA);
        assertThat(classifier.brand("readyroad"))
                .isEqualTo(AnalyticsModels.BrandClassification.LEGACY_BRAND_QUERY);
        assertThat(classifier.brand("Ready Road"))
                .isEqualTo(AnalyticsModels.BrandClassification.LEGACY_BRAND_QUERY);
        assertThat(classifier.brand("ready to road"))
                .isEqualTo(AnalyticsModels.BrandClassification.COMPETITOR_OR_AMBIGUOUS_BRAND);
        assertThat(classifier.brand("readytoroad"))
                .isEqualTo(AnalyticsModels.BrandClassification.COMPETITOR_OR_AMBIGUOUS_BRAND);
        assertThat(classifier.brand("autoweg"))
                .isEqualTo(AnalyticsModels.BrandClassification.NON_BRAND);
    }

    @Test
    void classifiesLongTailIntentAndLocalizedRoutesWithoutGuessingFromEnglishPaths() {
        assertThat(classifier.longTail("verschil autoweg en autosnelweg belgië")).isTrue();
        assertThat(classifier.intent("verschil autoweg en autosnelweg belgië"))
                .isEqualTo(AnalyticsModels.SearchIntent.INFORMATIONAL);
        assertThat(classifier.language("https://rijvia.be/ar/lessons/les-1", "علامات المرور"))
                .isEqualTo("AR");
        assertThat(classifier.language("https://rijvia.be/nl/practice", "oefenen"))
                .isEqualTo("NL");
        assertThat(classifier.language("https://rijvia.be/fr/exam", "examen"))
                .isEqualTo("FR");
        assertThat(classifier.language("https://rijvia.be/lessons", "traffic signs"))
                .isEqualTo("EN");
    }
}
