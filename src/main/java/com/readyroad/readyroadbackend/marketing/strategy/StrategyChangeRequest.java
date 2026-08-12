package com.readyroad.readyroadbackend.marketing.strategy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record StrategyChangeRequest(
        @NotNull StrategyResourceType resourceType,
        String resourceId,
        @NotNull Map<String, Object> data,
        @NotBlank String idempotencyKey) {}
