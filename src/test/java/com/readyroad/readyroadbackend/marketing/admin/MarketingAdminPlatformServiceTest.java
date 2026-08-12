package com.readyroad.readyroadbackend.marketing.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMode;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.domain.AgentDefinition;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import com.readyroad.readyroadbackend.marketing.repository.AgentDefinitionRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationResult;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarketingAdminPlatformServiceTest {

    @Mock AgentDefinitionRepository definitionRepository;
    @Mock AgentTaskRepository taskRepository;
    @Mock TaskCreationService taskCreationService;
    @Mock MarketingAuditService auditService;
    @Spy ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks MarketingAdminPlatformService service;

    @Test
    void disablingAnAgentCreatesAnApprovalBoundControlTask() {
        AgentDefinition strategy = new AgentDefinition("STRATEGY", "Strategy", true);
        when(definitionRepository.findByAgentType("STRATEGY")).thenReturn(Optional.of(strategy));
        AgentTask controlTask = new AgentTask();
        controlTask.setId(81L);
        controlTask.setAgentType(MarketingAdminPlatformService.ADMIN_AGENT_TYPE);
        controlTask.setTaskType(MarketingAdminPlatformService.AGENT_ENABLED_TASK);
        controlTask.setStatus(TaskStatus.WAITING_APPROVAL);
        controlTask.setPriority(TaskPriority.HIGH);
        controlTask.setRequiresApproval(true);
        controlTask.setApprovalMode(ApprovalMode.HUMAN_APPROVAL);
        controlTask.setCorrelationId("agent-control");
        when(taskCreationService.create(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new TaskCreationResult(controlTask, true));

        MarketingTaskLifecycleResponse result = service.requestAgentEnabledChange(
                "STRATEGY", false, "agent-control-test", "admin");

        assertThat(result.status()).isEqualTo("WAITING_APPROVAL");
        ArgumentCaptor<CreateMarketingTaskCommand> command =
                ArgumentCaptor.forClass(CreateMarketingTaskCommand.class);
        verify(taskCreationService).create(command.capture());
        assertThat(command.getValue().agentType()).isEqualTo("ADMIN_PLATFORM");
        assertThat(command.getValue().taskType()).isEqualTo("AGENT_ENABLED_CHANGE");
        assertThat(command.getValue().idempotencyKey()).isEqualTo("agent-control-test");
        assertThat(command.getValue().approvalMetadata().approvalRequired()).isTrue();
        assertThat(command.getValue().payload().path("agentType").asText()).isEqualTo("STRATEGY");
        assertThat(command.getValue().payload().path("enabled").asBoolean()).isFalse();
    }

    @Test
    void manualRetryCreatesANewLinkedTaskAndPreservesApprovalPolicy() {
        AgentTask failed = failedTask();
        AgentTask replacement = new AgentTask();
        replacement.setId(91L);
        replacement.setAgentType("STRATEGY");
        replacement.setTaskType("STRATEGY_CHANGE");
        replacement.setStatus(TaskStatus.WAITING_APPROVAL);
        replacement.setPriority(TaskPriority.HIGH);
        replacement.setApprovalMode(ApprovalMode.HUMAN_APPROVAL);
        replacement.setCorrelationId("correlation-1");
        replacement.setIdempotencyKey("generated");
        when(taskRepository.findById(44L)).thenReturn(Optional.of(failed));
        when(taskCreationService.create(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new TaskCreationResult(replacement, true));

        MarketingTaskLifecycleResponse result = service.retry(44L, "admin");

        ArgumentCaptor<CreateMarketingTaskCommand> command =
                ArgumentCaptor.forClass(CreateMarketingTaskCommand.class);
        verify(taskCreationService).create(command.capture());
        assertThat(result.id()).isEqualTo(91L);
        assertThat(command.getValue().parentTaskId()).isEqualTo(44L);
        assertThat(command.getValue().sourceType()).isEqualTo("MANUAL_RETRY");
        assertThat(command.getValue().sourceId()).isEqualTo("44");
        assertThat(command.getValue().idempotencyKey()).startsWith("manual-retry-44-");
        assertThat(command.getValue().approvalMetadata())
                .isEqualTo(ApprovalMetadata.humanApproval("MASTER_SPEC_V3"));
    }

    private AgentTask failedTask() {
        AgentTask task = new AgentTask();
        task.setId(44L);
        task.setAgentType("STRATEGY");
        task.setTaskType("STRATEGY_CHANGE");
        task.setPayload(new ObjectMapper().createObjectNode().put("resourceType", "USP"));
        task.setStatus(TaskStatus.FAILED);
        task.setPriority(TaskPriority.HIGH);
        task.setRequiresApproval(true);
        task.setApprovalMode(ApprovalMode.HUMAN_APPROVAL);
        task.setApprovalSource("MASTER_SPEC_V3");
        task.setCorrelationId("correlation-1");
        return task;
    }
}
