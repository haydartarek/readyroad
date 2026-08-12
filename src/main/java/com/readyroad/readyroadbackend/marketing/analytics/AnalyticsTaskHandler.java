package com.readyroad.readyroadbackend.marketing.analytics;

import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsTaskHandler implements MarketingTaskHandler {

    public static final String FULL_SYNC = "ANALYTICS_FULL_SYNC";
    public static final String WEEKLY_REPORT = "ANALYTICS_WEEKLY_REPORT";
    public static final String MONTHLY_REPORT = "ANALYTICS_MONTHLY_REPORT";
    public static final String SETTINGS_UPDATE = "ANALYTICS_SETTINGS_UPDATE";
    private static final ZoneId OPERATIONS_ZONE = ZoneId.of("Europe/Brussels");

    private final AnalyticsSyncService syncService;
    private final AnalyticsReportService reportService;
    private final AnalyticsSettingsService settingsService;
    private final MarketingAuditService auditService;

    @Override
    public boolean supports(String agentType, String taskType) {
        return AnalyticsSettingsService.AGENT_TYPE.equals(agentType)
                && (FULL_SYNC.equals(taskType)
                        || WEEKLY_REPORT.equals(taskType)
                        || MONTHLY_REPORT.equals(taskType)
                        || SETTINGS_UPDATE.equals(taskType));
    }

    @Override
    public void execute(ClaimedTask task) {
        switch (task.taskType()) {
            case FULL_SYNC -> syncService.synchronize(task.taskId(), task.payload().path("initial").asBoolean(false));
            case WEEKLY_REPORT -> reportService.weekly(task.taskId(), LocalDate.now(OPERATIONS_ZONE));
            case MONTHLY_REPORT -> reportService.monthly(task.taskId(), LocalDate.now(OPERATIONS_ZONE));
            case SETTINGS_UPDATE -> updateSettings(task);
            default -> throw new MarketingTaskExecutionException(
                    "UNSUPPORTED_TASK_TYPE", "Unsupported analytics task type");
        }
    }

    private void updateSettings(ClaimedTask task) {
        var policy = task.payload().path("policy");
        var thresholds = task.payload().path("thresholds");
        if (!policy.isObject() || !thresholds.isObject()) {
            throw new MarketingTaskExecutionException(
                    "INVALID_ANALYTICS_SETTINGS", "Analytics settings payload is invalid");
        }
        try {
            settingsService.update(
                    policy, thresholds, task.payload().path("actor").asText("MARKETING_WORKER"));
            auditService.recordEntityEvent(
                    "ANALYTICS_SETTINGS_UPDATED",
                    task.payload().path("actor").asText("MARKETING_WORKER"),
                    "AGENT_SETTING", AnalyticsSettingsService.AGENT_TYPE,
                    task.taskId(), task.correlationId());
        } catch (IllegalArgumentException error) {
            throw new MarketingTaskExecutionException(
                    "INVALID_ANALYTICS_SETTINGS", "Analytics settings are outside the allowed range");
        }
    }
}
