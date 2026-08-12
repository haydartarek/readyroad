package com.readyroad.readyroadbackend.marketing.task;

import com.readyroad.readyroadbackend.marketing.audit.ExecutionLogService;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.AgentTaskAttempt;
import com.readyroad.readyroadbackend.marketing.domain.ExecutionLogLevel;
import com.readyroad.readyroadbackend.marketing.domain.TaskAttemptStatus;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskAttemptRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskExecutionService {

    private final AgentTaskRepository taskRepository;
    private final AgentTaskAttemptRepository attemptRepository;
    private final TaskStateMachine stateMachine;
    private final MarketingRetryPolicy retryPolicy;
    private final MarketingAuditService auditService;
    private final ExecutionLogService executionLogService;

    @Transactional
    public void complete(Long taskId, String workerId) {
        AgentTask task = lockedRunningTask(taskId, workerId);
        AgentTaskAttempt attempt = runningAttempt(taskId);
        Instant now = Instant.now();

        stateMachine.validate(task.getStatus(), TaskStatus.COMPLETED);
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(now);
        clearFailure(task);
        clearLock(task);

        attempt.setStatus(TaskAttemptStatus.COMPLETED);
        attempt.setCompletedAt(now);

        auditService.recordTaskEvent(task, "TASK_COMPLETED", workerId,
                auditService.detail("attempt", task.getAttempts()));
        executionLogService.record(taskId, attempt.getId(), ExecutionLogLevel.INFO,
                "TASK_COMPLETED", "Task completed", auditService.detail("attempt", task.getAttempts()));
    }

    @Transactional
    public void fail(Long taskId, String workerId, MarketingTaskExecutionException failure) {
        AgentTask task = lockedRunningTask(taskId, workerId);
        AgentTaskAttempt attempt = runningAttempt(taskId);
        Instant now = Instant.now();
        String safeMessage = safeMessage(failure);

        task.setErrorCode(failure.errorCode());
        task.setErrorMessage(safeMessage);
        attempt.setErrorCode(failure.errorCode());
        attempt.setErrorMessage(safeMessage);
        attempt.setCompletedAt(now);
        boolean retryable = retryPolicy.isRetryable(failure.errorCode());
        attempt.setRetryable(retryable);
        clearLock(task);

        var delay = retryPolicy.delayAfterAttempt(task.getAttempts());
        boolean shouldRetry = retryable
                && task.getAttempts() < task.getMaxAttempts()
                && delay.isPresent();

        if (shouldRetry) {
            stateMachine.validate(TaskStatus.RUNNING, TaskStatus.RETRY_SCHEDULED);
            Instant retryAt = now.plus(delay.orElseThrow());
            task.setStatus(TaskStatus.RETRY_SCHEDULED);
            task.setNextRetryAt(retryAt);
            attempt.setStatus(TaskAttemptStatus.RETRY_SCHEDULED);
            attempt.setNextRetryAt(retryAt);
            auditService.recordTaskEvent(task, "TASK_RETRY_SCHEDULED", workerId,
                    auditService.detail("attempt", task.getAttempts()));
        } else {
            stateMachine.validate(TaskStatus.RUNNING, TaskStatus.FAILED);
            task.setStatus(TaskStatus.FAILED);
            task.setFailedAt(now);
            attempt.setStatus(TaskAttemptStatus.FAILED);
            auditService.recordTaskEvent(task, "TASK_FAILED", workerId,
                    auditService.detail("errorCode", failure.errorCode()));
        }

        executionLogService.record(taskId, attempt.getId(), ExecutionLogLevel.ERROR,
                failure.errorCode(), safeMessage, auditService.detail("retryable", shouldRetry));
    }

    private AgentTask lockedRunningTask(Long taskId, String workerId) {
        AgentTask task = taskRepository.findByIdForUpdate(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown task: " + taskId));
        if (task.getStatus() != TaskStatus.RUNNING || !workerId.equals(task.getLockedBy())) {
            throw new IllegalStateException("Task is not owned by worker: " + taskId);
        }
        return task;
    }

    private AgentTaskAttempt runningAttempt(Long taskId) {
        return attemptRepository
                .findFirstByTaskIdAndStatusOrderByAttemptNumberDesc(taskId, TaskAttemptStatus.RUNNING)
                .orElseThrow(() -> new IllegalStateException("RUNNING attempt not found for task: " + taskId));
    }

    private static String safeMessage(MarketingTaskExecutionException failure) {
        String message = failure.getMessage();
        if (message == null || message.isBlank()) {
            return "Task execution failed";
        }
        return message.length() <= 500 ? message : message.substring(0, 500);
    }

    private static void clearFailure(AgentTask task) {
        task.setErrorCode(null);
        task.setErrorMessage(null);
        task.setNextRetryAt(null);
    }

    private static void clearLock(AgentTask task) {
        task.setLockedBy(null);
        task.setLockedAt(null);
        task.setLockExpiresAt(null);
    }
}
