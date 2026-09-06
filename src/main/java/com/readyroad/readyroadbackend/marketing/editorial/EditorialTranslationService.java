package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.content.ContentLocale;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class EditorialTranslationService {

    static final String AGENT_TYPE = "EDITORIAL";
    static final String TASK_TYPE = "ARTICLE_TRANSLATION_ADAPT";
    static final String AUDIT_EVENT = "EDITORIAL_ARTICLE_TRANSLATIONS_CREATED";

    private final EditorialTranslationStore store;
    private final EditorialTranslationClient translationClient;
    private final EditorialTranslationQualityPolicy qualityPolicy;
    private final EditorialArticleVersionService versionService;
    private final EditorialArticleWorkflowService workflowService;
    private final TaskCreationService taskCreationService;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;
    private final PlatformTransactionManager transactionManager;

    public MarketingTaskLifecycleResponse request(
            long articleId,
            EditorialTranslationDtos.CreateRequest request,
            String actor) {

        validateRequest(articleId, request, actor);

        EditorialTranslationStore.TranslationContext context = store.context(articleId);

        if (!context.state().allowsDraftPreparation()) {
            throw new IllegalStateException(
                    "Article must be in an editable draft or review state before translation adaptation");
        }
        ContentLocale sourceLocale = ContentLocale.valueOf(context.canonicalLanguage());
        if (targetsRequiringAdaptation(articleId, sourceLocale).isEmpty()) {
            throw new IllegalStateException(
                    "No localized article version or focus keyword requires adaptation");
        }

        ObjectNode payload = objectMapper.createObjectNode()
                .put("articleId", articleId)
                .put("sourceVersionId", context.sourceVersionId())
                .put("canonicalLanguage", context.canonicalLanguage())
                .put("idempotencyKey", request.idempotencyKey().trim());

        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                AGENT_TYPE,
                TASK_TYPE,
                payload,
                TaskPriority.HIGH,
                null,
                actor.trim(),
                "article-translation:" + articleId + ":" + request.idempotencyKey().trim(),
                null,
                null,
                "ARTICLE",
                String.valueOf(articleId),
                ApprovalMetadata.standingOwnerAuthorization()));

        return MarketingTaskLifecycleResponse.from(result.task());
    }

    public void create(ClaimedTask task) {
        Preparation preparation = prepare(task);

        if (preparation.completed()) {
            return;
        }

        for (ContentLocale target : preparation.targets()) {
            // Recheck before spending: another completed task may already have supplied this language.
            if (!targetsRequiringAdaptation(preparation.context().articleId(),
                    ContentLocale.valueOf(preparation.context().canonicalLanguage())).contains(target)) {
                continue;
            }
            var request = adaptRequest(preparation.context(), target);
            var existing = versionService.current(preparation.context().articleId(), target.name());
            var validated = existing.isPresent()
                    ? qualityPolicy.validateKeyword(request, translationClient.adaptKeyword(request))
                    : qualityPolicy.validate(request, translationClient.adapt(request));
            new TransactionTemplate(transactionManager).executeWithoutResult(
                    status -> persist(task, preparation, Map.of(target, validated)));
        }

        new TransactionTemplate(transactionManager).executeWithoutResult(
                status -> persist(task, preparation, Map.of()));
    }

    Preparation prepare(ClaimedTask task) {
        long articleId = articleId(task);

        EditorialTranslationStore.TranslationContext context =
                store.context(articleId);

        ContentLocale sourceLocale = ContentLocale.valueOf(context.canonicalLanguage());
        List<ContentLocale> targets = targetsRequiringAdaptation(articleId, sourceLocale);
        if (!context.state().allowsDraftPreparation()) {
            throw new IllegalStateException(
                    "Article is not eligible for translation in state "
                            + context.state());
        }

        validateSourceSnapshot(task, context);

        if (targets.isEmpty() && context.state() != EditorialArticleState.TRANSLATION_REQUIRED) {
            return new Preparation(context, List.of(), true);
        }

        required(
                context.focusKeyword(),
                "Canonical article focusKeyword is required before translation");

        required(
                EditorialArticleMetadata.metaTitle(context.metadata()),
                "Canonical article metaTitle is required before translation");

        required(
                EditorialArticleMetadata.metaDescription(context.metadata()),
                "Canonical article metaDescription is required before translation");

        required(
                text(context.metadata(), "primaryCta"),
                "Canonical article primary CTA is required before translation");

        required(
                context.title(),
                "Canonical article title is required before translation");

        required(
                context.body(),
                "Canonical article body is required before translation");

        return new Preparation(context, targets, false);
    }

    void persist(
            ClaimedTask task,
            Preparation preparation,
            Map<ContentLocale, EditorialTranslationQualityPolicy.ValidatedTranslation> generated) {

        EditorialTranslationStore.TranslationContext current =
                store.lockContext(preparation.context().articleId());

        ContentLocale sourceLocale = ContentLocale.valueOf(current.canonicalLanguage());
        if (!current.state().allowsDraftPreparation()) {
            throw new IllegalStateException(
                    "Article is no longer eligible for translation adaptation");
        }

        if (current.sourceVersionId()
                != preparation.context().sourceVersionId()) {
            throw new IllegalStateException(
                    "Canonical article version changed during translation; request translation again");
        }

        if (current.state() != EditorialArticleState.TRANSLATION_REQUIRED
                && targetsRequiringAdaptation(current.articleId(), sourceLocale).isEmpty()) {
            return;
        }

        var typography =
                EditorialArticleMetadata.typography(current.metadata());

        for (Map.Entry<ContentLocale,
                EditorialTranslationQualityPolicy.ValidatedTranslation> entry
                : generated.entrySet()) {

            ContentLocale target = entry.getKey();

            EditorialTranslationQualityPolicy.ValidatedTranslation translation =
                    entry.getValue();

            var existing = versionService.current(current.articleId(), target.name());
            if (existing.isPresent()
                    && EditorialArticleMetadata.focusKeyword(existing.get().metadata()) != null) {
                continue;
            }

            if (existing.isPresent()) {
                var version = existing.get();
                ObjectNode repairedMetadata = EditorialArticleMetadata.withFocusKeyword(
                        version.metadata(), translation.focusKeyword());
                ObjectNode repairedGeneration = version.generationMetadata() != null
                                && version.generationMetadata().isObject()
                        ? (ObjectNode) version.generationMetadata().deepCopy()
                        : objectMapper.createObjectNode();
                repairedGeneration
                        .put("seoMetadataRepairTaskId", task.taskId())
                        .put("seoMetadataRepairModel", translation.model())
                        .put("seoMetadataRepairInputTokens", translation.inputTokens())
                        .put("seoMetadataRepairOutputTokens", translation.outputTokens())
                        .put("seoMetadataRepairOutcome", translation.requestOutcome());
                versionService.append(
                        new EditorialArticleVersionDtos.AppendRequest(
                                current.articleId(), target.name(), version.title(),
                                EditorialArticleMetadata.slugFromFocusKeyword(translation.focusKeyword()),
                                version.summary(), version.body(), repairedMetadata,
                                repairedGeneration, version.status()),
                        "EDITORIAL_WORKER");
                continue;
            }

            if (translation.body() == null) {
                throw new IllegalStateException("Localized version changed during keyword repair; reload before retrying");
            }
            ObjectNode metadata =
                    EditorialArticleMetadata.withSeoMetadata(
                            objectMapper.createObjectNode(),
                            translation.metaTitle(),
                            translation.metaDescription());

            metadata = EditorialArticleMetadata.withFocusKeyword(
                    metadata,
                    translation.focusKeyword());

            metadata =
                    EditorialArticleMetadata.withInternalLinks(
                            metadata,
                            List.of());

            metadata =
                    EditorialArticleMetadata.withTypography(
                            metadata,
                            typography);

            metadata.put(
                    "primaryCta",
                    translation.cta());

            ObjectNode generation =
                    objectMapper.createObjectNode()
                            .put("provider", "OPENAI")
                            .put("model", translation.model())
                            .put("inputTokens", translation.inputTokens())
                            .put("outputTokens", translation.outputTokens())
                            .put("requestOutcome", translation.requestOutcome())
                            .put("translationTaskId", task.taskId())
                            .put("sourceVersionId", current.sourceVersionId())
                            .put("sourceVersionNumber", current.sourceVersionNumber())
                            .put("sourceLanguage", current.canonicalLanguage())
                            .put("targetLanguage", target.name())
                            .put(
                                    "adaptationMode",
                                    "CANONICAL_TRANSLATION_ADAPT");

            String sourceReference =
                    text(
                            current.generationMetadata(),
                            "sourceReference");

            if (sourceReference != null) {
                generation.put(
                        "sourceReference",
                        sourceReference);
            }

            versionService.append(
                    new EditorialArticleVersionDtos.AppendRequest(
                            current.articleId(),
                            target.name(),
                            translation.title(),
                            EditorialArticleMetadata.slugFromFocusKeyword(translation.focusKeyword()),
                            translation.summary(),
                            translation.body(),
                            metadata,
                            generation,
                            "DRAFT"),
                    "EDITORIAL_WORKER");

        }

        if (!targetsRequiringAdaptation(current.articleId(), sourceLocale).isEmpty()) {
            return;
        }

        if (current.state() == EditorialArticleState.TRANSLATION_REQUIRED) {
            workflowService.transition(
                    new EditorialArticleWorkflowDtos.TransitionRequest(
                            current.articleId(),
                            EditorialArticleState.IMAGE_REQUIRED,
                            task.taskId(),
                            task.correlationId(),
                            "EDITORIAL_WORKER",
                            "Canonical article adapted into all required localized versions",
                            Set.of()));
        }

        ObjectNode details =
                objectMapper.createObjectNode()
                        .put("articleId", current.articleId())
                        .put("sourceVersionId", current.sourceVersionId())
                        .put("sourceLanguage", current.canonicalLanguage())
                        .put("translationTaskId", task.taskId());

        var languages =
                details.putArray("currentLanguages");

        store.currentLanguages(current.articleId()).stream()
                .sorted()
                .forEach(languages::add);

        auditService.recordEntityEvent(
                AUDIT_EVENT,
                "EDITORIAL_WORKER",
                "EDITORIAL_ARTICLE",
                String.valueOf(current.articleId()),
                task.taskId(),
                task.correlationId(),
                details);
    }

    private List<ContentLocale> targetsRequiringAdaptation(
            long articleId,
            ContentLocale sourceLocale) {
        return ContentLocale.SUPPORTED.stream()
                .filter(locale -> locale != sourceLocale)
                .filter(locale -> versionService.current(articleId, locale.name())
                        .map(version -> EditorialArticleMetadata.focusKeyword(version.metadata()) == null)
                        .orElse(true))
                .toList();
    }

    private static EditorialTranslationClient.AdaptRequest adaptRequest(
            EditorialTranslationStore.TranslationContext context,
            ContentLocale target) {

        String focusKeyword = required(
                context.focusKeyword(),
                "Canonical article focusKeyword is required before translation");

        String metaTitle =
                required(
                        EditorialArticleMetadata.metaTitle(context.metadata()),
                        "Canonical article metaTitle is required before translation");

        String metaDescription =
                required(
                        EditorialArticleMetadata.metaDescription(context.metadata()),
                        "Canonical article metaDescription is required before translation");

        String primaryCta =
                required(
                        text(context.metadata(), "primaryCta"),
                        "Canonical article primary CTA is required before translation");

        return new EditorialTranslationClient.AdaptRequest(
                context.articleId(),
                context.sourceVersionId(),
                ContentLocale.valueOf(context.canonicalLanguage()),
                target,
                context.title(),
                context.slug(),
                context.summary(),
                context.body(),
                focusKeyword,
                metaTitle,
                metaDescription,
                primaryCta);
    }

    private static void validateSourceSnapshot(
            ClaimedTask task,
            EditorialTranslationStore.TranslationContext context) {

        long expectedSourceVersionId =
                task.payload().path("sourceVersionId").asLong(0);

        String expectedCanonicalLanguage =
                task.payload()
                        .path("canonicalLanguage")
                        .asText("")
                        .trim();

        if (expectedSourceVersionId <= 0
                || expectedCanonicalLanguage.isBlank()) {
            throw new IllegalArgumentException(
                    "Translation task canonical source snapshot is incomplete");
        }

        if (expectedSourceVersionId != context.sourceVersionId()
                || !expectedCanonicalLanguage.equals(context.canonicalLanguage())) {
            throw new IllegalStateException(
                    "Translation task canonical source snapshot is stale; request translation again");
        }
    }

    private static long articleId(ClaimedTask task) {
        if (task == null || task.payload() == null) {
            throw new IllegalArgumentException(
                    "Translation task payload is required");
        }

        long articleId =
                task.payload()
                        .path("articleId")
                        .asLong(0);

        if (articleId <= 0) {
            throw new IllegalArgumentException(
                    "Editorial translation task has no valid articleId");
        }

        return articleId;
    }

    private static void validateRequest(
            long articleId,
            EditorialTranslationDtos.CreateRequest request,
            String actor) {

        if (articleId <= 0) {
            throw new IllegalArgumentException(
                    "articleId must be positive");
        }

        if (request == null
                || request.idempotencyKey() == null
                || request.idempotencyKey().isBlank()) {
            throw new IllegalArgumentException(
                    "Translation idempotencyKey is required");
        }

        if (actor == null
                || actor.isBlank()
                || actor.trim().length() > 160) {
            throw new IllegalArgumentException(
                    "A valid actor is required");
        }
    }

    private static String required(
            String value,
            String message) {

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(message);
        }

        return value.trim();
    }

    private static String text(
            JsonNode node,
            String field) {

        if (node == null || !node.isObject()) {
            return null;
        }

        String value =
                node.path(field)
                        .asText("")
                        .trim();

        return value.isEmpty()
                ? null
                : value;
    }

    record Preparation(
            EditorialTranslationStore.TranslationContext context,
            List<ContentLocale> targets,
            boolean completed) {}
}
