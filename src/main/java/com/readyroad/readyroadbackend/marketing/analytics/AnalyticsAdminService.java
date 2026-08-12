package com.readyroad.readyroadbackend.marketing.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.repository.AgentSettingRepository;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.time.LocalDate;
import java.time.Instant;
import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AnalyticsAdminService {

    private static final ZoneId OPERATIONS_ZONE = ZoneId.of("Europe/Brussels");

    private final GoogleServiceAccountCredentials credentials;
    private final MarketingProperties properties;
    private final AnalyticsStore store;
    private final AnalyticsSettingsService settingsService;
    private final AgentSettingRepository settingRepository;
    private final TaskCreationService taskCreationService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public AnalyticsAdminDtos.Status status() {
        LocalDate latest = store.latestSearchConsoleDate();
        List<String> alerts = new ArrayList<>();
        if (!credentials.isConfigured()) {
            alerts.add("GOOGLE_SERVICE_ACCOUNT_NOT_CONFIGURED");
        }
        if (latest != null && latest.isBefore(
                LocalDate.now(OPERATIONS_ZONE).minusDays(settingsService.current().noDataDays()))) {
            alerts.add("SEARCH_CONSOLE_DATA_DELAYED");
        }
        var latestSync = store.latestFullSyncTask();
        String syncStatus = String.valueOf(latestSync.getOrDefault("status", ""));
        if ("FAILED".equals(syncStatus)) {
            alerts.add("ANALYTICS_SOURCE_FAILED_AFTER_RETRIES");
        } else if (("RETRY_SCHEDULED".equals(syncStatus) || "RUNNING".equals(syncStatus))
                && olderThanThreshold(latestSync.get("created_at"), settingsService.current().sourceFailureHours())) {
            alerts.add("ANALYTICS_SOURCE_FAILURE_DURATION_EXCEEDED");
        }
        return new AnalyticsAdminDtos.Status(
                credentials.isConfigured(), "DEDICATED_READ_ONLY_SERVICE_ACCOUNT",
                properties.getAnalytics().getGa4AccountId(),
                "properties/" + properties.getAnalytics().getGa4PropertyId(),
                properties.getAnalytics().getSearchConsoleSiteUrl(), latest,
                store.sourceStatus(), List.copyOf(alerts));
    }

    @Transactional(readOnly = true)
    public AnalyticsAdminDtos.SettingsView settings() {
        return new AnalyticsAdminDtos.SettingsView(
                settingsService.current(), value(AnalyticsSettingsService.POLICY_KEY),
                value(AnalyticsSettingsService.THRESHOLDS_KEY));
    }

    @Transactional
    public MarketingTaskLifecycleResponse requestSync(String idempotencyKey, String actor) {
        if (!credentials.isConfigured()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Google read-only Service Account is not configured");
        }
        boolean initial = store.latestSearchConsoleDate() == null;
        var payload = objectMapper.createObjectNode()
                .put("initial", initial)
                .put("mode", initial ? "INITIAL_BACKFILL" : "INCREMENTAL");
        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                AnalyticsSettingsService.AGENT_TYPE, AnalyticsTaskHandler.FULL_SYNC, payload,
                TaskPriority.HIGH, null, actor, idempotencyKey, null, null,
                "ANALYTICS_SOURCE", "GOOGLE_READ_ONLY", ApprovalMetadata.standingOwnerAuthorization()));
        return MarketingTaskLifecycleResponse.from(result.task());
    }

    @Transactional
    public MarketingTaskLifecycleResponse requestSettingsUpdate(
            AnalyticsAdminDtos.SettingsUpdateRequest request, String actor) {
        var payload = objectMapper.createObjectNode();
        payload.set("policy", request.policy().deepCopy());
        payload.set("thresholds", request.thresholds().deepCopy());
        payload.put("actor", actor);
        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                AnalyticsSettingsService.AGENT_TYPE, AnalyticsTaskHandler.SETTINGS_UPDATE, payload,
                TaskPriority.NORMAL, null, actor, request.idempotencyKey(), null, null,
                "AGENT_SETTING", AnalyticsSettingsService.AGENT_TYPE,
                ApprovalMetadata.standingOwnerAuthorization()));
        return MarketingTaskLifecycleResponse.from(result.task());
    }

    @Transactional(readOnly = true)
    public AnalyticsAdminDtos.Discovery discovery(int requestedLimit) {
        int limit = Math.max(1, Math.min(requestedLimit, 200));
        LocalDate end = LocalDate.now(OPERATIONS_ZONE).minusDays(1);
        LocalDate start = end.minusDays(settingsService.current().windowDays() - 1L);
        return new AnalyticsAdminDtos.Discovery(
                store.opportunities(limit), store.contentGaps(limit),
                store.queryClassificationSummary(start, end),
                store.languagePerformance(start, end), store.devicePerformance(start, end));
    }

    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> reports(int limit) {
        return store.reports(limit);
    }

    private com.fasterxml.jackson.databind.JsonNode value(String key) {
        return settingRepository.findByAgentTypeAndSettingKey(AnalyticsSettingsService.AGENT_TYPE, key)
                .<com.fasterxml.jackson.databind.JsonNode>map(setting -> setting.getSettingValue().deepCopy())
                .orElseGet(objectMapper::createObjectNode);
    }

    private static boolean olderThanThreshold(Object value, int hours) {
        Instant createdAt = value instanceof java.sql.Timestamp timestamp
                ? timestamp.toInstant()
                : value instanceof java.time.OffsetDateTime offset ? offset.toInstant()
                : value instanceof Instant instant ? instant : null;
        return createdAt != null && Duration.between(createdAt, Instant.now()).toHours() >= hours;
    }
}
