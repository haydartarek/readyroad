package com.readyroad.readyroadbackend.marketing.approval;

import com.fasterxml.jackson.databind.JsonNode;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.domain.AgentApproval;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.ApprovalDecision;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import com.readyroad.readyroadbackend.marketing.repository.AgentApprovalRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.task.TaskStateMachine;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private static final String SYSTEM_ACTOR = "SYSTEM";

    private final AgentTaskRepository taskRepository;
    private final AgentApprovalRepository approvalRepository;
    private final TaskStateMachine stateMachine;
    private final MarketingAuditService auditService;
    private final List<ApprovalDecisionHandler> decisionHandlers;

    @Transactional
    public AgentTask approve(Long taskId, String actor, String reason) {
        AgentTask task = taskForApproval(taskId);
        AgentApproval approval = currentApproval(task);
        ensureCurrentPayload(task, approval);
        handlers(task).forEach(handler -> handler.validateApproval(task, actor, reason));

        stateMachine.validate(task.getStatus(), TaskStatus.APPROVED);
        Instant now = Instant.now();
        task.setStatus(TaskStatus.APPROVED);
        task.setApprovedBy(requireActor(actor));
        task.setApprovedAt(now);
        task.setRejectedBy(null);
        task.setRejectedAt(null);
        task.setRejectionReason(null);

        approval.setDecision(ApprovalDecision.APPROVED);
        approval.setApprovedBy(actor);
        approval.setApprovedAt(now);
        approval.setReason(reason);

        if (task.getScheduledAt() != null && task.getScheduledAt().isAfter(now)) {
            stateMachine.validate(TaskStatus.APPROVED, TaskStatus.SCHEDULED);
            task.setStatus(TaskStatus.SCHEDULED);
        }

        auditService.recordTaskEvent(task, "TASK_APPROVED", actor,
                auditService.detail("payloadVersion", task.getPayloadVersion()));
        return task;
    }

    @Transactional
    public AgentTask reject(Long taskId, String actor, String reason) {
        AgentTask task = taskForApproval(taskId);
        AgentApproval approval = currentApproval(task);
        ensureCurrentPayload(task, approval);
        List<ApprovalDecisionHandler> handlers = handlers(task);
        handlers.forEach(handler -> handler.validateRejection(task, actor, reason));

        stateMachine.validate(task.getStatus(), TaskStatus.REJECTED);
        Instant now = Instant.now();
        task.setStatus(TaskStatus.REJECTED);
        task.setRejectedBy(requireActor(actor));
        task.setRejectedAt(now);
        task.setRejectionReason(reason);

        approval.setDecision(ApprovalDecision.REJECTED);
        approval.setRejectedBy(actor);
        approval.setRejectedAt(now);
        approval.setReason(reason);

        handlers.forEach(handler -> handler.afterRejection(task, actor, reason));

        auditService.recordTaskEvent(task, "TASK_REJECTED", actor,
                auditService.detail("payloadVersion", task.getPayloadVersion()));
        return task;
    }

    @Transactional
    public AgentTask replaceApprovedPayload(
            Long taskId,
            JsonNode replacement,
            boolean withinStandingAuthorization,
            String actor) {
        AgentTask task = taskRepository.findByIdForUpdate(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown task: " + taskId));
        if (task.getStatus() != TaskStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED tasks can replace an approved payload");
        }
        if (replacement == null) {
            throw new IllegalArgumentException("replacement payload is required");
        }

        task.setPayload(replacement.deepCopy());
        task.setPayloadVersion(task.getPayloadVersion() + 1);

        if (withinStandingAuthorization) {
            Instant now = Instant.now();
            task.setRequiresApproval(false);
            task.setApprovalMode(ApprovalMode.STANDING_OWNER_AUTHORIZATION);
            task.setApprovalSource(ApprovalMetadata.MASTER_SPEC_V3);
            task.setApprovedBy(SYSTEM_ACTOR);
            task.setApprovedAt(now);
            task.setRejectedBy(null);
            task.setRejectedAt(null);
            task.setRejectionReason(null);
            approvalRepository.save(standingApproval(task));
        } else {
            stateMachine.validate(TaskStatus.APPROVED, TaskStatus.WAITING_APPROVAL);
            task.setStatus(TaskStatus.WAITING_APPROVAL);
            task.setRequiresApproval(true);
            task.setApprovalMode(ApprovalMode.HUMAN_APPROVAL);
            task.setApprovalSource(ApprovalMetadata.MASTER_SPEC_V3);
            task.setApprovedBy(null);
            task.setApprovedAt(null);
            approvalRepository.save(pendingApproval(task, actor));
        }

        auditService.recordTaskEvent(task, "TASK_PAYLOAD_UPDATED", actor,
                auditService.detail("payloadVersion", task.getPayloadVersion()));
        return task;
    }

    private AgentTask taskForApproval(Long taskId) {
        AgentTask task = taskRepository.findByIdForUpdate(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown task: " + taskId));
        if (task.getStatus() != TaskStatus.WAITING_APPROVAL) {
            throw new IllegalStateException("Task is not waiting for approval: " + taskId);
        }
        return task;
    }

    private AgentApproval currentApproval(AgentTask task) {
        return approvalRepository.findByTaskIdAndPayloadVersion(task.getId(), task.getPayloadVersion())
                .filter(approval -> approval.getDecision() == ApprovalDecision.PENDING)
                .orElseThrow(() -> new IllegalStateException(
                        "Current payload has no pending approval: " + task.getId()));
    }

    private static void ensureCurrentPayload(AgentTask task, AgentApproval approval) {
        if (!approval.getPayloadSnapshot().equals(task.getPayload())) {
            throw new IllegalStateException("Approval payload snapshot is stale");
        }
    }

    private List<ApprovalDecisionHandler> handlers(AgentTask task) {
        return decisionHandlers.stream().filter(handler -> handler.supports(task)).toList();
    }

    private static AgentApproval pendingApproval(AgentTask task, String actor) {
        AgentApproval approval = baseApproval(task, actor);
        approval.setDecision(ApprovalDecision.PENDING);
        approval.setApprovalMode(ApprovalMode.HUMAN_APPROVAL);
        return approval;
    }

    private static AgentApproval standingApproval(AgentTask task) {
        Instant now = Instant.now();
        AgentApproval approval = baseApproval(task, SYSTEM_ACTOR);
        approval.setDecision(ApprovalDecision.APPROVED);
        approval.setApprovalMode(ApprovalMode.STANDING_OWNER_AUTHORIZATION);
        approval.setApprovedBy(SYSTEM_ACTOR);
        approval.setApprovedAt(now);
        approval.setReason("Standing owner authorization renewed for payload version");
        return approval;
    }

    private static AgentApproval baseApproval(AgentTask task, String actor) {
        AgentApproval approval = new AgentApproval();
        approval.setTaskId(task.getId());
        approval.setPayloadVersion(task.getPayloadVersion());
        approval.setRequestedAt(Instant.now());
        approval.setRequestedBy(requireActor(actor));
        approval.setPayloadSnapshot(task.getPayload().deepCopy());
        approval.setApprovalSource(task.getApprovalSource());
        return approval;
    }

    private static String requireActor(String actor) {
        if (actor == null || actor.isBlank()) {
            throw new IllegalArgumentException("actor is required");
        }
        return actor;
    }
}
