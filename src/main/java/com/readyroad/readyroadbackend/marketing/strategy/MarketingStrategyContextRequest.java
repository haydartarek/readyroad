package com.readyroad.readyroadbackend.marketing.strategy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MarketingStrategyContextRequest(
        @NotNull Long uspId,
        @NotBlank String icpId,
        @NotNull Long contentPillarId,
        @NotNull Long funnelStageId,
        @NotNull Long conversionGoalId) {}
