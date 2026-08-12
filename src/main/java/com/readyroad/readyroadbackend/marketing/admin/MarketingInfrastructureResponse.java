package com.readyroad.readyroadbackend.marketing.admin;

import java.util.Map;

public record MarketingInfrastructureResponse(
        boolean enabled,
        long pollIntervalMs,
        int batchSize,
        long lockTtlSeconds,
        Map<String, Long> tasksByStatus) {
}
