package com.readyroad.readyroadbackend.marketing.repository;

import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgentTaskRepository extends JpaRepository<AgentTask, Long> {

    Optional<AgentTask> findByAgentTypeAndTaskTypeAndIdempotencyKey(
            String agentType,
            String taskType,
            String idempotencyKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT task FROM AgentTask task WHERE task.id = :id")
    Optional<AgentTask> findByIdForUpdate(@Param("id") Long id);

    @Query(value = """
            SELECT task.*
            FROM agent_tasks task
            JOIN agent_definitions definition ON definition.agent_type = task.agent_type
            WHERE definition.enabled = TRUE
              AND (
                task.status = 'PENDING'
                OR (task.status = 'SCHEDULED' AND task.scheduled_at <= :now)
                OR (task.status = 'APPROVED' AND (task.scheduled_at IS NULL OR task.scheduled_at <= :now))
                OR (task.status = 'RETRY_SCHEDULED' AND task.next_retry_at <= :now)
              )
            ORDER BY task.priority DESC, task.scheduled_at ASC, task.created_at ASC
            LIMIT :batchSize
            FOR UPDATE OF task SKIP LOCKED
            """, nativeQuery = true)
    List<AgentTask> claimEligibleTasks(@Param("now") Instant now, @Param("batchSize") int batchSize);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT task FROM AgentTask task
            WHERE task.status = :status
              AND task.lockExpiresAt IS NOT NULL
              AND task.lockExpiresAt <= :now
            ORDER BY task.lockExpiresAt ASC
            """)
    List<AgentTask> findExpiredRunningTasks(
            @Param("status") TaskStatus status,
            @Param("now") Instant now);

    long countByStatus(TaskStatus status);
}
