package com.readyroad.readyroadbackend.marketing.content;

import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextRequest;

public record ContentTaskPayload(
        ContentSourceType sourceType,
        String sourceId,
        MarketingStrategyContextRequest strategyContext) {}
