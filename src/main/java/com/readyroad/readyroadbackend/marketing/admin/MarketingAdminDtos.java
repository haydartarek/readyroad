package com.readyroad.readyroadbackend.marketing.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.readyroad.readyroadbackend.marketing.domain.AgentExecutionLog;
import com.readyroad.readyroadbackend.marketing.domain.AgentSchedule;
import com.readyroad.readyroadbackend.marketing.domain.AgentSetting;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.MarketingAuditLog;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class MarketingAdminDtos {

    private MarketingAdminDtos() {}

    public record Overview(
            boolean enabled,
            Map<String, Long> tasksByStatus,
            long tasksToday,
            long activeAgents,
            long activeWorkers,
            List<AuditItem> recentActivity,
            List<ErrorItem> alerts,
            Instant generatedAt) {}

    public record AgentStatus(
            String agentType,
            String displayName,
            String description,
            boolean enabled,
            Instant lastRunAt,
            Instant lastSuccessAt,
            Instant lastFailureAt,
            long currentTasks,
            long tasksToday,
            long totalTasks,
            long completedTasks,
            long failedTasks,
            long retryCount,
            double successRate) {
    }

    public record TaskSummary(
            Long id,
            String agentType,
            String taskType,
            String status,
            String priority,
            int attempts,
            int maxAttempts,
            boolean requiresApproval,
            String approvalMode,
            String errorCode,
            String errorMessage,
            Instant scheduledAt,
            Instant nextRetryAt,
            Instant createdAt,
            Instant updatedAt) {
        static TaskSummary from(AgentTask task) {
            return new TaskSummary(
                    task.getId(), task.getAgentType(), task.getTaskType(), task.getStatus().name(),
                    task.getPriority().name(), task.getAttempts(), task.getMaxAttempts(),
                    task.isRequiresApproval(), task.getApprovalMode().name(), task.getErrorCode(),
                    task.getErrorMessage(), task.getScheduledAt(), task.getNextRetryAt(),
                    task.getCreatedAt(), task.getUpdatedAt());
        }
    }

    public record TaskPage(List<TaskSummary> items, long total) {}

    public record ErrorItem(
            Long id,
            Long taskId,
            Long attemptId,
            String eventCode,
            String message,
            Instant createdAt) {
        static ErrorItem from(AgentExecutionLog log) {
            return new ErrorItem(
                    log.getId(), log.getTaskId(), log.getAttemptId(), log.getEventCode(),
                    log.getMessage(), log.getCreatedAt());
        }
    }

    public record AuditItem(
            Long id,
            Long taskId,
            String eventType,
            String actor,
            String entityType,
            String entityId,
            Instant createdAt) {
        static AuditItem from(MarketingAuditLog log) {
            return new AuditItem(
                    log.getId(), log.getTaskId(), log.getEventType(), log.getActor(),
                    log.getEntityType(), log.getEntityId(), log.getCreatedAt());
        }
    }

    public record SettingItem(
            Long id, String agentType, String key, Object value, String updatedBy, Instant updatedAt) {
        static SettingItem from(AgentSetting setting) {
            return new SettingItem(
                    setting.getId(), setting.getAgentType(), setting.getSettingKey(),
                    plainJsonValue(setting.getSettingValue()), setting.getUpdatedBy(), setting.getUpdatedAt());
        }
    }

    public record ScheduleItem(
            Long id,
            String agentType,
            String key,
            String taskType,
            String cronExpression,
            Short intervalDays,
            String zoneId,
            boolean enabled,
            Instant lastRunAt,
            Instant nextRunAt) {
        static ScheduleItem from(AgentSchedule schedule) {
            return new ScheduleItem(
                    schedule.getId(), schedule.getAgentType(), schedule.getScheduleKey(), schedule.getTaskType(),
                    schedule.getCronExpression(), schedule.getIntervalDays(), schedule.getZoneId(), schedule.isEnabled(),
                    schedule.getLastRunAt(), schedule.getNextRunAt());
        }
    }

    public record Settings(List<SettingItem> settings, List<ScheduleItem> schedules) {}

    public record WorkerHealth(
            String status,
            boolean enabled,
            long activeWorkers,
            long runningTasks,
            long expiredLocks,
            long pollIntervalMs,
            int batchSize,
            long lockTtlSeconds,
            Instant checkedAt) {}

    public record EnabledRequest(
            @NotNull Boolean enabled,
            @NotBlank @Size(max = 255) String idempotencyKey) {}

    private static Object plainJsonValue(JsonNode value) {
        if (value == null || value.isNull()) {
            return null;
        }
        if (value.isArray()) {
            java.util.List<Object> items = new java.util.ArrayList<>();
            value.forEach(item -> items.add(plainJsonValue(item)));
            return java.util.Collections.unmodifiableList(items);
        }
        if (value.isObject()) {
            java.util.Map<String, Object> fields = new java.util.LinkedHashMap<>();
            value.properties().forEach(entry -> fields.put(entry.getKey(), plainJsonValue(entry.getValue())));
            return java.util.Collections.unmodifiableMap(fields);
        }
        if (value.isBoolean()) {
            return value.booleanValue();
        }
        if (value.isNumber()) {
            return value.numberValue();
        }
        return value.asText();
    }
}
