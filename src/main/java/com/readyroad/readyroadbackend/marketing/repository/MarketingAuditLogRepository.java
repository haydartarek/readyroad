package com.readyroad.readyroadbackend.marketing.repository;

import com.readyroad.readyroadbackend.marketing.domain.MarketingAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketingAuditLogRepository extends JpaRepository<MarketingAuditLog, Long> {
    long countByEventType(String eventType);
}
