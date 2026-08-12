package com.readyroad.readyroadbackend.marketing.repository;

import com.readyroad.readyroadbackend.marketing.domain.AgentExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentExecutionLogRepository extends JpaRepository<AgentExecutionLog, Long> {
}
