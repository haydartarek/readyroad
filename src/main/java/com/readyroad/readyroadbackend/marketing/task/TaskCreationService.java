package com.readyroad.readyroadbackend.marketing.task;

import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskCreationService {

    private final AgentTaskRepository taskRepository;
    private final TaskPersistenceService persistenceService;
    private final MarketingAuditService auditService;

    public TaskCreationResult create(CreateMarketingTaskCommand command) {
        return taskRepository.findByAgentTypeAndTaskTypeAndIdempotencyKey(
                        command.agentType(), command.taskType(), command.idempotencyKey())
                .map(task -> duplicate(task, command.createdBy()))
                .orElseGet(() -> persistOrResolveDuplicate(command));
    }

    private TaskCreationResult persistOrResolveDuplicate(CreateMarketingTaskCommand command) {
        try {
            return new TaskCreationResult(persistenceService.persist(command), true);
        } catch (DataIntegrityViolationException duplicate) {
            AgentTask existing = taskRepository.findByAgentTypeAndTaskTypeAndIdempotencyKey(
                            command.agentType(), command.taskType(), command.idempotencyKey())
                    .orElseThrow(() -> duplicate);
            return duplicate(existing, command.createdBy());
        }
    }

    private TaskCreationResult duplicate(AgentTask task, String actor) {
        auditService.recordTaskEvent(task, "DUPLICATE_TASK_REJECTED", actor,
                auditService.detail("originalTaskId", task.getId()));
        return new TaskCreationResult(task, false);
    }
}
