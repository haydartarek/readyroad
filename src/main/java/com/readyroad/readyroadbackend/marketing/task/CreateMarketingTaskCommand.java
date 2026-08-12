package com.readyroad.readyroadbackend.marketing.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import java.time.Instant;

public record CreateMarketingTaskCommand(
        String agentType,
        String taskType,
        JsonNode payload,
        TaskPriority priority,
        Instant scheduledAt,
        String createdBy,
        String idempotencyKey,
        String correlationId,
        Long parentTaskId,
        String sourceType,
        String sourceId,
        ApprovalMetadata approvalMetadata) {

    public CreateMarketingTaskCommand {
        requireText(agentType, "agentType");
        requireText(taskType, "taskType");
        requireText(createdBy, "createdBy");
        requireText(idempotencyKey, "idempotencyKey");
        if (approvalMetadata == null) {
            throw new IllegalArgumentException("approvalMetadata is required");
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }
}
