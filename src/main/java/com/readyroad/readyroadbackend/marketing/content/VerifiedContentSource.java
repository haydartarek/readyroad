package com.readyroad.readyroadbackend.marketing.content;

import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextRequest;
import java.util.Map;

public record VerifiedContentSource(
        ContentSourceType type,
        String id,
        String sourceReference,
        String sourceHash,
        Map<ContentLocale, LocalizedFacts> facts,
        MarketingStrategyContextRequest embeddedStrategy) {

    public VerifiedContentSource {
        facts = Map.copyOf(facts);
    }

    public LocalizedFacts factsFor(ContentLocale locale) {
        LocalizedFacts localized = facts.get(locale);
        if (localized == null || localized.title() == null || localized.title().isBlank()
                || localized.facts() == null || localized.facts().isBlank()) {
            throw new BlockedContentSourceException("Source has no verified " + locale + " content");
        }
        return localized;
    }

    public record LocalizedFacts(String title, String facts) {}
}
