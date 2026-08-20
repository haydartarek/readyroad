package com.readyroad.readyroadbackend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record AdminSystemSettingsUpdateRequest(
        @NotBlank(message = "siteName is required")
        String siteName,

        @NotBlank(message = "defaultLanguage is required")
        @Pattern(regexp = "en|ar|nl|fr", message = "defaultLanguage must be one of en, ar, nl, fr")
        String defaultLanguage,

        @NotNull(message = "maintenanceMode is required")
        Boolean maintenanceMode,

        @NotNull(message = "allowRegistrations is required")
        Boolean allowRegistrations,

        @NotNull(message = "examQuestions is required")
        @Min(value = 10, message = "examQuestions must be at least 10")
        @Max(value = 100, message = "examQuestions must be at most 100")
        Integer examQuestions,

        @NotNull(message = "examDurationMinutes is required")
        @DecimalMin(value = "1.00", message = "examDurationMinutes must be at least 1")
        @DecimalMax(value = "120.00", message = "examDurationMinutes must be at most 120")
        BigDecimal examDurationMinutes,

        @NotNull(message = "passingScorePercent is required")
        @Min(value = 50, message = "passingScorePercent must be at least 50")
        @Max(value = 100, message = "passingScorePercent must be at most 100")
        Integer passingScorePercent) {
}
