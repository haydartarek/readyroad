package com.readyroad.readyroadbackend.marketing.content;

import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public final class ContentAdminDtos {
    private ContentAdminDtos() {}

    public record GenerateRequest(
            @NotNull ContentSourceType sourceType,
            @NotBlank String sourceId,
            @Valid MarketingStrategyContextRequest strategyContext,
            @NotBlank String idempotencyKey) {}

    public record Status(
            boolean configured,
            String provider,
            String api,
            String primaryModel,
            String reviewModel,
            String reasoningEffort,
            List<String> languages,
            long packageCount,
            long variantCount) {}

    public record PackageItem(
            long id,
            String sourceType,
            String sourceId,
            String title,
            String status,
            int variants,
            Instant createdAt) {}
}
