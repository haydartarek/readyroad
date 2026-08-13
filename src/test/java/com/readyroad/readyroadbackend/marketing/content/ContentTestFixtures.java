package com.readyroad.readyroadbackend.marketing.content;

import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContext;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextRequest;
import com.readyroad.readyroadbackend.marketing.strategy.StrategyDtos;
import java.util.EnumMap;
import java.util.Map;

final class ContentTestFixtures {
    private ContentTestFixtures() {}

    static VerifiedContentSource source() {
        Map<ContentLocale, VerifiedContentSource.LocalizedFacts> facts = new EnumMap<>(ContentLocale.class);
        for (ContentLocale locale : ContentLocale.SUPPORTED) {
            facts.put(locale, new VerifiedContentSource.LocalizedFacts(
                    "Verified " + locale + " title", "Verified " + locale + " facts"));
        }
        return new VerifiedContentSource(
                ContentSourceType.ROAD_SIGN, "A1", "ROAD_SIGN:A1", "source-hash", facts, null);
    }

    static MarketingStrategyContextRequest request() {
        return new MarketingStrategyContextRequest(1L, "ICP-SIGN-SEARCH", 2L, 3L, 4L);
    }

    static MarketingStrategyContext strategy() {
        return new MarketingStrategyContext(
                new StrategyDtos.Usp(1L, "Verified USP", "Evidence-backed", "FEATURE", "READYROAD", true,
                        (short) 1, "OWNER"),
                new StrategyDtos.Icp("ICP-SIGN-SEARCH", "Sign search learner", "en", "Belgium", null,
                        "Understand a sign", "Needs verified guidance", "informational", "educational", "web",
                        null, "EDUCATION", "CONTINUE", true, "OWNER"),
                new StrategyDtos.Positioning(1L, "Independent Belgian theory learning platform", null, null,
                        true, "OWNER"),
                new StrategyDtos.ContentPillar(2L, "TRAFFIC_SIGNS", "Traffic signs", true, (short) 1, "OWNER"),
                new StrategyDtos.FunnelStage(3L, "EDUCATION", (short) 3, true, "OWNER"),
                new StrategyDtos.ConversionGoal(4L, "CONTINUE", "Continue learning", null,
                        "Study this topic on ReadyRoad", 3L, true, "OWNER"));
    }

    static ContentGenerationClient.GeneratedContent generated(ContentLocale locale) {
        return new ContentGenerationClient.GeneratedContent(
                locale.name(), "ROAD_SIGN:A1", "Title " + locale, "Summary " + locale,
                "Educational body " + locale, "Continue " + locale,
                "gpt-5.6-terra", 100, 50, "SUCCEEDED");
    }

    static ContentGenerationClient.GenerationRequest generationRequest(ContentLocale locale) {
        VerifiedContentSource source = source();
        return new ContentGenerationClient.GenerationRequest(locale, source, source.factsFor(locale), strategy());
    }
}
