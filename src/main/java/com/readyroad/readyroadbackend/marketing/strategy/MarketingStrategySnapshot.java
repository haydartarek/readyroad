package com.readyroad.readyroadbackend.marketing.strategy;

import java.util.List;

public record MarketingStrategySnapshot(
        List<StrategyDtos.Usp> usps,
        List<StrategyDtos.Icp> icps,
        List<StrategyDtos.Positioning> positioning,
        List<StrategyDtos.ContentPillar> contentPillars,
        List<StrategyDtos.FunnelStage> funnelStages,
        List<StrategyDtos.ConversionGoal> conversionGoals,
        List<StrategyDtos.SocialProof> socialProof) {}
