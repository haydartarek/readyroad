package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.analytics.AnalyticsSettingsService;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditorialPerformanceService {

    private static final String ACTOR = "EDITORIAL_WORKER";

    private final EditorialPerformanceStore store;
    private final EditorialPerformancePolicy policy;
    private final AnalyticsSettingsService settingsService;
    private final EditorialArticleWorkflowService workflowService;
    private final EditorialPerformanceTaskService taskService;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void snapshot(ClaimedTask task) {
        TaskContext context = context(task, false);
        LocalDate periodEnd = context.completedThrough();
        LocalDate periodStart = periodEnd.minusDays(context.windowDays() - 1L);
        LocalDate previousEnd = periodStart.minusDays(1);
        LocalDate previousStart = previousEnd.minusDays(context.windowDays() - 1L);
        List<EditorialPerformanceStore.PublicationRoute> routes =
                store.currentPublicationRoutes(context.articleId());
        if (routes.isEmpty()) {
            throw failure("ARTICLE_PERFORMANCE_NOT_PUBLISHED", "Article has no published localized routes");
        }
        Set<Long> capturedPublicationIds = store.snapshotsForTask(context.articleId(), task.taskId()).stream()
                .map(EditorialPerformanceStore.SnapshotRow::publicationId)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        if (routes.stream().allMatch(route -> capturedPublicationIds.contains(route.publicationId()))) {
            taskService.enqueueRefreshRecommendation(task);
            return;
        }
        for (var route : routes) {
            var current = store.aggregate(route.path(), periodStart, periodEnd);
            var previous = store.aggregate(route.path(), previousStart, previousEnd);
            store.saveSnapshot(
                    context.articleId(), route, periodStart, periodEnd, previousStart, previousEnd,
                    current, previous, task.taskId(), context.analyticsTaskId());
        }
        auditService.recordEntityEvent(
                "ARTICLE_PERFORMANCE_SNAPSHOT_CAPTURED",
                ACTOR,
                "EDITORIAL_ARTICLE",
                String.valueOf(context.articleId()),
                task.taskId(),
                task.correlationId(),
                objectMapper.createObjectNode()
                        .put("periodStart", periodStart.toString())
                        .put("periodEnd", periodEnd.toString())
                        .put("localizedRoutes", routes.size()));
        taskService.enqueueRefreshRecommendation(task);
    }

    @Transactional
    public void recommend(ClaimedTask task) {
        TaskContext context = context(task, true);
        long performanceTaskId = task.payload().path("performanceTaskId").asLong(0);
        if (performanceTaskId <= 0) {
            throw failure("ARTICLE_PERFORMANCE_TASK_REQUIRED", "Performance task reference is required");
        }
        List<EditorialPerformanceStore.SnapshotRow> snapshots =
                store.snapshotsForTask(context.articleId(), performanceTaskId);
        if (snapshots.isEmpty()) {
            throw failure("ARTICLE_PERFORMANCE_MISSING", "No performance snapshot exists for this article");
        }
        var decision = policy.evaluate(snapshots, settingsService.current());
        var saved = store.saveRecommendation(
                context.articleId(), performanceTaskId, task.taskId(),
                context.completedThrough(), decision);
        if (!saved.created()) {
            return;
        }
        if (decision.recommended() && store.articleState(context.articleId()) == EditorialArticleState.PUBLISHED) {
            workflowService.transition(new EditorialArticleWorkflowDtos.TransitionRequest(
                    context.articleId(),
                    EditorialArticleState.UPDATE_RECOMMENDED,
                    task.taskId(),
                    task.correlationId(),
                    ACTOR,
                    "Search Console performance met the approved refresh thresholds",
                    Set.of()));
        }
        auditService.recordEntityEvent(
                decision.recommended()
                        ? "ARTICLE_REFRESH_RECOMMENDED"
                        : "ARTICLE_REFRESH_NOT_RECOMMENDED",
                ACTOR,
                "EDITORIAL_ARTICLE",
                String.valueOf(context.articleId()),
                task.taskId(),
                task.correlationId(),
                objectMapper.valueToTree(decision.evidence()));
    }

    @Transactional(readOnly = true)
    public EditorialPerformanceDtos.Overview overview(long articleId) {
        if (articleId <= 0) {
            throw new IllegalArgumentException("articleId must be positive");
        }
        return new EditorialPerformanceDtos.Overview(
                store.latestSnapshots(articleId),
                store.latestRecommendation(articleId).orElse(null));
    }

    private static TaskContext context(ClaimedTask task, boolean recommendation) {
        long articleId = task.payload().path("articleId").asLong(0);
        long analyticsTaskId = recommendation
                ? 0
                : task.payload().path("analyticsTaskId").asLong(0);
        int windowDays = recommendation ? 1 : task.payload().path("windowDays").asInt(0);
        LocalDate completedThrough;
        try {
            completedThrough = LocalDate.parse(task.payload().path("completedThrough").asText());
        } catch (RuntimeException error) {
            throw failure("ARTICLE_PERFORMANCE_PERIOD_INVALID", "Performance period is invalid");
        }
        if (articleId <= 0 || (!recommendation && (analyticsTaskId <= 0 || windowDays < 7 || windowDays > 90))) {
            throw failure("ARTICLE_PERFORMANCE_PAYLOAD_INVALID", "Performance task payload is invalid");
        }
        return new TaskContext(articleId, analyticsTaskId, completedThrough, windowDays);
    }

    private static MarketingTaskExecutionException failure(String code, String message) {
        return new MarketingTaskExecutionException(code, message);
    }

    private record TaskContext(
            long articleId,
            long analyticsTaskId,
            LocalDate completedThrough,
            int windowDays) {}
}
