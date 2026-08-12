package com.readyroad.readyroadbackend.marketing.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMode;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "agent_schedules")
@Getter
@Setter
@NoArgsConstructor
public class AgentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_type", nullable = false, length = 64)
    private String agentType;

    @Column(name = "schedule_key", nullable = false, length = 128)
    private String scheduleKey;

    @Column(name = "task_type", nullable = false, length = 128)
    private String taskType;

    @Convert(converter = TaskPriorityConverter.class)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.NORMAL;

    @Column(name = "cron_expression", nullable = false, length = 128)
    private String cronExpression;

    @Column(name = "zone_id", nullable = false, length = 64)
    private String zoneId = "Europe/Brussels";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private JsonNode payload;

    @Column(name = "requires_approval", nullable = false)
    private boolean requiresApproval;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_mode", nullable = false, length = 64)
    private ApprovalMode approvalMode = ApprovalMode.STANDING_OWNER_AUTHORIZATION;

    @Column(name = "approval_source", nullable = false, length = 128)
    private String approvalSource = "MASTER_SPEC_V3";

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "last_run_at")
    private Instant lastRunAt;

    @Column(name = "next_run_at")
    private Instant nextRunAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
