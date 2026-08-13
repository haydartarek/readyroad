package com.readyroad.readyroadbackend.marketing.content;

import com.readyroad.readyroadbackend.marketing.strategy.BlockedStrategyContextException;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContext;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextRequest;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentAgentService {

    private final ContentSourceService sourceService;
    private final MarketingStrategyContextService strategyService;
    private final ContentGenerationClient generationClient;
    private final ContentQualityValidator qualityValidator;
    private final ContentPersistenceService persistenceService;

    public ContentPackageResult generate(ContentTaskPayload payload, Long taskId) {
        if (payload == null || payload.sourceType() == null) {
            throw new BlockedContentSourceException("Content source type is required");
        }
        VerifiedContentSource source = sourceService.load(payload.sourceType(), payload.sourceId());
        MarketingStrategyContextRequest strategyRequest = payload.strategyContext() != null
                ? payload.strategyContext()
                : source.embeddedStrategy();
        if (strategyRequest == null) {
            throw new BlockedStrategyContextException("CONTENT_STRATEGY_CONTEXT");
        }
        MarketingStrategyContext strategy = strategyService.resolve(strategyRequest);
        var existing = persistenceService.existing(source, strategy);
        if (existing.isPresent()) {
            return existing.get();
        }
        List<ContentQualityValidator.ValidatedContent> variants = new ArrayList<>();
        for (ContentLocale locale : ContentLocale.SUPPORTED) {
            var generated = generationClient.generate(new ContentGenerationClient.GenerationRequest(
                    locale, source, source.factsFor(locale), strategy));
            variants.add(qualityValidator.validate(locale, source, generated));
        }
        return persistenceService.persist(source, strategy, List.copyOf(variants), taskId);
    }
}
