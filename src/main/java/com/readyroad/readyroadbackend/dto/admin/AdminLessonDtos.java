package com.readyroad.readyroadbackend.dto.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class AdminLessonDtos {

    private AdminLessonDtos() {
    }

    public record LessonSummary(
            Long id,
            String lessonCode,
            String titleAr,
            String titleNl,
            String titleFr,
            String titleEn,
            boolean active,
            String editorState,
            int displayOrder,
            int estimatedMinutes,
            int pageCount,
            int currentVersion,
            boolean hasDraft,
            Long draftRevision,
            int categoryCount,
            long mediaCount,
            LocalDateTime updatedAt) {
    }

    public record DraftResponse(
            Long lessonId,
            int baseVersionNumber,
            long revision,
            Map<String, Object> document,
            Long createdByUserId,
            Long updatedByUserId,
            Instant createdAt,
            Instant updatedAt) {
    }

    public record VersionSummary(
            Long id,
            int versionNumber,
            String source,
            String changeNote,
            Long publishedByUserId,
            Instant publishedAt) {
    }

    public record CategoryLinkResponse(
            Long categoryId,
            String code,
            String nameAr,
            String nameNl,
            String nameFr,
            String nameEn,
            boolean primary,
            int displayOrder) {
    }

    public record MediaAssetResponse(
            Long id,
            String storageKey,
            String storageProvider,
            String originalFilename,
            String mimeType,
            long sizeBytes,
            Integer width,
            Integer height,
            String sha256,
            String status,
            Long uploadedByUserId,
            Instant createdAt,
            Instant archivedAt) {
    }

    public record TheoryCategoryResponse(
            Long id,
            String code,
            String nameAr,
            String nameNl,
            String nameFr,
            String nameEn,
            int displayOrder) {
    }

    public record LessonDetail(
            Long id,
            String lessonCode,
            String titleAr,
            String titleNl,
            String titleFr,
            String titleEn,
            boolean active,
            int displayOrder,
            int estimatedMinutes,
            int pageCount,
            int currentVersion,
            Map<String, Object> publishedDocument,
            DraftResponse draft,
            List<CategoryLinkResponse> categoryLinks,
            List<MediaAssetResponse> mediaAssets,
            List<VersionSummary> versions) {
    }

    public record SaveDraftRequest(
            @NotNull Long expectedRevision,
            @NotNull Map<String, Object> document) {
    }

    public record CreateLessonRequest(
            @NotBlank @Size(max = 50)
            @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9_-]*$")
            String lessonCode,
            @NotBlank String titleAr,
            @NotBlank String titleNl,
            @NotBlank String titleFr,
            @NotBlank String titleEn,
            String descriptionAr,
            String descriptionNl,
            String descriptionFr,
            String descriptionEn,
            @Size(max = 10) String icon,
            @NotNull @Min(0) Integer displayOrder,
            @NotNull @Min(1) Integer estimatedMinutes,
            @NotNull @Valid CreateLessonPageRequest page) {
    }

    public record CreateLessonPageRequest(
            @NotBlank String titleAr,
            @NotBlank String titleNl,
            @NotBlank String titleFr,
            @NotBlank String titleEn,
            String contentAr,
            String contentNl,
            String contentFr,
            String contentEn) {
    }

    public record PublishRequest(
            @NotNull Long expectedRevision,
            String changeNote) {
    }
}
