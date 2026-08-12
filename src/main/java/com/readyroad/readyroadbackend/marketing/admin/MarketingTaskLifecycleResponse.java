package com.readyroad.readyroadbackend.marketing.admin;

import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import java.time.Instant;

public record MarketingTaskLifecycleResponse(
        Long id,
        String agentType,
        String taskType,
        String status,
        int payloadVersion,
        String priority,
        int attempts,
        int maxAttempts,
        boolean requiresApproval,
        String approvalMode,
        String correlationId,
        Instant scheduledAt,
        Instant nextRetryAt,
        String errorCode) {

    public static MarketingTaskLifecycleResponse from(AgentTask task) {
        return new MarketingTaskLifecycleResponse(
                task.getId(),
                task.getAgentType(),
                task.getTaskType(),
                task.getStatus().name(),
                task.getPayloadVersion(),
                task.getPriority().name(),
                task.getAttempts(),
                task.getMaxAttempts(),
                task.isRequiresApproval(),
                task.getApprovalMode().name(),
                task.getCorrelationId(),
                task.getScheduledAt(),
                task.getNextRetryAt(),
                task.getErrorCode());
    }
}
