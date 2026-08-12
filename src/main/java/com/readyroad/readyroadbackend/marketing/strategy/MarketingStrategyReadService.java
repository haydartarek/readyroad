package com.readyroad.readyroadbackend.marketing.strategy;

import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingContentPillarRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingConversionGoalRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingFunnelStageRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingIcpRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingPositioningRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.MarketingUspRepository;
import com.readyroad.readyroadbackend.marketing.strategy.repository.SocialProofItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketingStrategyReadService {

    private final MarketingUspRepository uspRepository;
    private final MarketingIcpRepository icpRepository;
    private final MarketingPositioningRepository positioningRepository;
    private final MarketingContentPillarRepository pillarRepository;
    private final MarketingFunnelStageRepository funnelRepository;
    private final MarketingConversionGoalRepository conversionGoalRepository;
    private final SocialProofItemRepository socialProofRepository;

    @Transactional(readOnly = true)
    public MarketingStrategySnapshot snapshot() {
        return new MarketingStrategySnapshot(
                uspRepository.findAllByOrderByPriorityDescIdAsc().stream().map(StrategyDtos.Usp::from).toList(),
                icpRepository.findAllByOrderByIdAsc().stream().map(StrategyDtos.Icp::from).toList(),
                positioningRepository.findAllByOrderByIdAsc().stream().map(StrategyDtos.Positioning::from).toList(),
                pillarRepository.findAllByOrderByPriorityDescIdAsc().stream()
                        .map(StrategyDtos.ContentPillar::from).toList(),
                funnelRepository.findAllByOrderBySequenceNumberAsc().stream()
                        .map(StrategyDtos.FunnelStage::from).toList(),
                conversionGoalRepository.findAllByOrderByIdAsc().stream()
                        .map(StrategyDtos.ConversionGoal::from).toList(),
                socialProofRepository.findAllByOrderByIdAsc().stream().map(StrategyDtos.SocialProof::from).toList());
    }
}
