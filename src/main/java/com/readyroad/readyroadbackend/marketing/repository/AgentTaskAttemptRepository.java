package com.readyroad.readyroadbackend.marketing.repository;

import com.readyroad.readyroadbackend.marketing.domain.AgentTaskAttempt;
import com.readyroad.readyroadbackend.marketing.domain.TaskAttemptStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentTaskAttemptRepository extends JpaRepository<AgentTaskAttempt, Long> {
    Optional<AgentTaskAttempt> findFirstByTaskIdAndStatusOrderByAttemptNumberDesc(
            Long taskId,
            TaskAttemptStatus status);
}
