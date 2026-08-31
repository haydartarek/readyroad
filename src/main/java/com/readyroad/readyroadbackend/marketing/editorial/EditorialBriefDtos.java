package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public final class EditorialBriefDtos {

    private EditorialBriefDtos() {}

    public record CreateRequest(
            @NotBlank String targetLanguage,
            @NotBlank @Size(max = 64) String searchIntent,
            @NotBlank @Size(max = 500) String workingTitle,
            @NotBlank @Size(max = 4000) String purpose,
            @NotNull @Valid MarketingStrategyContextRequest strategyContext,
            @NotNull @Size(min = 1, max = 12)
            List<@NotBlank @Size(max = 120) String> targetQueries,
            @NotNull List<@NotBlank @Size(max = 1000) String> sourceRequirements,
            boolean legalReviewRequired,
            @NotBlank @Size(max = 255) String idempotencyKey) {}
}
