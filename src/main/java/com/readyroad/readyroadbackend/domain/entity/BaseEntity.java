package com.readyroad.readyroadbackend.domain.entity;

import com.readyroad.readyroadbackend.util.TextNormalizer;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        normalizeTextFields();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        normalizeTextFields();
    }

    /**
     * Override in subclasses to normalize typographic (smart) quote characters
     * in text fields before every persist and update.
     * Called automatically by the JPA lifecycle — no manual invocation needed.
     * Default implementation is a no-op.
     *
     * @see TextNormalizer
     */
    protected void normalizeTextFields() {
        // no-op by default — overridden in content entities
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

