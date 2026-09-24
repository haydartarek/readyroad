package com.readyroad.readyroadbackend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "lesson_categories")
public class LessonCategory {

    @EmbeddedId
    private LessonCategoryId id;

    @MapsId("lessonId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected LessonCategory() {
    }

    public LessonCategory(
            Lesson lesson,
            Category category,
            boolean primary,
            int displayOrder) {

        if (lesson == null || lesson.getId() == null) {
            throw new IllegalArgumentException("Persisted lesson is required");
        }

        if (category == null || category.getId() == null) {
            throw new IllegalArgumentException("Persisted category is required");
        }

        if (displayOrder < 0) {
            throw new IllegalArgumentException("displayOrder cannot be negative");
        }

        this.lesson = lesson;
        this.category = category;
        this.primary = primary;
        this.displayOrder = displayOrder;

        this.id = new LessonCategoryId(
                lesson.getId(),
                category.getId());
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public LessonCategoryId getId() {
        return id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        if (displayOrder < 0) {
            throw new IllegalArgumentException("displayOrder cannot be negative");
        }

        this.displayOrder = displayOrder;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}