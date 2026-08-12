package com.readyroad.readyroadbackend.marketing.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingContentPillar;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingConversionGoal;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingFunnelStage;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingIcp;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingPositioning;
import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingUsp;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingContentPillarRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingConversionGoalRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingFunnelStageRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingIcpRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingPositioningRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingUspRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarketingStrategyContextServiceTest {

    @Mock MarketingUspRepository uspRepository;
    @Mock MarketingIcpRepository icpRepository;
    @Mock MarketingPositioningRepository positioningRepository;
    @Mock MarketingContentPillarRepository pillarRepository;
    @Mock MarketingFunnelStageRepository funnelRepository;
    @Mock MarketingConversionGoalRepository conversionGoalRepository;

    private MarketingStrategyContextService service;

    @BeforeEach
    void setUp() {
        service = new MarketingStrategyContextService(
                uspRepository,
                icpRepository,
                positioningRepository,
                pillarRepository,
                funnelRepository,
                conversionGoalRepository);
    }

    @Test
    void resolvesOnlyACompleteActiveStrategyContext() {
        MarketingUsp usp = new MarketingUsp();
        usp.setId(1L);
        usp.setTitle("Four-language learning");
        usp.setActive(true);

        MarketingIcp icp = new MarketingIcp();
        icp.setId("ICP-AR-BEGINNER");
        icp.setActive(true);

        MarketingPositioning positioning = new MarketingPositioning();
        positioning.setId(1L);
        positioning.setStatement("ReadyRoad positioning");
        positioning.setBrandVoice(new ObjectMapper().createArrayNode().add("human"));
        positioning.setActive(true);

        MarketingContentPillar pillar = new MarketingContentPillar();
        pillar.setId(1L);
        pillar.setName("Theory exam");
        pillar.setActive(true);

        MarketingFunnelStage funnel = new MarketingFunnelStage();
        funnel.setId(2L);
        funnel.setStageKey("EDUCATION");
        funnel.setActive(true);

        MarketingConversionGoal goal = new MarketingConversionGoal();
        goal.setId(3L);
        goal.setFunnelStageId(2L);
        goal.setActive(true);

        when(uspRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(usp));
        when(icpRepository.findByIdAndActiveTrue("ICP-AR-BEGINNER")).thenReturn(Optional.of(icp));
        when(positioningRepository.findFirstByActiveTrueOrderByIdAsc()).thenReturn(Optional.of(positioning));
        when(pillarRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(pillar));
        when(funnelRepository.findByIdAndActiveTrue(2L)).thenReturn(Optional.of(funnel));
        when(conversionGoalRepository.findByIdAndActiveTrue(3L)).thenReturn(Optional.of(goal));

        MarketingStrategyContext result = service.resolve(
                new MarketingStrategyContextRequest(1L, "ICP-AR-BEGINNER", 1L, 2L, 3L));

        assertThat(result.usp().title()).isEqualTo("Four-language learning");
        assertThat(result.icp().id()).isEqualTo("ICP-AR-BEGINNER");
        assertThat(result.positioning().brandVoice()).isNotNull();
        assertThat(result.contentPillar().id()).isEqualTo(1L);
        assertThat(result.funnelStage().id()).isEqualTo(2L);
        assertThat(result.conversionGoal().id()).isEqualTo(3L);
    }

    @Test
    void blocksInsteadOfInventingAMissingStrategyContext() {
        when(uspRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.resolve(
                        new MarketingStrategyContextRequest(99L, "ICP-AR-BEGINNER", 1L, 2L, 3L)))
                .isInstanceOf(BlockedStrategyContextException.class)
                .hasMessageContaining("USP")
                .extracting("errorCode")
                .isEqualTo("BLOCKED_STRATEGY_CONTEXT");
    }
}
