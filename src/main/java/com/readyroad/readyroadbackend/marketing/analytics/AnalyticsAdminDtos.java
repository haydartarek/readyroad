package com.readyroad.readyroadbackend.marketing.analytics;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class AnalyticsAdminDtos {

    private AnalyticsAdminDtos() {}

    public record Status(
            boolean serviceAccountConfigured,
            String authenticationMode,
            String ga4AccountId,
            String ga4PropertyResource,
            String searchConsoleSiteUrl,
            LocalDate latestSearchConsoleDate,
            List<Map<String, Object>> sources,
            List<String> alerts) {}

    public record SettingsView(AnalyticsSettings values, JsonNode policy, JsonNode thresholds) {}

    public record SettingsUpdateRequest(
            @NotNull JsonNode policy,
            @NotNull JsonNode thresholds,
            @NotBlank @Size(max = 255) String idempotencyKey) {}

    public record SyncRequest(@NotBlank @Size(max = 255) String idempotencyKey) {}

    public record Discovery(
            List<Map<String, Object>> opportunities,
            List<Map<String, Object>> contentGaps,
            List<Map<String, Object>> queryClassifications,
            List<Map<String, Object>> languages,
            List<Map<String, Object>> devices) {}
}
