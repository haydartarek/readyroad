package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.analytics.AnalyticsSettingsService;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EditorialPerformanceTaskService {

    static final String AGENT_TYPE = "EDITORIAL";
    static final String ARTICLE_SOURCE = "ARTICLE";
    private static final String ACTOR = "ANALYTICS_WORKER";

    private final EditorialPerformanceStore store;
    private final AnalyticsSettingsService settingsService;
    private final TaskCreationService taskCreationService;
    private final ObjectMapper objectMapper;

    public int enqueueAfterAnalytics(long analyticsTaskId, LocalDate completedThrough) {
        int created = 0;
        int windowDays = settingsService.current().windowDays();
        for (long articleId : store.publishedArticleIds()) {
            var payload = objectMapper.createObjectNode()
                    .put("articleId", articleId)
                    .put("analyticsTaskId", analyticsTaskId)
                    .put("completedThrough", completedThrough.toString())
                    .put("windowDays", windowDays);
            var result = taskCreationService.create(new CreateMarketingTaskCommand(
                    AGENT_TYPE,
                    EditorialPerformanceTaskHandler.PERFORMANCE_SNAPSHOT,
                    payload,
                    TaskPriority.NORMAL,
                    null,
                    ACTOR,
                    "article-performance:" + articleId + ":" + completedThrough,
                    null,
                    analyticsTaskId,
                    ARTICLE_SOURCE,
                    String.valueOf(articleId),
                    ApprovalMetadata.standingOwnerAuthorization()));
            if (result.created()) {
                created++;
            }
        }
        return created;
    }

    void enqueueRefreshRecommendation(ClaimedTask performanceTask) {
        long articleId = performanceTask.payload().path("articleId").asLong();
        String completedThrough = performanceTask.payload().path("completedThrough").asText();
        var payload = objectMapper.createObjectNode()
                .put("articleId", articleId)
                .put("performanceTaskId", performanceTask.taskId())
                .put("completedThrough", completedThrough);
        taskCreationService.create(new CreateMarketingTaskCommand(
                AGENT_TYPE,
                EditorialPerformanceTaskHandler.REFRESH_RECOMMENDATION,
                payload,
                TaskPriority.NORMAL,
                null,
                "EDITORIAL_WORKER",
                "article-refresh:" + articleId + ":" + completedThrough,
                performanceTask.correlationId(),
                performanceTask.taskId(),
                ARTICLE_SOURCE,
                String.valueOf(articleId),
                ApprovalMetadata.standingOwnerAuthorization()));
    }
}
