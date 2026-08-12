package com.readyroad.readyroadbackend.marketing.domain;

import com.fasterxml.jackson.databind.JsonNode;
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
@Table(name = "agent_execution_logs")
@Getter
@Setter
@NoArgsConstructor
public class AgentExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "attempt_id")
    private Long attemptId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ExecutionLogLevel level;

    @Column(name = "event_code", nullable = false, length = 128)
    private String eventCode;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "safe_context", nullable = false)
    private JsonNode safeContext;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
