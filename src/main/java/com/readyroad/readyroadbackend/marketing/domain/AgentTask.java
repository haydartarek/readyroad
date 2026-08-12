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
import jakarta.persistence.Version;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "agent_tasks")
@Getter
@Setter
@NoArgsConstructor
public class AgentTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_type", nullable = false, length = 64)
    private String agentType;

    @Column(name = "task_type", nullable = false, length = 128)
    private String taskType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private JsonNode payload;

    @Column(name = "payload_version", nullable = false)
    private int payloadVersion = 1;

    @Convert(converter = TaskPriorityConverter.class)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TaskStatus status;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts = 4;

    @Column(name = "next_retry_at")
    private Instant nextRetryAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, length = 160)
    private String createdBy;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "error_code", length = 128)
    private String errorCode;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "requires_approval", nullable = false)
    private boolean requiresApproval;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_mode", nullable = false, length = 64)
    private ApprovalMode approvalMode;

    @Column(name = "approval_source", nullable = false, length = 128)
    private String approvalSource;

    @Column(name = "approved_by", length = 160)
    private String approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "rejected_by", length = 160)
    private String rejectedBy;

    @Column(name = "rejected_at")
    private Instant rejectedAt;

    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason;

    @Column(name = "idempotency_key", nullable = false, length = 255)
    private String idempotencyKey;

    @Column(name = "correlation_id", nullable = false, length = 128)
    private String correlationId;

    @Column(name = "parent_task_id")
    private Long parentTaskId;

    @Column(name = "source_type", length = 128)
    private String sourceType;

    @Column(name = "source_id", length = 255)
    private String sourceId;

    @Column(name = "locked_by", length = 160)
    private String lockedBy;

    @Column(name = "locked_at")
    private Instant lockedAt;

    @Column(name = "lock_expires_at")
    private Instant lockExpiresAt;

    @Version
    @Column(nullable = false)
    private long version;

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
