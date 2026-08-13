package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationResult;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EditorialPriorityTaskService {

    private final TaskCreationService taskCreationService;
    private final ObjectMapper objectMapper;

    public TaskCreationResult enqueue(String triggerType, String triggerId, String actor) {
        var payload = objectMapper.createObjectNode()
                .put("triggerType", triggerType)
                .put("triggerId", triggerId)
                .put("actor", actor);
        return taskCreationService.create(new CreateMarketingTaskCommand(
                EditorialPrioritySettingsService.AGENT_TYPE,
                EditorialPriorityTaskHandler.RECALCULATE,
                payload,
                TaskPriority.HIGH,
                null,
                actor,
                "priority:" + triggerType + ":" + triggerId,
                null,
                null,
                triggerType,
                triggerId,
                ApprovalMetadata.standingOwnerAuthorization()));
    }

    public TaskCreationResult enqueueAfterAnalytics(Long analyticsTaskId, LocalDate completedThrough) {
        return enqueue("ANALYTICS_SYNC", analyticsTaskId + ":" + completedThrough, "ANALYTICS_WORKER");
    }

    public TaskCreationResult enqueueAfterStrategyChange(Long strategyTaskId) {
        return enqueue("STRATEGY_CHANGE", String.valueOf(strategyTaskId), "STRATEGY_WORKER");
    }

    public TaskCreationResult enqueueForNewTopic(Long topicId, String actor) {
        return enqueue("ARTICLE_TOPIC_ADDED", String.valueOf(topicId), actor);
    }
}
