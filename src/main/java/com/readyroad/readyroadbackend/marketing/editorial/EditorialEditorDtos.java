package com.readyroad.readyroadbackend.marketing.editorial;

import jakarta.validation.Valid;
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
            EditorialContentGraphDtos.Graph contentGraph,
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
            Long uspId,
            String icpId,
            Long contentPillarId,
            Long funnelStageId,
            Long conversionGoalId,
            Long articleId,
            String lifecycleState,
            String canonicalLanguage,
            EditorialArticleImageDtos.Asset image,
            List<CurrentVersion> currentVersions) {}

    public record AuthoringStatus(
            long topicId,
            String topicStatus,
            Long articleId,
            String lifecycleState,
            Long briefId,
            String briefStatus,
            String briefLanguage,
            String briefReference,
            int claimsTotal,
            int claimsSupported,
            int claimsRequiringReview,
            int claimsMissing,
            String latestBriefTaskStatus,
            String latestSourceTaskStatus,
            String latestDraftTaskStatus,
            boolean canCreateBrief,
            boolean canCollectSources,
            boolean canCreateDraft) {}

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
            @Valid List<EditorialInternalLinkDtos.Input> internalLinks,
            @PositiveOrZero Integer expectedCurrentVersion) {

        public SaveRequest(
                String title,
                String slug,
                String summary,
                String body,
                String metaTitle,
                String metaDescription,
                Integer expectedCurrentVersion) {
            this(title, slug, summary, body, metaTitle, metaDescription, List.of(), expectedCurrentVersion);
        }
    }

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
            List<EditorialInternalLinkDtos.Link> internalLinks,
            String status,
            boolean current,
            Instant createdAt,
            String createdBy) {}
}
