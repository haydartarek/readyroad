package com.readyroad.readyroadbackend.marketing.strategy;

import com.readyroad.readyroadbackend.marketing.strategy.domain.MarketingConversionGoal;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingContentPillarRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingConversionGoalRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingFunnelStageRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingIcpRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingPositioningRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingUspRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketingStrategyContextService {

    private final MarketingUspRepository uspRepository;
    private final MarketingIcpRepository icpRepository;
    private final MarketingPositioningRepository positioningRepository;
    private final MarketingContentPillarRepository pillarRepository;
    private final MarketingFunnelStageRepository funnelRepository;
    private final MarketingConversionGoalRepository conversionGoalRepository;

    @Transactional(readOnly = true)
    public MarketingStrategyContext resolve(MarketingStrategyContextRequest request) {
        var usp = uspRepository.findByIdAndActiveTrue(request.uspId())
                .orElseThrow(() -> missing("USP"));
        var icp = icpRepository.findByIdAndActiveTrue(request.icpId())
                .orElseThrow(() -> missing("ICP"));
        var positioning = positioningRepository.findFirstByActiveTrueOrderByIdAsc()
                .orElseThrow(() -> missing("POSITIONING_AND_BRAND_VOICE"));
        var pillar = pillarRepository.findByIdAndActiveTrue(request.contentPillarId())
                .orElseThrow(() -> missing("CONTENT_PILLAR"));
        var funnel = funnelRepository.findByIdAndActiveTrue(request.funnelStageId())
                .orElseThrow(() -> missing("FUNNEL_STAGE"));
        MarketingConversionGoal conversionGoal = conversionGoalRepository
                .findByIdAndActiveTrue(request.conversionGoalId())
                .orElseThrow(() -> missing("CONVERSION_GOAL"));
        if (!request.funnelStageId().equals(conversionGoal.getFunnelStageId())) {
            throw missing("CONVERSION_GOAL_FOR_FUNNEL_STAGE");
        }

        return new MarketingStrategyContext(
                StrategyDtos.Usp.from(usp),
                StrategyDtos.Icp.from(icp),
                StrategyDtos.Positioning.from(positioning),
                StrategyDtos.ContentPillar.from(pillar),
                StrategyDtos.FunnelStage.from(funnel),
                StrategyDtos.ConversionGoal.from(conversionGoal));
    }

    private static BlockedStrategyContextException missing(String context) {
        return new BlockedStrategyContextException(context);
    }
}
