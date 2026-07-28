package com.readyroad.readyroadbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdatePreferredLanguageRequest(
        @NotNull(message = "Preferred language is required")
        @Pattern(
                regexp = "^(en|nl|fr|ar)$",
                message = "Preferred language must be one of: en, nl, fr, ar")
        String preferredLanguage) {
}
