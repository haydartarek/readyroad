package com.readyroad.readyroadbackend.marketing.repository;

import com.readyroad.readyroadbackend.marketing.domain.AgentExecutionLog;
import com.readyroad.readyroadbackend.marketing.domain.ExecutionLogLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentExecutionLogRepository extends JpaRepository<AgentExecutionLog, Long> {
    Page<AgentExecutionLog> findByLevelOrderByCreatedAtDesc(ExecutionLogLevel level, Pageable pageable);
}
