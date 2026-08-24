package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextService;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditorialBriefService {

    static final String AGENT_TYPE = "EDITORIAL";
    static final String TASK_TYPE = "ARTICLE_BRIEF_CREATE";
    private static final Set<String> LANGUAGES = Set.of("AR", "NL", "FR", "EN");

    private final TaskCreationService taskCreationService;
    private final MarketingStrategyContextService strategyContextService;
    private final EditorialBriefStore store;
    private final EditorialArticleWorkflowService workflowService;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;

    public MarketingTaskLifecycleResponse request(
            long topicId,
            EditorialBriefDtos.CreateRequest request,
            String actor) {
        validateRequest(topicId, request, actor);
        String language = language(request.targetLanguage());
        ObjectNode payload = objectMapper.valueToTree(request);
        payload.put("articleTopicId", topicId);
        payload.put("targetLanguage", language);
        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                AGENT_TYPE,
                TASK_TYPE,
                payload,
                TaskPriority.HIGH,
                null,
                actor.trim(),
                "article-brief:" + topicId + ":" + request.idempotencyKey().trim(),
                null,
                null,
                "ARTICLE_TOPIC",
                String.valueOf(topicId),
                ApprovalMetadata.standingOwnerAuthorization()));
        return MarketingTaskLifecycleResponse.from(result.task());
    }

    @Transactional
    public void create(ClaimedTask task, EditorialBriefDtos.CreateRequest request) {
        if (store.createdByTask(task.taskId())) {
            return;
        }
        long topicId = task.payload().path("articleTopicId").asLong(0);
        if (topicId <= 0) {
            throw new IllegalArgumentException("Editorial brief task has no valid articleTopicId");
        }
        String language = language(request.targetLanguage());
        var strategy = strategyContextService.resolve(request.strategyContext());
        var topic = store.lockTopic(topicId);
        if (!Set.of("PLANNED", "BRIEF_READY").contains(topic.status())) {
            throw new IllegalStateException("Article topic is not eligible for brief creation");
        }
        store.requireNoApprovedBrief(topicId, language);
        long articleId = store.createOrBindArticle(topic, language, request.strategyContext());
        long briefId = store.insertApprovedBrief(
                topicId, task.taskId(), language, request, strategy.conversionGoal().primaryCta());
        workflowService.transition(new EditorialArticleWorkflowDtos.TransitionRequest(
                articleId,
                EditorialArticleState.BRIEF_READY,
                task.taskId(),
                task.correlationId(),
                "EDITORIAL_WORKER",
                "Owner-authorized article brief created from resolved Strategy Context",
                Set.of()));
        store.bindTopic(topicId, language, request);
        auditService.recordEntityEvent(
                "EDITORIAL_ARTICLE_BRIEF_CREATED",
                "EDITORIAL_WORKER",
                "ARTICLE_BRIEF",
                String.valueOf(briefId),
                task.taskId(),
                task.correlationId(),
                objectMapper.createObjectNode()
                        .put("articleId", articleId)
                        .put("articleTopicId", topicId)
                        .put("targetLanguage", language)
                        .put("strategyContextResolved", true));
    }

    private static void validateRequest(
            long topicId,
            EditorialBriefDtos.CreateRequest request,
            String actor) {
        if (topicId <= 0) {
            throw new IllegalArgumentException("topicId must be positive");
        }
        if (request == null || request.strategyContext() == null) {
            throw new IllegalArgumentException("Editorial brief request and Strategy Context are required");
        }
        language(request.targetLanguage());
        if (actor == null || actor.isBlank() || actor.trim().length() > 160) {
            throw new IllegalArgumentException("A valid actor is required");
        }
    }

    private static String language(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        if (!LANGUAGES.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported article language: " + value);
        }
        return normalized;
    }
}
