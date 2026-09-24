package com.readyroad.readyroadbackend.domain.entity;

import com.readyroad.readyroadbackend.domain.enums.LessonMediaStatus;
import com.readyroad.readyroadbackend.domain.enums.LessonMediaStorageProvider;
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

import java.time.Instant;

@Entity
@Table(name = "lesson_media_assets")
public class LessonMediaAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false, updatable = false)
    private Lesson lesson;

    @Column(name = "storage_key", nullable = false, unique = true, length = 512, updatable = false)
    private String storageKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_provider", nullable = false, length = 32, updatable = false)
    private LessonMediaStorageProvider storageProvider;

    @Column(name = "original_filename", nullable = false, updatable = false)
    private String originalFilename;

    @Column(name = "mime_type", nullable = false, length = 100, updatable = false)
    private String mimeType;

    @Column(name = "size_bytes", nullable = false, updatable = false)
    private long sizeBytes;

    @Column(updatable = false)
    private Integer width;

    @Column(updatable = false)
    private Integer height;

    @Column(length = 64, updatable = false)
    private String sha256;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private LessonMediaStatus status;

    @Column(name = "uploaded_by_user_id", updatable = false)
    private Long uploadedByUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "archived_at")
    private Instant archivedAt;

    protected LessonMediaAsset() {
    }

    public LessonMediaAsset(
            Lesson lesson,
            String storageKey,
            LessonMediaStorageProvider storageProvider,
            String originalFilename,
            String mimeType,
            long sizeBytes,
            Integer width,
            Integer height,
            String sha256,
            Long uploadedByUserId) {

        if (lesson == null || lesson.getId() == null) {
            throw new IllegalArgumentException("Persisted lesson is required");
        }

        if (storageKey == null || storageKey.isBlank()) {
            throw new IllegalArgumentException("storageKey is required");
        }

        if (storageProvider == null) {
            throw new IllegalArgumentException("storageProvider is required");
        }

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("originalFilename is required");
        }

        if (mimeType == null || !mimeType.startsWith("image/")) {
            throw new IllegalArgumentException("Image MIME type is required");
        }

        if (sizeBytes <= 0) {
            throw new IllegalArgumentException("sizeBytes must be positive");
        }

        if (width != null && width <= 0) {
            throw new IllegalArgumentException("width must be positive");
        }

        if (height != null && height <= 0) {
            throw new IllegalArgumentException("height must be positive");
        }

        if (sha256 != null && !sha256.matches("^[0-9A-Fa-f]{64}$")) {
            throw new IllegalArgumentException("Invalid SHA-256 digest");
        }

        this.lesson = lesson;
        this.storageKey = storageKey;
        this.storageProvider = storageProvider;
        this.originalFilename = originalFilename;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.width = width;
        this.height = height;
        this.sha256 = sha256;
        this.status = LessonMediaStatus.ACTIVE;
        this.uploadedByUserId = uploadedByUserId;
    }

    public void archive() {
        if (status == LessonMediaStatus.ARCHIVED) {
            return;
        }

        status = LessonMediaStatus.ARCHIVED;
        archivedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }

        if (status == null) {
            status = LessonMediaStatus.ACTIVE;
        }
    }

    public Long getId() {
        return id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public LessonMediaStorageProvider getStorageProvider() {
        return storageProvider;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public String getSha256() {
        return sha256;
    }

    public LessonMediaStatus getStatus() {
        return status;
    }

    public Long getUploadedByUserId() {
        return uploadedByUserId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getArchivedAt() {
        return archivedAt;
    }
}