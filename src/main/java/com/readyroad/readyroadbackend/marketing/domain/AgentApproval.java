package com.readyroad.readyroadbackend.marketing.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "agent_approvals")
@Getter
@Setter
@NoArgsConstructor
public class AgentApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "payload_version", nullable = false)
    private int payloadVersion;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "requested_by", nullable = false, length = 160)
    private String requestedBy;

    @Column(name = "approved_by", length = 160)
    private String approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "rejected_by", length = 160)
    private String rejectedBy;

    @Column(name = "rejected_at")
    private Instant rejectedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ApprovalDecision decision;

    @Column(columnDefinition = "text")
    private String reason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_snapshot", nullable = false)
    private JsonNode payloadSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_mode", nullable = false, length = 64)
    private ApprovalMode approvalMode;

    @Column(name = "approval_source", nullable = false, length = 128)
    private String approvalSource;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
