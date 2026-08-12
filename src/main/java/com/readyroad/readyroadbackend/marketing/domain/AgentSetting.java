package com.readyroad.readyroadbackend.marketing.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "agent_settings")
@Getter
@Setter
@NoArgsConstructor
public class AgentSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_type", nullable = false, length = 64)
    private String agentType;

    @Column(name = "setting_key", nullable = false, length = 128)
    private String settingKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "setting_value", nullable = false)
    private JsonNode settingValue;

    @Column(name = "updated_by", nullable = false, length = 160)
    private String updatedBy;

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
