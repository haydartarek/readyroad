package com.readyroad.readyroadbackend.marketing.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.domain.AgentDefinition;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.repository.AgentDefinitionRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MarketingAdminControlTaskHandler implements MarketingTaskHandler {

    private final AgentDefinitionRepository definitionRepository;
    private final AgentTaskRepository taskRepository;
    private final MarketingAuditService auditService;

    @Override
    public boolean supports(String agentType, String taskType) {
        return MarketingAdminPlatformService.ADMIN_AGENT_TYPE.equals(agentType)
                && MarketingAdminPlatformService.AGENT_ENABLED_TASK.equals(taskType);
    }

    @Override
    @Transactional
    public void execute(ClaimedTask claimedTask) {
        JsonNode payload = claimedTask.payload();
        String target = requiredText(payload, "agentType");
        if (MarketingAdminPlatformService.ADMIN_AGENT_TYPE.equals(target)) {
            throw new MarketingTaskExecutionException("INVALID_AGENT_CONTROL", "Admin platform cannot disable itself");
        }
        if (!payload.has("enabled") || !payload.get("enabled").isBoolean()) {
            throw new MarketingTaskExecutionException("VALIDATION_ERROR", "enabled is required");
        }
        AgentTask task = taskRepository.findById(claimedTask.taskId())
                .orElseThrow(() -> new MarketingTaskExecutionException("TASK_NOT_FOUND", "Admin control task not found"));
        if (task.getApprovedBy() == null || task.getApprovedBy().isBlank()) {
            throw new MarketingTaskExecutionException("APPROVAL_REQUIRED", "Approved admin control task is required");
        }
        AgentDefinition definition = definitionRepository.findByAgentType(target)
                .orElseThrow(() -> new MarketingTaskExecutionException("AGENT_NOT_FOUND", "Marketing agent not found"));
        boolean enabled = payload.get("enabled").asBoolean();
        definition.setEnabled(enabled);
        definitionRepository.save(definition);
        auditService.recordEntityEvent(
                enabled ? "AGENT_ENABLED" : "AGENT_DISABLED",
                task.getApprovedBy(),
                "AGENT_DEFINITION",
                target,
                task.getId(),
                task.getCorrelationId());
    }

    private static String requiredText(JsonNode payload, String field) {
        JsonNode value = payload == null ? null : payload.get(field);
        if (value == null || !value.isTextual() || value.asText().isBlank()) {
            throw new MarketingTaskExecutionException("VALIDATION_ERROR", field + " is required");
        }
        return value.asText().trim();
    }
}
