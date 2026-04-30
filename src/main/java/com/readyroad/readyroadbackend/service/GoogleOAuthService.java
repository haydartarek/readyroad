package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.dto.GoogleAuthExchangeRequest;
import com.readyroad.readyroadbackend.exception.SocialAuthException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthService {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(12);

    private final ObjectMapper objectMapper;
    private final BackendMessageService messages;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();

    @Value("${google.oauth.client-id:}")
    private String clientId;

    @Value("${google.oauth.client-secret:}")
    private String clientSecret;

    @Value("${google.oauth.token-uri:https://oauth2.googleapis.com/token}")
    private String tokenUri;

    @Value("${google.oauth.user-info-uri:https://openidconnect.googleapis.com/v1/userinfo}")
    private String userInfoUri;

    public GoogleUserInfo exchangeCodeForUser(GoogleAuthExchangeRequest request) {
        ensureConfigured();
        GoogleTokenResponse tokenResponse = exchangeAuthorizationCode(request);

        if (!StringUtils.hasText(tokenResponse.accessToken())) {
            log.warn("Google token response did not contain an access token");
            throw new SocialAuthException(
                    HttpStatus.BAD_GATEWAY,
                    "GOOGLE_PROFILE_INVALID",
                    messages.get("auth.google.invalid_profile"));
        }

        return fetchUserInfo(tokenResponse.accessToken());
    }

    private void ensureConfigured() {
        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret)) {
            log.error("Google OAuth is not configured: missing client id or client secret");
            throw new SocialAuthException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "SOCIAL_AUTH_UNAVAILABLE",
                    messages.get("auth.google.unavailable"));
        }
    }

    private GoogleTokenResponse exchangeAuthorizationCode(GoogleAuthExchangeRequest request) {
        try {
            String body = buildFormBody(Map.of(
                    "code", request.code(),
                    "client_id", clientId,
                    "client_secret", clientSecret,
                    "redirect_uri", request.redirectUri(),
                    "grant_type", "authorization_code",
                    "code_verifier", request.codeVerifier()));

            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(tokenUri))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.warn("Google token exchange failed with status {}: {}", response.statusCode(), response.body());
                throw new SocialAuthException(
                        HttpStatus.BAD_REQUEST,
                        "GOOGLE_EXCHANGE_FAILED",
                        extractProviderErrorMessage(response.body(), messages.get("auth.google.exchange_failed")));
            }

            return objectMapper.readValue(response.body(), GoogleTokenResponse.class);
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            log.error("Google token exchange failed", ex);
            throw new SocialAuthException(
                    HttpStatus.BAD_GATEWAY,
                    "GOOGLE_EXCHANGE_FAILED",
                    messages.get("auth.google.exchange_failed"));
        }
    }

    private GoogleUserInfo fetchUserInfo(String accessToken) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(userInfoUri))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.warn("Google user-info lookup failed with status {}: {}", response.statusCode(), response.body());
                throw new SocialAuthException(
                        HttpStatus.BAD_GATEWAY,
                        "GOOGLE_PROFILE_INVALID",
                        messages.get("auth.google.invalid_profile"));
            }

            GoogleUserInfo userInfo = objectMapper.readValue(response.body(), GoogleUserInfo.class);
            if (!StringUtils.hasText(userInfo.providerUserId()) || !StringUtils.hasText(userInfo.email())) {
                log.warn("Google user-info payload missing required fields: {}", response.body());
                throw new SocialAuthException(
                        HttpStatus.BAD_GATEWAY,
                        "GOOGLE_PROFILE_INVALID",
                        messages.get("auth.google.invalid_profile"));
            }

            return userInfo;
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            log.error("Google user-info lookup failed", ex);
            throw new SocialAuthException(
                    HttpStatus.BAD_GATEWAY,
                    "GOOGLE_PROFILE_INVALID",
                    messages.get("auth.google.invalid_profile"));
        }
    }

    private String extractProviderErrorMessage(String body, String fallbackMessage) {
        try {
            GoogleErrorResponse errorResponse = objectMapper.readValue(body, GoogleErrorResponse.class);
            if (StringUtils.hasText(errorResponse.errorDescription())) {
                return errorResponse.errorDescription();
            }
            if (StringUtils.hasText(errorResponse.error())) {
                return errorResponse.error();
            }
        } catch (IOException ignored) {
            // Ignore provider error parsing and return fallback below.
        }
        return fallbackMessage;
    }

    private String buildFormBody(Map<String, String> values) {
        return values.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                        + "="
                        + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GoogleTokenResponse(
            @JsonProperty("access_token")
            String accessToken,
            @JsonProperty("id_token")
            String idToken,
            @JsonProperty("token_type")
            String tokenType,
            String scope) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GoogleErrorResponse(
            String error,
            @JsonProperty("error_description")
            String errorDescription) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GoogleUserInfo(
            @JsonProperty("sub")
            String providerUserId,
            String email,
            @JsonProperty("email_verified")
            Boolean emailVerified,
            String name,
            @JsonProperty("given_name")
            String givenName,
            @JsonProperty("family_name")
            String familyName,
            String picture) {
    }
}
