package com.readyroad.readyroadbackend.domain.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.readyroad.readyroadbackend.domain.enums.LessonVersionSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "lesson_versions")
public class LessonVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false, updatable = false)
    private Lesson lesson;

    @Column(name = "version_number", nullable = false, updatable = false)
    private int versionNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, updatable = false, columnDefinition = "jsonb")
    private JsonNode document;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 32)
    private LessonVersionSource source;

    @Column(name = "change_note", updatable = false, columnDefinition = "text")
    private String changeNote;

    @Column(name = "published_by_user_id", updatable = false)
    private Long publishedByUserId;

    @Column(name = "published_at", nullable = false, updatable = false)
    private Instant publishedAt;

    protected LessonVersion() {
    }

    private LessonVersion(
            Lesson lesson,
            int versionNumber,
            JsonNode document,
            LessonVersionSource source,
            String changeNote,
            Long publishedByUserId) {

        if (lesson == null || lesson.getId() == null) {
            throw new IllegalArgumentException("Persisted lesson is required");
        }

        if (versionNumber < 1) {
            throw new IllegalArgumentException("versionNumber must be positive");
        }

        if (document == null || !document.isObject()) {
            throw new IllegalArgumentException("Lesson version document must be a JSON object");
        }

        if (source == null) {
            throw new IllegalArgumentException("Lesson version source is required");
        }

        this.lesson = lesson;
        this.versionNumber = versionNumber;
        this.document = document;
        this.source = source;
        this.changeNote = changeNote;
        this.publishedByUserId = publishedByUserId;
    }

    public static LessonVersion cms(
            Lesson lesson,
            int versionNumber,
            JsonNode document,
            String changeNote,
            Long publishedByUserId) {

        return new LessonVersion(
                lesson,
                versionNumber,
                document,
                LessonVersionSource.CMS,
                changeNote,
                publishedByUserId);
    }

    @PrePersist
    void onCreate() {
        if (publishedAt == null) {
            publishedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public JsonNode getDocument() {
        return document;
    }

    public LessonVersionSource getSource() {
        return source;
    }

    public String getChangeNote() {
        return changeNote;
    }

    public Long getPublishedByUserId() {
        return publishedByUserId;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }
}