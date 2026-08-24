package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.content.ContentGenerationClient;
import com.readyroad.readyroadbackend.marketing.content.ContentLocale;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EditorialDraftService {

    static final String AGENT_TYPE = "EDITORIAL";
    static final String TASK_TYPE = "ARTICLE_DRAFT_CREATE";

    private final EditorialDraftStore store;
    private final EditorialDraftPersistenceService persistenceService;
    private final EditorialDraftQualityPolicy qualityPolicy;
    private final ContentGenerationClient generationClient;
    private final TaskCreationService taskCreationService;
    private final ObjectMapper objectMapper;

    public MarketingTaskLifecycleResponse request(
            long articleId,
            EditorialDraftDtos.CreateRequest request,
            String actor) {
        validateRequest(articleId, request, actor);
        store.requireArticle(articleId);
        var payload = objectMapper.createObjectNode()
                .put("articleId", articleId)
                .put("idempotencyKey", request.idempotencyKey().trim());
        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                AGENT_TYPE,
                TASK_TYPE,
                payload,
                TaskPriority.HIGH,
                null,
                actor.trim(),
                "article-draft:" + articleId + ":" + request.idempotencyKey().trim(),
                null,
                null,
                "ARTICLE",
                String.valueOf(articleId),
                ApprovalMetadata.standingOwnerAuthorization()));
        return MarketingTaskLifecycleResponse.from(result.task());
    }

    public void create(ClaimedTask task) {
        var preparation = persistenceService.prepare(task);
        if (preparation.completed()) {
            return;
        }
        ContentLocale locale = ContentLocale.valueOf(preparation.context().language());
        var generated = generationClient.generate(new ContentGenerationClient.GenerationRequest(
                locale,
                preparation.source(),
                preparation.source().factsFor(locale),
                preparation.strategy()));
        var validated = qualityPolicy.validate(
                locale, preparation.source(), generated, preparation.context().pillar());
        persistenceService.persist(task, preparation, validated);
    }

    private static void validateRequest(
            long articleId,
            EditorialDraftDtos.CreateRequest request,
            String actor) {
        if (articleId <= 0) {
            throw new IllegalArgumentException("articleId must be positive");
        }
        if (request == null || request.idempotencyKey() == null || request.idempotencyKey().isBlank()) {
            throw new IllegalArgumentException("Draft idempotencyKey is required");
        }
        if (actor == null || actor.isBlank() || actor.trim().length() > 160) {
            throw new IllegalArgumentException("A valid actor is required");
        }
    }
}
