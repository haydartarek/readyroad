package com.readyroad.readyroadbackend.marketing.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;

public record ClaimedTask(
        Long taskId,
        String agentType,
        String taskType,
        JsonNode payload,
        int payloadVersion,
        TaskPriority priority,
        int attemptNumber,
        String correlationId) {
}
