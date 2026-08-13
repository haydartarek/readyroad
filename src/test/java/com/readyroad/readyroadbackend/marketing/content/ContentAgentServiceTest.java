package com.readyroad.readyroadbackend.marketing.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentAgentServiceTest {

    @Mock ContentSourceService sourceService;
    @Mock MarketingStrategyContextService strategyService;
    @Mock ContentGenerationClient generationClient;
    @Mock ContentPersistenceService persistenceService;

    private ContentQualityValidator validator;
    private ContentAgentService service;

    @BeforeEach
    void setUp() {
        validator = new ContentQualityValidator(new com.readyroad.readyroadbackend.marketing.config.MarketingProperties());
        service = new ContentAgentService(
                sourceService, strategyService, generationClient, validator, persistenceService);
    }

    @Test
    void generatesEachLanguageIndependentlyAndPersistsOneAtomicPackage() {
        var source = ContentTestFixtures.source();
        var request = ContentTestFixtures.request();
        var strategy = ContentTestFixtures.strategy();
        when(sourceService.load(ContentSourceType.ROAD_SIGN, "A1")).thenReturn(source);
        when(strategyService.resolve(request)).thenReturn(strategy);
        when(persistenceService.existing(source, strategy)).thenReturn(Optional.empty());
        when(generationClient.generate(any(ContentGenerationClient.GenerationRequest.class)))
                .thenAnswer(invocation -> ContentTestFixtures.generated(
                        invocation.getArgument(0, ContentGenerationClient.GenerationRequest.class).locale()));
        when(persistenceService.persist(eq(source), eq(strategy), any(), eq(99L)))
                .thenReturn(new ContentPackageResult(7L, 4, false));

        var result = service.generate(new ContentTaskPayload(ContentSourceType.ROAD_SIGN, "A1", request), 99L);

        assertThat(result.variants()).isEqualTo(4);
        for (ContentLocale locale : ContentLocale.SUPPORTED) {
            verify(generationClient).generate(org.mockito.ArgumentMatchers.argThat(generation ->
                    generation.locale() == locale && generation.facts().facts().contains(locale.name())));
        }
        verify(persistenceService).persist(eq(source), eq(strategy),
                org.mockito.ArgumentMatchers.argThat(items -> items.size() == 4), eq(99L));
    }

    @Test
    void returnsExistingPackageWithoutCallingOpenAI() {
        var source = ContentTestFixtures.source();
        var request = ContentTestFixtures.request();
        var strategy = ContentTestFixtures.strategy();
        when(sourceService.load(ContentSourceType.ROAD_SIGN, "A1")).thenReturn(source);
        when(strategyService.resolve(request)).thenReturn(strategy);
        when(persistenceService.existing(source, strategy))
                .thenReturn(Optional.of(new ContentPackageResult(8L, 4, true)));

        var result = service.generate(new ContentTaskPayload(ContentSourceType.ROAD_SIGN, "A1", request), 100L);

        assertThat(result.existing()).isTrue();
        verify(generationClient, never()).generate(any());
        verify(persistenceService, never()).persist(any(), any(), any(), any());
    }
}
