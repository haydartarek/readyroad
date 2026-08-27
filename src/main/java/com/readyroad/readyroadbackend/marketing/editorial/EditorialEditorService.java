package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EditorialEditorService {

    static final String AUDIT_EVENT = "EDITORIAL_ARTICLE_DRAFT_SAVED";
    private static final List<String> LANGUAGES = List.of("AR", "NL", "FR", "EN");
    private static final Set<String> LANGUAGE_SET = Set.copyOf(LANGUAGES);
    private static final Set<String> SIZE_TOKENS = Set.of("COMPACT", "DEFAULT", "LARGE");
    private static final Set<String> COLOR_TOKENS =
            Set.of("DEFAULT", "MUTED", "PRIMARY", "SECONDARY");

    private final EditorialEditorStore store;
    private final EditorialArticleVersionStore versionStore;
    private final EditorialArticleVersionService versionService;
    private final EditorialInternalLinkPolicy internalLinkPolicy;
    private final EditorialContentGraphService contentGraphService;
    private final EditorialArticleImageService articleImageService;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public EditorialEditorDtos.Workspace workspace() {
        Map<Long, List<EditorialEditorStore.CurrentVersionRow>> versions = store.currentVersions().stream()
                .collect(Collectors.groupingBy(EditorialEditorStore.CurrentVersionRow::articleId));
        List<EditorialEditorDtos.Topic> topics = store.topics().stream()
                .map(topic -> new EditorialEditorDtos.Topic(
                        topic.id(), topic.topicKey(), topic.order(), topic.sourceType(), topic.title(),
                        topic.titleLanguage(), topic.primaryLanguage(), topic.priority(),
                        topic.strategyContextResolved(), topic.uspId(), topic.icpId(),
                        topic.contentPillarId(), topic.funnelStageId(), topic.conversionGoalId(),
                        topic.articleId(), topic.lifecycleState(),
                        topic.canonicalLanguage(),
                        topic.articleId() == null
                                ? null
                                : articleImageService.current(topic.articleId()).orElse(null),
                        currentVersions(topic.articleId(), versions)))
                .toList();
        return new EditorialEditorDtos.Workspace(
                LANGUAGES,
                Arrays.stream(EditorialArticleQualityGate.values()).map(Enum::name).toList(),
                contentGraphService.graph(),
                topics);
    }

    @Transactional(readOnly = true)
    public EditorialEditorDtos.AuthoringStatus authoringStatus(long topicId) {
        if (topicId <= 0) {
            throw new IllegalArgumentException("topicId must be positive");
        }
        var status = store.authoringStatus(topicId);
        String briefTaskStatus = store.latestTaskStatus(
                EditorialBriefService.TASK_TYPE, "ARTICLE_TOPIC", String.valueOf(topicId));
        String sourceTaskStatus = store.latestTaskStatus(
                EditorialSourceCollectionService.TASK_TYPE, "ARTICLE_TOPIC", String.valueOf(topicId));
        String draftTaskStatus = status.articleId() == null
                ? null
                : store.latestTaskStatus(
                        EditorialDraftService.TASK_TYPE, "ARTICLE", String.valueOf(status.articleId()));
        boolean briefTaskActive = activeTask(briefTaskStatus);
        boolean sourceTaskActive = activeTask(sourceTaskStatus);
        boolean draftTaskActive = activeTask(draftTaskStatus);
        boolean briefReady = status.briefId() != null && "BRIEF_READY".equals(status.lifecycleState());
        return new EditorialEditorDtos.AuthoringStatus(
                status.topicId(), status.topicStatus(), status.articleId(), status.lifecycleState(),
                status.briefId(), status.briefStatus(), status.briefLanguage(),
                status.briefId() == null ? null : "ARTICLE_BRIEF:" + status.briefId(),
                status.claimsTotal(), status.claimsSupported(), status.claimsRequiringReview(),
                status.claimsMissing(), briefTaskStatus, sourceTaskStatus, draftTaskStatus,
                status.briefId() == null
                        && Set.of("PLANNED", "BRIEF_READY").contains(status.topicStatus())
                        && !briefTaskActive,
                briefReady && !sourceTaskActive,
                briefReady
                        && status.claimsTotal() > 0
                        && status.claimsSupported() == status.claimsTotal()
                        && !draftTaskActive);
    }

    @Transactional(readOnly = true)
    public List<EditorialEditorDtos.Version> versions(long articleId, String language) {
        return versionService.history(articleId, language(language)).stream()
                .map(this::version)
                .toList();
    }

    @Transactional
    public EditorialEditorDtos.SaveResult save(
            long topicId,
            String language,
            EditorialEditorDtos.SaveRequest request,
            String actor) {
        validate(topicId, request, actor);
        String normalizedLanguage = language(language);
        var topic = store.lockTopic(topicId);
        boolean articleCreated = store.topics().stream()
                .filter(candidate -> candidate.id() == topicId)
                .findFirst()
                .map(candidate -> candidate.articleId() == null)
                .orElseThrow(() -> new IllegalArgumentException("Unknown article topic: " + topicId));
        if (articleCreated && !topic.strategyContextResolved()) {
            throw blockedStrategyContext();
        }
        var article = store.findOrCreateArticle(topic);
        ensureEditable(article);
        if (!topic.strategyContextResolved()) {
            throw blockedStrategyContext();
        }
        versionStore.lockArticle(article.id());
        var current = versionStore.current(article.id(), normalizedLanguage);
        var normalized = normalizedRequest(request);
        var internalLinks = internalLinkPolicy.normalize(
                article.id(), normalizedLanguage, normalized.internalLinks());

        if (current.isPresent() && sameContent(current.get(), normalized, internalLinks)) {
            return result(topicId, article, articleCreated, false, current.get());
        }
        int currentVersion = current.map(EditorialArticleVersionDtos.Version::versionNumber).orElse(0);
        Integer expected = request.expectedCurrentVersion();
        if ((currentVersion == 0 && expected != null && expected != 0)
                || (currentVersion > 0 && !Objects.equals(expected, currentVersion))) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Article version changed; reload the editor before saving");
        }

        var metadata = EditorialArticleMetadata.withSeoMetadata(
                current.map(EditorialArticleVersionDtos.Version::metadata)
                        .orElseGet(objectMapper::createObjectNode),
                normalized.metaTitle(),
                normalized.metaDescription());
        metadata = EditorialArticleMetadata.withInternalLinks(metadata, internalLinks);
        metadata = EditorialArticleMetadata.withTypography(metadata, normalized.typography());
        var generationMetadata = current.map(EditorialArticleVersionDtos.Version::generationMetadata)
                .orElseGet(objectMapper::createObjectNode);
        var appended = versionService.append(new EditorialArticleVersionDtos.AppendRequest(
                article.id(), normalizedLanguage, normalized.title(), normalized.slug(),
                normalized.summary(), normalized.body(), metadata, generationMetadata, "DRAFT"), actor);

        ObjectNode details = objectMapper.createObjectNode();
        details.put("topicId", topicId);
        details.put("language", normalizedLanguage);
        details.put("versionNumber", appended.versionNumber());
        details.put("articleCreated", articleCreated);
        details.put("previousVersionNumber", currentVersion);
        auditService.recordEntityEvent(
                AUDIT_EVENT,
                actor.trim(),
                "EDITORIAL_ARTICLE",
                String.valueOf(article.id()),
                null,
                "editor-save-" + appended.id(),
                details);
        return result(topicId, article, articleCreated, true, appended);
    }

    private List<EditorialEditorDtos.CurrentVersion> currentVersions(
            Long articleId,
            Map<Long, List<EditorialEditorStore.CurrentVersionRow>> versions) {
        if (articleId == null) {
            return List.of();
        }
        return versions.getOrDefault(articleId, List.of()).stream()
                .map(value -> new EditorialEditorDtos.CurrentVersion(
                        value.language(), value.versionNumber(), value.title(), value.slug(),
                        value.status(), value.createdAt(), value.createdBy()))
                .toList();
    }

    private EditorialEditorDtos.SaveResult result(
            long topicId,
            EditorialEditorStore.ArticleRow article,
            boolean articleCreated,
            boolean created,
            EditorialArticleVersionDtos.Version version) {
        return new EditorialEditorDtos.SaveResult(
                topicId, article.id(), article.lifecycleState(), articleCreated, created, version(version));
    }

    private EditorialEditorDtos.Version version(EditorialArticleVersionDtos.Version value) {
        return new EditorialEditorDtos.Version(
                value.id(), value.articleId(), value.versionNumber(), value.language(), value.title(),
                value.slug(), value.summary(), value.body(),
                EditorialArticleMetadata.metaTitle(value.metadata()),
                EditorialArticleMetadata.metaDescription(value.metadata()),
                EditorialArticleMetadata.internalLinks(value.metadata()),
                EditorialArticleMetadata.typography(value.metadata()),
                value.status(), value.current(),
                value.createdAt(), value.createdBy());
    }

    private EditorialEditorDtos.SaveRequest normalizedRequest(EditorialEditorDtos.SaveRequest request) {
        return new EditorialEditorDtos.SaveRequest(
                request.title().trim(), blankToNull(request.slug()), blankToNull(request.summary()),
                request.body(), request.metaTitle().trim(), request.metaDescription().trim(),
                request.internalLinks() == null ? List.of() : List.copyOf(request.internalLinks()),
                normalizedTypography(request.typography()),
                request.expectedCurrentVersion());
    }

    private boolean sameContent(
            EditorialArticleVersionDtos.Version current,
            EditorialEditorDtos.SaveRequest request,
            List<EditorialInternalLinkDtos.Link> internalLinks) {
        return current.status().equals("DRAFT")
                && current.title().equals(request.title())
                && Objects.equals(current.slug(), request.slug())
                && Objects.equals(current.summary(), request.summary())
                && current.body().equals(request.body())
                && Objects.equals(EditorialArticleMetadata.metaTitle(current.metadata()), request.metaTitle())
                && Objects.equals(
                        EditorialArticleMetadata.metaDescription(current.metadata()),
                        request.metaDescription())
                && EditorialArticleMetadata.internalLinks(current.metadata()).equals(internalLinks)
                && EditorialArticleMetadata.typography(current.metadata()).equals(request.typography());
    }

    private static EditorialEditorDtos.Typography normalizedTypography(
            EditorialEditorDtos.Typography value) {
        var typography = value == null ? EditorialEditorDtos.Typography.defaults() : value;
        requireToken(typography.h1Size(), SIZE_TOKENS, "h1Size");
        requireToken(typography.h2Size(), SIZE_TOKENS, "h2Size");
        requireToken(typography.h3Size(), SIZE_TOKENS, "h3Size");
        requireToken(typography.h4Size(), SIZE_TOKENS, "h4Size");
        requireToken(typography.paragraphSize(), SIZE_TOKENS, "paragraphSize");
        requireToken(typography.textColor(), COLOR_TOKENS, "textColor");
        return typography;
    }

    private static void requireToken(String value, Set<String> allowed, String field) {
        if (value == null || !allowed.contains(value)) {
            throw new IllegalArgumentException("Unsupported article typography value for " + field);
        }
    }

    private static String language(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        if (!LANGUAGE_SET.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported article language: " + value);
        }
        return normalized;
    }

    private static void validate(
            long topicId,
            EditorialEditorDtos.SaveRequest request,
            String actor) {
        if (topicId <= 0) {
            throw new IllegalArgumentException("topicId must be positive");
        }
        if (request == null || request.title() == null || request.title().isBlank()
                || request.body() == null || request.body().isBlank()
                || request.metaTitle() == null || request.metaTitle().isBlank()
                || request.metaDescription() == null || request.metaDescription().isBlank()) {
            throw new IllegalArgumentException("Article title, body and SEO metadata are required");
        }
        if (actor == null || actor.isBlank() || actor.trim().length() > 160) {
            throw new IllegalArgumentException("A valid editor actor is required");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static void ensureEditable(EditorialEditorStore.ArticleRow article) {
        if (Set.of("WAITING_APPROVAL", "APPROVED", "SCHEDULED", "PUBLISHED", "REJECTED", "ARCHIVED")
                .contains(article.lifecycleState())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Article content is locked in lifecycle state " + article.lifecycleState());
        }
    }

    private static ResponseStatusException blockedStrategyContext() {
        return new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "BLOCKED_STRATEGY_CONTEXT: resolve the required Strategy Context first");
    }

    private static boolean activeTask(String status) {
        return status != null
                && Set.of("PENDING", "SCHEDULED", "WAITING_APPROVAL", "APPROVED", "RUNNING", "RETRY_SCHEDULED")
                .contains(status);
    }
}
