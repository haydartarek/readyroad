package com.readyroad.readyroadbackend.marketing.task;

import com.readyroad.readyroadbackend.marketing.audit.ExecutionLogService;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.AgentTaskAttempt;
import com.readyroad.readyroadbackend.marketing.domain.ExecutionLogLevel;
import com.readyroad.readyroadbackend.marketing.domain.TaskAttemptStatus;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskAttemptRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskClaimService {

    private static final String LOCK_EXPIRED_CODE = "WORKER_LOCK_EXPIRED";

    private final AgentTaskRepository taskRepository;
    private final AgentTaskAttemptRepository attemptRepository;
    private final TaskStateMachine stateMachine;
    private final MarketingRetryPolicy retryPolicy;
    private final MarketingProperties properties;
    private final MarketingAuditService auditService;
    private final ExecutionLogService executionLogService;

    @Transactional
    public List<ClaimedTask> claimNextBatch(String workerId) {
        requireWorkerId(workerId);
        Instant now = Instant.now();
        recoverExpiredLocks(now);

        int batchSize = properties.getWorker().getBatchSize();
        Duration lockTtl = properties.getWorker().getLockTtl();
        List<AgentTask> candidates = taskRepository.claimEligibleTasks(now, batchSize);
        List<ClaimedTask> claimed = new ArrayList<>(candidates.size());

        for (AgentTask task : candidates) {
            stateMachine.validate(task.getStatus(), TaskStatus.RUNNING);
            task.setStatus(TaskStatus.RUNNING);
            task.setAttempts(task.getAttempts() + 1);
            task.setStartedAt(task.getStartedAt() == null ? now : task.getStartedAt());
            task.setLockedBy(workerId);
            task.setLockedAt(now);
            task.setLockExpiresAt(now.plus(lockTtl));
            task.setNextRetryAt(null);
            task.setErrorCode(null);
            task.setErrorMessage(null);

            AgentTaskAttempt attempt = new AgentTaskAttempt();
            attempt.setTaskId(task.getId());
            attempt.setAttemptNumber(task.getAttempts());
            attempt.setStatus(TaskAttemptStatus.RUNNING);
            attempt.setWorkerId(workerId);
            attempt.setStartedAt(now);
            attempt = attemptRepository.save(attempt);

            auditService.recordTaskEvent(task, "TASK_CLAIMED", workerId,
                    auditService.detail("attempt", task.getAttempts()));
            executionLogService.record(task.getId(), attempt.getId(), ExecutionLogLevel.INFO,
                    "TASK_CLAIMED", "Task claimed by worker", auditService.detail("workerId", workerId));

            claimed.add(new ClaimedTask(
                    task.getId(),
                    task.getAgentType(),
                    task.getTaskType(),
                    task.getPayload().deepCopy(),
                    task.getPayloadVersion(),
                    task.getPriority(),
                    task.getAttempts(),
                    task.getCorrelationId()));
        }
        return List.copyOf(claimed);
    }

    private void recoverExpiredLocks(Instant now) {
        for (AgentTask task : taskRepository.findExpiredRunningTasks(TaskStatus.RUNNING, now)) {
            AgentTaskAttempt attempt = attemptRepository
                    .findFirstByTaskIdAndStatusOrderByAttemptNumberDesc(task.getId(), TaskAttemptStatus.RUNNING)
                    .orElseThrow(() -> new IllegalStateException(
                            "RUNNING task has no RUNNING attempt: " + task.getId()));

            attempt.setStatus(TaskAttemptStatus.INTERRUPTED);
            attempt.setCompletedAt(now);
            attempt.setErrorCode(LOCK_EXPIRED_CODE);
            attempt.setErrorMessage("Worker lock expired before task completion");
            attempt.setRetryable(true);

            task.setErrorCode(LOCK_EXPIRED_CODE);
            task.setErrorMessage("Worker lock expired before task completion");
            clearLock(task);

            var delay = retryPolicy.delayAfterAttempt(task.getAttempts());
            if (task.getAttempts() < task.getMaxAttempts() && delay.isPresent()) {
                stateMachine.validate(TaskStatus.RUNNING, TaskStatus.RETRY_SCHEDULED);
                Instant retryAt = now.plus(delay.orElseThrow());
                task.setStatus(TaskStatus.RETRY_SCHEDULED);
                task.setNextRetryAt(retryAt);
                attempt.setNextRetryAt(retryAt);
            } else {
                stateMachine.validate(TaskStatus.RUNNING, TaskStatus.FAILED);
                task.setStatus(TaskStatus.FAILED);
                task.setFailedAt(now);
            }

            attemptRepository.save(attempt);
            auditService.recordTaskEvent(task, LOCK_EXPIRED_CODE, "SYSTEM",
                    auditService.detail("attempt", task.getAttempts()));
            executionLogService.record(task.getId(), attempt.getId(), ExecutionLogLevel.WARN,
                    LOCK_EXPIRED_CODE, "Worker lock expired", auditService.detail("attempt", task.getAttempts()));
        }
    }

    private static void clearLock(AgentTask task) {
        task.setLockedBy(null);
        task.setLockedAt(null);
        task.setLockExpiresAt(null);
    }

    private static void requireWorkerId(String workerId) {
        if (workerId == null || workerId.isBlank()) {
            throw new IllegalArgumentException("workerId is required");
        }
    }
}
