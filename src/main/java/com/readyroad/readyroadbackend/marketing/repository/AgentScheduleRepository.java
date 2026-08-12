package com.readyroad.readyroadbackend.marketing.repository;

import com.readyroad.readyroadbackend.marketing.domain.AgentSchedule;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgentScheduleRepository extends JpaRepository<AgentSchedule, Long> {
    List<AgentSchedule> findAllByOrderByAgentTypeAscScheduleKeyAsc();

    List<AgentSchedule> findByEnabledTrueAndNextRunAtLessThanEqualOrderByNextRunAtAsc(Instant now);

    @Query(value = """
            SELECT schedule.*
            FROM agent_schedules schedule
            WHERE schedule.enabled = TRUE
              AND schedule.next_run_at <= :now
            ORDER BY schedule.next_run_at ASC
            LIMIT :batchSize
            FOR UPDATE OF schedule SKIP LOCKED
            """, nativeQuery = true)
    List<AgentSchedule> claimDueSchedules(@Param("now") Instant now, @Param("batchSize") int batchSize);
}
