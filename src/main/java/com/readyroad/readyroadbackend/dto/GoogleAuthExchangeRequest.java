package com.readyroad.readyroadbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GoogleAuthExchangeRequest(
        @NotBlank(message = "Authorization code is required")
        String code,

        @NotBlank(message = "Redirect URI is required")
        String redirectUri,

        @NotBlank(message = "PKCE code verifier is required")
        String codeVerifier,

        @Pattern(
                regexp = "^(en|nl|fr|ar)$",
                message = "Preferred language must be one of: en, nl, fr, ar")
        String preferredLanguage
) {
    public GoogleAuthExchangeRequest(String code, String redirectUri, String codeVerifier) {
        this(code, redirectUri, codeVerifier, null);
    }
}
