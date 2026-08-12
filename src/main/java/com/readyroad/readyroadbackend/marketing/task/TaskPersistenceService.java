package com.readyroad.readyroadbackend.marketing.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.domain.AgentApproval;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.ApprovalDecision;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import com.readyroad.readyroadbackend.marketing.repository.AgentApprovalRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentDefinitionRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskPersistenceService {

    private static final String SYSTEM_ACTOR = "SYSTEM";

    private final AgentDefinitionRepository definitionRepository;
    private final AgentTaskRepository taskRepository;
    private final AgentApprovalRepository approvalRepository;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AgentTask persist(CreateMarketingTaskCommand command) {
        if (definitionRepository.findByAgentType(command.agentType()).isEmpty()) {
            throw new IllegalArgumentException("Unknown marketing agent: " + command.agentType());
        }

        AgentTask task = new AgentTask();
        task.setAgentType(command.agentType());
        task.setTaskType(command.taskType());
        task.setPayload(command.payload() == null ? objectMapper.createObjectNode() : command.payload().deepCopy());
        task.setPriority(command.priority() == null ? TaskPriority.NORMAL : command.priority());
        task.setScheduledAt(command.scheduledAt());
        task.setCreatedBy(command.createdBy());
        task.setIdempotencyKey(command.idempotencyKey());
        task.setCorrelationId(command.correlationId() == null || command.correlationId().isBlank()
                ? UUID.randomUUID().toString()
                : command.correlationId());
        task.setParentTaskId(command.parentTaskId());
        task.setSourceType(command.sourceType());
        task.setSourceId(command.sourceId());
        task.setRequiresApproval(command.approvalMetadata().approvalRequired());
        task.setApprovalMode(command.approvalMetadata().approvalMode());
        task.setApprovalSource(command.approvalMetadata().approvalSource());
        task.setStatus(initialStatus(command));

        AgentTask saved = taskRepository.saveAndFlush(task);
        approvalRepository.save(saved.isRequiresApproval()
                ? newPendingApproval(saved, command.createdBy())
                : newStandingApproval(saved));
        auditService.recordTaskEvent(saved, "TASK_CREATED", command.createdBy(),
                auditService.detail("status", saved.getStatus().name()));
        return saved;
    }

    private static TaskStatus initialStatus(CreateMarketingTaskCommand command) {
        if (command.approvalMetadata().approvalRequired()) {
            return TaskStatus.WAITING_APPROVAL;
        }
        if (command.scheduledAt() != null && command.scheduledAt().isAfter(Instant.now())) {
            return TaskStatus.SCHEDULED;
        }
        return TaskStatus.PENDING;
    }

    private static AgentApproval newPendingApproval(AgentTask task, String actor) {
        AgentApproval approval = new AgentApproval();
        approval.setTaskId(task.getId());
        approval.setPayloadVersion(task.getPayloadVersion());
        approval.setRequestedAt(Instant.now());
        approval.setRequestedBy(actor);
        approval.setDecision(ApprovalDecision.PENDING);
        approval.setPayloadSnapshot(task.getPayload().deepCopy());
        approval.setApprovalMode(task.getApprovalMode());
        approval.setApprovalSource(task.getApprovalSource());
        return approval;
    }

    private static AgentApproval newStandingApproval(AgentTask task) {
        Instant now = Instant.now();
        AgentApproval approval = new AgentApproval();
        approval.setTaskId(task.getId());
        approval.setPayloadVersion(task.getPayloadVersion());
        approval.setRequestedAt(now);
        approval.setRequestedBy(SYSTEM_ACTOR);
        approval.setApprovedBy(SYSTEM_ACTOR);
        approval.setApprovedAt(now);
        approval.setDecision(ApprovalDecision.APPROVED);
        approval.setReason("Standing owner authorization from MASTER_SPEC_V3");
        approval.setPayloadSnapshot(task.getPayload().deepCopy());
        approval.setApprovalMode(task.getApprovalMode());
        approval.setApprovalSource(task.getApprovalSource());
        return approval;
    }
}
