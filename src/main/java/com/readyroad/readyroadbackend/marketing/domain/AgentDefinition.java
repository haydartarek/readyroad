package com.readyroad.readyroadbackend.marketing.domain;

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

@Entity
@Table(name = "agent_definitions")
@Getter
@Setter
@NoArgsConstructor
public class AgentDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_type", nullable = false, unique = true, length = 64)
    private String agentType;

    @Column(name = "display_name", nullable = false, length = 160)
    private String displayName;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public AgentDefinition(String agentType, String displayName, boolean enabled) {
        this.agentType = agentType;
        this.displayName = displayName;
        this.enabled = enabled;
    }

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
