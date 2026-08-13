package com.readyroad.readyroadbackend.marketing.content;

import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContext;

public interface ContentGenerationClient {

    GeneratedContent generate(GenerationRequest request);

    record GenerationRequest(
            ContentLocale locale,
            VerifiedContentSource source,
            VerifiedContentSource.LocalizedFacts facts,
            MarketingStrategyContext strategy) {}

    record GeneratedContent(
            String language,
            String sourceReference,
            String title,
            String summary,
            String body,
            String cta,
            String model,
            long inputTokens,
            long outputTokens,
            String requestOutcome) {}
}
