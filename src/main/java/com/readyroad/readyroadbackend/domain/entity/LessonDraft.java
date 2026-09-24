package com.readyroad.readyroadbackend.domain.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "lesson_drafts")
public class LessonDraft {

    @Id
    @Column(name = "lesson_id")
    private Long lessonId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "base_version_number", nullable = false)
    private int baseVersionNumber;

    @Version
    @Column(nullable = false)
    private Long revision;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode document;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @Column(name = "updated_by_user_id")
    private Long updatedByUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected LessonDraft() {
    }

    public LessonDraft(
            Lesson lesson,
            int baseVersionNumber,
            JsonNode document,
            Long actorUserId) {

        if (lesson == null || lesson.getId() == null) {
            throw new IllegalArgumentException("Persisted lesson is required");
        }

        if (baseVersionNumber < 1) {
            throw new IllegalArgumentException("baseVersionNumber must be positive");
        }

        if (document == null || !document.isObject()) {
            throw new IllegalArgumentException("Lesson draft document must be a JSON object");
        }

        this.lesson = lesson;
        this.lessonId = lesson.getId();
        this.baseVersionNumber = baseVersionNumber;
        this.document = document;
        this.createdByUserId = actorUserId;
        this.updatedByUserId = actorUserId;
    }

    public void updateDocument(
            JsonNode document,
            Long actorUserId) {

        if (document == null || !document.isObject()) {
            throw new IllegalArgumentException("Lesson draft document must be a JSON object");
        }

        this.document = document;
        this.updatedByUserId = actorUserId;
    }

    public void rebase(
            int baseVersionNumber,
            JsonNode document,
            Long actorUserId) {

        if (baseVersionNumber < 1) {
            throw new IllegalArgumentException("baseVersionNumber must be positive");
        }

        this.baseVersionNumber = baseVersionNumber;
        updateDocument(document, actorUserId);
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

    public Long getLessonId() {
        return lessonId;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public int getBaseVersionNumber() {
        return baseVersionNumber;
    }

    public Long getRevision() {
        return revision;
    }

    public JsonNode getDocument() {
        return document;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public Long getUpdatedByUserId() {
        return updatedByUserId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}