package com.readyroad.readyroadbackend.marketing.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class MarketingAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "event_type", nullable = false, length = 128)
    private String eventType;

    @Column(nullable = false, length = 160)
    private String actor;

    @Column(name = "entity_type", length = 128)
    private String entityType;

    @Column(name = "entity_id", length = 255)
    private String entityId;

    @Column(name = "correlation_id", length = 128)
    private String correlationId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "safe_details", nullable = false)
    private JsonNode safeDetails;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
