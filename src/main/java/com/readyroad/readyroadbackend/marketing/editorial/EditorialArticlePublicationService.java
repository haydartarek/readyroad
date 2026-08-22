package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMode;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationResult;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class EditorialArticlePublicationService {

    static final String TASK_TYPE = "ARTICLE_PUBLISH";
    private static final String AGENT_TYPE = "EDITORIAL";
    private static final String ARTICLE_SOURCE = "ARTICLE";
    private static final String SYSTEM_ACTOR = "SYSTEM";
    private static final Set<String> REQUIRED_LANGUAGES = Set.of("AR", "NL", "FR", "EN");

    private final EditorialArticleWorkflowStore workflowStore;
    private final EditorialArticleWorkflowService workflowService;
    private final EditorialArticleApprovalStore approvalStore;
    private final EditorialArticlePublicationStore publicationStore;
    private final TaskCreationService taskCreationService;
    private final AgentTaskRepository taskRepository;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;

    @Transactional
    TaskCreationResult schedule(AgentTask approvalTask, String actor) {
        requireApprovedHumanTask(approvalTask);
        long articleId = articleId(approvalTask.getPayload());
        List<EditorialArticleApprovalStore.VersionSnapshot> versions = snapshot(approvalTask.getPayload());
        requireCompleteSnapshot(versions);
        if (!versions.equals(currentVersions(articleId))) {
            throw new MarketingTaskExecutionException(
                    "ARTICLE_PUBLICATION_STALE",
                    "The approved article versions changed before publication scheduling");
        }

        ObjectNode payload = publicationPayload(articleId, approvalTask, versions);
        TaskCreationResult result = taskCreationService.create(new CreateMarketingTaskCommand(
                AGENT_TYPE,
                TASK_TYPE,
                payload,
                TaskPriority.CRITICAL,
                null,
                requireActor(actor),
                idempotencyKey(articleId, versions),
                approvalTask.getCorrelationId(),
                approvalTask.getId(),
                ARTICLE_SOURCE,
                String.valueOf(articleId),
                ApprovalMetadata.standingOwnerAuthorization()));

        EditorialArticleWorkflowStore.LockedArticle article = workflowStore.lock(articleId);
        if (article.state() == EditorialArticleState.APPROVED) {
            workflowService.transition(new EditorialArticleWorkflowDtos.TransitionRequest(
                    articleId,
                    EditorialArticleState.SCHEDULED,
                    result.task().getId(),
                    result.task().getCorrelationId(),
                    requireActor(actor),
                    "Exact approved article snapshot queued for publication",
                    Set.of()));
        } else if (article.state() != EditorialArticleState.SCHEDULED
                && article.state() != EditorialArticleState.PUBLISHED) {
            throw new MarketingTaskExecutionException(
                    "ARTICLE_PUBLICATION_INVALID_STATE",
                    "Article is not approved for publication scheduling");
        }
        return result;
    }

    @Transactional
    void publish(ClaimedTask claimed) {
        AgentTask publicationTask = taskRepository.findById(claimed.taskId())
                .orElseThrow(() -> failure(
                        "ARTICLE_PUBLICATION_TASK_NOT_FOUND",
                        "Article publication task no longer exists"));
        validatePublicationTask(publicationTask, claimed);
        AgentTask approvalTask = approvedParent(publicationTask);
        long articleId = articleId(publicationTask.getPayload());
        List<EditorialArticleApprovalStore.VersionSnapshot> approvedVersions = snapshot(publicationTask.getPayload());
        requireCompleteSnapshot(approvedVersions);
        if (!approvedVersions.equals(snapshot(approvalTask.getPayload()))
                || !approvedVersions.equals(currentVersions(articleId))) {
            throw failure(
                    "ARTICLE_PUBLICATION_STALE",
                    "The current article versions do not match the approved publication snapshot");
        }

        EditorialArticleWorkflowStore.LockedArticle article = workflowStore.lock(articleId);
        if (article.state() == EditorialArticleState.PUBLISHED) {
            if (!publicationStore.hasExactPublications(
                    articleId, approvalTask.getId(), publicationTask.getId(), approvedVersions)) {
                throw failure(
                        "ARTICLE_PUBLICATION_INCONSISTENT",
                        "Published article records do not match the approved snapshot");
            }
            return;
        }
        if (article.state() != EditorialArticleState.SCHEDULED) {
            throw failure(
                    "ARTICLE_PUBLICATION_NOT_APPROVED",
                    "Article publication requires an approved and scheduled article");
        }

        publicationStore.publish(
                articleId, approvalTask.getId(), publicationTask.getId(), approvedVersions);
        workflowService.transition(new EditorialArticleWorkflowDtos.TransitionRequest(
                articleId,
                EditorialArticleState.PUBLISHED,
                publicationTask.getId(),
                publicationTask.getCorrelationId(),
                SYSTEM_ACTOR,
                "Published exact human-approved article version snapshot",
                Set.of()));
        auditService.recordEntityEvent(
                "ARTICLE_PUBLISHED",
                SYSTEM_ACTOR,
                "EDITORIAL_ARTICLE",
                String.valueOf(articleId),
                publicationTask.getId(),
                publicationTask.getCorrelationId(),
                publicationAuditDetails(approvalTask, approvedVersions));
    }

    private AgentTask approvedParent(AgentTask publicationTask) {
        Long parentTaskId = publicationTask.getParentTaskId();
        if (parentTaskId == null || parentTaskId <= 0) {
            throw failure(
                    "ARTICLE_PUBLICATION_NOT_APPROVED",
                    "Article publication task has no human-approved parent task");
        }
        AgentTask approvalTask = taskRepository.findById(parentTaskId)
                .orElseThrow(() -> failure(
                        "ARTICLE_PUBLICATION_NOT_APPROVED",
                        "Human approval task no longer exists"));
        try {
            requireApprovedHumanTask(approvalTask);
        } catch (RuntimeException invalidApproval) {
            throw failure(
                    "ARTICLE_PUBLICATION_NOT_APPROVED",
                    "Article publication parent task is not human approved");
        }
        if (!String.valueOf(articleId(publicationTask.getPayload())).equals(approvalTask.getSourceId())) {
            throw failure(
                    "ARTICLE_PUBLICATION_NOT_APPROVED",
                    "Article publication parent approval targets another article");
        }
        return approvalTask;
    }

    private void validatePublicationTask(AgentTask task, ClaimedTask claimed) {
        if (!AGENT_TYPE.equals(task.getAgentType())
                || !TASK_TYPE.equals(task.getTaskType())
                || !ARTICLE_SOURCE.equals(task.getSourceType())
                || !task.getId().equals(claimed.taskId())
                || !task.getCorrelationId().equals(claimed.correlationId())) {
            throw failure(
                    "ARTICLE_PUBLICATION_TASK_INVALID",
                    "Task is not a valid editorial publication task");
        }
    }

    private static void requireApprovedHumanTask(AgentTask approvalTask) {
        if (approvalTask == null
                || !AGENT_TYPE.equals(approvalTask.getAgentType())
                || !EditorialArticleApprovalService.TASK_TYPE.equals(approvalTask.getTaskType())
                || approvalTask.getApprovalMode() != ApprovalMode.HUMAN_APPROVAL
                || approvalTask.getApprovedBy() == null
                || approvalTask.getApprovedBy().isBlank()
                || !Set.of(TaskStatus.APPROVED, TaskStatus.RUNNING, TaskStatus.COMPLETED)
                        .contains(approvalTask.getStatus())) {
            throw new IllegalStateException("Article publication requires an approved human task");
        }
    }

    private List<EditorialArticleApprovalStore.VersionSnapshot> currentVersions(long articleId) {
        return approvalStore.currentVersions(articleId);
    }

    private ObjectNode publicationPayload(
            long articleId,
            AgentTask approvalTask,
            List<EditorialArticleApprovalStore.VersionSnapshot> versions) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("articleId", articleId);
        payload.put("approvalTaskId", approvalTask.getId());
        payload.put("approvalPayloadVersion", approvalTask.getPayloadVersion());
        ArrayNode versionArray = payload.putArray("versions");
        versions.forEach(version -> versionArray.addObject()
                .put("id", version.id())
                .put("language", version.language())
                .put("versionNumber", version.versionNumber()));
        return payload;
    }

    private ObjectNode publicationAuditDetails(
            AgentTask approvalTask,
            List<EditorialArticleApprovalStore.VersionSnapshot> versions) {
        ObjectNode details = objectMapper.createObjectNode();
        details.put("approvalTaskId", approvalTask.getId());
        details.put("approvalPayloadVersion", approvalTask.getPayloadVersion());
        ArrayNode publishedVersions = details.putArray("publishedVersions");
        versions.forEach(version -> publishedVersions.addObject()
                .put("id", version.id())
                .put("language", version.language())
                .put("versionNumber", version.versionNumber()));
        return details;
    }

    private static List<EditorialArticleApprovalStore.VersionSnapshot> snapshot(JsonNode payload) {
        JsonNode versions = payload.path("versions");
        if (!versions.isArray()) {
            throw failure(
                    "ARTICLE_PUBLICATION_SNAPSHOT_MISSING",
                    "Article publication payload has no approved version snapshot");
        }
        return StreamSupport.stream(versions.spliterator(), false)
                .map(version -> new EditorialArticleApprovalStore.VersionSnapshot(
                        version.path("id").asLong(),
                        version.path("language").asText(),
                        version.path("versionNumber").asInt()))
                .sorted(Comparator.comparing(EditorialArticleApprovalStore.VersionSnapshot::language))
                .toList();
    }

    private static void requireCompleteSnapshot(
            List<EditorialArticleApprovalStore.VersionSnapshot> versions) {
        Set<String> languages = versions.stream()
                .map(EditorialArticleApprovalStore.VersionSnapshot::language)
                .collect(Collectors.toUnmodifiableSet());
        boolean validValues = versions.stream().allMatch(version -> version.id() > 0 && version.versionNumber() > 0);
        if (versions.size() != REQUIRED_LANGUAGES.size()
                || !languages.equals(REQUIRED_LANGUAGES)
                || !validValues) {
            throw failure(
                    "ARTICLE_PUBLICATION_SNAPSHOT_INVALID",
                    "Current AR, NL, FR and EN approved versions are required for publication");
        }
    }

    private static long articleId(JsonNode payload) {
        long articleId = payload.path("articleId").asLong();
        if (articleId <= 0) {
            throw failure(
                    "ARTICLE_PUBLICATION_ARTICLE_INVALID",
                    "Article publication payload has no valid articleId");
        }
        return articleId;
    }

    private static String idempotencyKey(
            long articleId,
            List<EditorialArticleApprovalStore.VersionSnapshot> versions) {
        String versionIds = versions.stream()
                .map(version -> String.valueOf(version.id()))
                .collect(Collectors.joining("-"));
        return "article-publication:" + articleId + ":" + versionIds;
    }

    private static String requireActor(String actor) {
        if (actor == null || actor.isBlank()) {
            throw new IllegalArgumentException("Publication scheduling actor is required");
        }
        return actor.trim();
    }

    private static MarketingTaskExecutionException failure(String code, String message) {
        return new MarketingTaskExecutionException(code, message);
    }
}

