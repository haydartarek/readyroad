package com.readyroad.readyroadbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthExchangeRequest(
        @NotBlank(message = "Authorization code is required")
        String code,

        @NotBlank(message = "Redirect URI is required")
        String redirectUri,

        @NotBlank(message = "PKCE code verifier is required")
        String codeVerifier
) {
}
