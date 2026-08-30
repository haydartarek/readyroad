package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EditorialArticleApprovalService {

    static final String TASK_TYPE = "ARTICLE_APPROVAL";
    static final String APPROVAL_SOURCE = "MASTER_SPEC_V3_PART_06_ARTICLE_APPROVAL";
    private static final String AGENT_TYPE = "EDITORIAL";
    private static final Set<String> REQUIRED_LANGUAGES = Set.of("AR", "NL", "FR", "EN");
    private static final Set<EditorialArticleQualityGate> REQUIRED_GATES =
            Set.copyOf(EnumSet.allOf(EditorialArticleQualityGate.class));

    private final EditorialArticleWorkflowStore workflowStore;
    private final EditorialArticleWorkflowService workflowService;
    private final EditorialArticleApprovalStore approvalStore;
    private final EditorialArticleImageStore imageStore;
    private final EditorialArticlePublicationService publicationService;
    private final TaskCreationService taskCreationService;
    private final ObjectMapper objectMapper;

    @Transactional
    public MarketingTaskLifecycleResponse request(
            long articleId,
            EditorialArticleApprovalDtos.Request request,
            String actor) {
        validateRequest(articleId, request, actor);
        var article = workflowStore.lock(articleId);
        if (article.state() != EditorialArticleState.IMAGE_REQUIRED
                && article.state() != EditorialArticleState.WAITING_APPROVAL) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Article must be IMAGE_REQUIRED before requesting approval");
        }

        Set<EditorialArticleQualityGate> gates = Set.copyOf(request.passedQualityGates());
        if (!gates.containsAll(REQUIRED_GATES)) {
            throw new IllegalStateException("All editorial quality gates must pass before approval");
        }
        List<EditorialArticleApprovalStore.VersionSnapshot> versions = currentVersions(articleId);
        EditorialArticleImageStore.ApprovedImage image = imageStore.requireApprovalReady(articleId);
        ObjectNode payload = payload(article, versions, image, gates, request.reason());
        String idempotencyKey = idempotencyKey(articleId, versions, image);
        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                AGENT_TYPE,
                TASK_TYPE,
                payload,
                TaskPriority.CRITICAL,
                null,
                actor.trim(),
                idempotencyKey,
                null,
                null,
                "ARTICLE",
                String.valueOf(articleId),
                ApprovalMetadata.humanApproval(APPROVAL_SOURCE)));
        workflowService.transition(new EditorialArticleWorkflowDtos.TransitionRequest(
                articleId,
                EditorialArticleState.WAITING_APPROVAL,
                result.task().getId(),
                result.task().getCorrelationId(),
                actor.trim(),
                request.reason().trim(),
                gates));
        return MarketingTaskLifecycleResponse.from(result.task());
    }

    @Transactional
    public void validateCurrentSnapshot(AgentTask task, String reason) {
        requireApprovalTask(task);
        requireDecisionReason(reason);
        long articleId = articleId(task.getPayload());
        List<EditorialArticleApprovalStore.VersionSnapshot> current = currentVersions(articleId);
        if (!snapshot(task.getPayload()).equals(current)) {
            throw new IllegalStateException("The current article versions changed after approval was requested");
        }
        requireCurrentImageSnapshot(articleId, task.getPayload());
        var article = workflowStore.lock(articleId);
        if (article.state() != EditorialArticleState.WAITING_APPROVAL) {
            throw new IllegalStateException("Article is not waiting for approval: " + articleId);
        }
    }

    @Transactional
    public void reject(AgentTask task, String actor, String reason) {
        validateCurrentSnapshot(task, reason);
        transition(task, EditorialArticleState.REJECTED, actor, reason);
    }

    @Transactional
    public void complete(ClaimedTask task, AgentTask persistedApprovalTask) {
        AgentTask context = new AgentTask();
        context.setId(task.taskId());
        context.setAgentType(task.agentType());
        context.setTaskType(task.taskType());
        context.setPayload(task.payload());
        context.setCorrelationId(task.correlationId());
        requireApprovalTask(context);
        long articleId = articleId(task.payload());
        if (!snapshot(task.payload()).equals(currentVersions(articleId))) {
            throw new MarketingTaskExecutionException(
                    "ARTICLE_APPROVAL_STALE",
                    "The current article versions changed after approval was requested");
        }
        requireCurrentImageSnapshot(articleId, task.payload());
        EditorialArticleState state = workflowStore.lock(articleId).state();
        if (state == EditorialArticleState.WAITING_APPROVAL) {
            transition(context, EditorialArticleState.APPROVED, persistedApprovalTask.getApprovedBy(),
                    "Approved current article version snapshot");
        } else if (state != EditorialArticleState.APPROVED
                && state != EditorialArticleState.SCHEDULED
                && state != EditorialArticleState.PUBLISHED) {
            throw new MarketingTaskExecutionException(
                    "ARTICLE_APPROVAL_INVALID_STATE",
                    "Article is not eligible for approved publication scheduling");
        }
        publicationService.schedule(persistedApprovalTask, persistedApprovalTask.getApprovedBy());
    }

    private void transition(
            AgentTask task,
            EditorialArticleState target,
            String actor,
            String reason) {
        workflowService.transition(new EditorialArticleWorkflowDtos.TransitionRequest(
                articleId(task.getPayload()),
                target,
                task.getId(),
                task.getCorrelationId(),
                actor,
                reason,
                Set.of()));
    }

    private List<EditorialArticleApprovalStore.VersionSnapshot> currentVersions(long articleId) {
        List<EditorialArticleApprovalStore.VersionSnapshot> versions = approvalStore.currentVersions(articleId);
        Set<String> languages = versions.stream()
                .map(EditorialArticleApprovalStore.VersionSnapshot::language)
                .collect(Collectors.toUnmodifiableSet());
        if (versions.size() != REQUIRED_LANGUAGES.size() || !languages.equals(REQUIRED_LANGUAGES)) {
            throw new IllegalStateException("Current AR, NL, FR and EN article versions are required for approval");
        }
        List<String> missingMetadata = approvalStore.languagesMissingMetadata(articleId);
        if (!missingMetadata.isEmpty()) {
            throw new IllegalStateException(
                    "Complete localized article metadata is required for approval: "
                            + String.join(", ", missingMetadata));
        }
        return versions;
    }

    private ObjectNode payload(
            EditorialArticleWorkflowStore.LockedArticle article,
            List<EditorialArticleApprovalStore.VersionSnapshot> versions,
            EditorialArticleImageStore.ApprovedImage image,
            Set<EditorialArticleQualityGate> gates,
            String reason) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("articleId", article.id());
        payload.put("articleTopicId", article.topicId());
        payload.put("canonicalLanguage", article.canonicalLanguage());
        payload.put("requestReason", reason.trim());
        payload.put("imageAssetId", image.assetId());
        ArrayNode versionArray = payload.putArray("versions");
        versions.forEach(version -> versionArray.addObject()
                .put("id", version.id())
                .put("language", version.language())
                .put("versionNumber", version.versionNumber()));
        ArrayNode gateArray = payload.putArray("passedQualityGates");
        gates.stream().sorted(Comparator.comparing(Enum::name)).map(Enum::name).forEach(gateArray::add);
        return payload;
    }

    private List<EditorialArticleApprovalStore.VersionSnapshot> snapshot(JsonNode payload) {
        JsonNode versions = payload.path("versions");
        if (!versions.isArray()) {
            throw new IllegalStateException("Article approval payload has no version snapshot");
        }
        return java.util.stream.StreamSupport.stream(versions.spliterator(), false)
                .map(version -> new EditorialArticleApprovalStore.VersionSnapshot(
                        version.path("id").asLong(),
                        version.path("language").asText(),
                        version.path("versionNumber").asInt()))
                .sorted(Comparator.comparing(EditorialArticleApprovalStore.VersionSnapshot::language))
                .toList();
    }

    private static String idempotencyKey(
            long articleId,
            List<EditorialArticleApprovalStore.VersionSnapshot> versions,
            EditorialArticleImageStore.ApprovedImage image) {
        String versionIds = versions.stream()
                .map(version -> String.valueOf(version.id()))
                .collect(Collectors.joining("-"));
        return "article-approval:" + articleId + ":" + versionIds + ":image-" + image.assetId();
    }

    private void requireCurrentImageSnapshot(long articleId, JsonNode payload) {
        EditorialArticleImageStore.ApprovedImage current = imageStore.requireApprovalReady(articleId);
        long imageAssetId = payload.path("imageAssetId").asLong();
        if (imageAssetId <= 0 || current.assetId() != imageAssetId) {
            throw new IllegalStateException("The approved article image changed after approval was requested");
        }
    }

    private static long articleId(JsonNode payload) {
        long articleId = payload.path("articleId").asLong();
        if (articleId <= 0) {
            throw new IllegalStateException("Article approval payload has no valid articleId");
        }
        return articleId;
    }

    private static void requireApprovalTask(AgentTask task) {
        if (task == null || !AGENT_TYPE.equals(task.getAgentType()) || !TASK_TYPE.equals(task.getTaskType())) {
            throw new IllegalArgumentException("Task is not an editorial article approval");
        }
    }

    private static void validateRequest(
            long articleId,
            EditorialArticleApprovalDtos.Request request,
            String actor) {
        if (articleId <= 0) {
            throw new IllegalArgumentException("articleId must be positive");
        }
        if (request == null || request.passedQualityGates() == null) {
            throw new IllegalArgumentException("Approval request and quality gates are required");
        }
        requireDecisionReason(request.reason());
        if (actor == null || actor.isBlank() || actor.trim().length() > 160) {
            throw new IllegalArgumentException("A valid approval requester is required");
        }
    }

    private static void requireDecisionReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Article approval decisions require a reason");
        }
        if (reason.trim().length() > 1000) {
            throw new IllegalArgumentException("Article approval reason must not exceed 1000 characters");
        }
    }
}
