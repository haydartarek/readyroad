package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditorialPerformanceTaskHandler implements MarketingTaskHandler {

    public static final String PERFORMANCE_SNAPSHOT = "ARTICLE_PERFORMANCE_SNAPSHOT";
    public static final String REFRESH_RECOMMENDATION = "ARTICLE_REFRESH_RECOMMENDATION";

    private final EditorialPerformanceService service;

    @Override
    public boolean supports(String agentType, String taskType) {
        return EditorialPerformanceTaskService.AGENT_TYPE.equals(agentType)
                && (PERFORMANCE_SNAPSHOT.equals(taskType) || REFRESH_RECOMMENDATION.equals(taskType));
    }

    @Override
    public void execute(ClaimedTask task) {
        switch (task.taskType()) {
            case PERFORMANCE_SNAPSHOT -> service.snapshot(task);
            case REFRESH_RECOMMENDATION -> service.recommend(task);
            default -> throw new MarketingTaskExecutionException(
                    "UNSUPPORTED_TASK_TYPE", "Unsupported editorial performance task type");
        }
    }
}
