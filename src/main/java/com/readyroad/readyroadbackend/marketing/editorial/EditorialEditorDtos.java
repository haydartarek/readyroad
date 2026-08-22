package com.readyroad.readyroadbackend.marketing.editorial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public final class EditorialEditorDtos {

    private EditorialEditorDtos() {}

    public record Workspace(
            List<String> languages,
            List<String> qualityGates,
            List<Topic> topics) {}

    public record Topic(
            long topicId,
            String topicKey,
            int order,
            String sourceType,
            String title,
            String titleLanguage,
            String primaryLanguage,
            String priority,
            boolean strategyContextResolved,
            Long articleId,
            String lifecycleState,
            String canonicalLanguage,
            List<CurrentVersion> currentVersions) {}

    public record CurrentVersion(
            String language,
            int versionNumber,
            String title,
            String slug,
            String status,
            Instant createdAt,
            String createdBy) {}

    public record SaveRequest(
            @NotBlank @Size(max = 500) String title,
            @Size(max = 255) String slug,
            @Size(max = 2000) String summary,
            @NotBlank @Size(max = 500_000) String body,
            @NotBlank @Size(max = 500) String metaTitle,
            @NotBlank @Size(max = 2000) String metaDescription,
            @PositiveOrZero Integer expectedCurrentVersion) {}

    public record SaveResult(
            long topicId,
            long articleId,
            String lifecycleState,
            boolean articleCreated,
            boolean created,
            Version version) {}

    public record Version(
            long id,
            long articleId,
            int versionNumber,
            String language,
            String title,
            String slug,
            String summary,
            String body,
            String metaTitle,
            String metaDescription,
            String status,
            boolean current,
            Instant createdAt,
            String createdBy) {}
}
