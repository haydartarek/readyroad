package com.readyroad.readyroadbackend.marketing.strategy;

public record MarketingStrategyContext(
        StrategyDtos.Usp usp,
        StrategyDtos.Icp icp,
        StrategyDtos.Positioning positioning,
        StrategyDtos.ContentPillar contentPillar,
        StrategyDtos.FunnelStage funnelStage,
        StrategyDtos.ConversionGoal conversionGoal) {}
