package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditorialPriorityTaskHandler implements MarketingTaskHandler {

    public static final String RECALCULATE = "EDITORIAL_PRIORITY_RECALCULATE";
    public static final String SETTINGS_UPDATE = "EDITORIAL_PRIORITY_SETTINGS_UPDATE";

    private final EditorialPriorityService priorityService;
    private final EditorialPrioritySettingsService settingsService;
    private final MarketingAuditService auditService;

    @Override
    public boolean supports(String agentType, String taskType) {
        return EditorialPrioritySettingsService.AGENT_TYPE.equals(agentType)
                && (RECALCULATE.equals(taskType) || SETTINGS_UPDATE.equals(taskType));
    }

    @Override
    public void execute(ClaimedTask task) {
        if (RECALCULATE.equals(task.taskType())) {
            priorityService.recalculate(
                    task.taskId(),
                    task.payload().path("triggerType").asText("MANUAL"),
                    task.payload().path("actor").asText("MARKETING_WORKER"));
            return;
        }
        if (SETTINGS_UPDATE.equals(task.taskType())) {
            var settings = task.payload().path("settings");
            String actor = task.payload().path("actor").asText("MARKETING_WORKER");
            try {
                settingsService.update(settings, actor);
                auditService.recordEntityEvent(
                        "EDITORIAL_PRIORITY_SETTINGS_UPDATED",
                        actor,
                        "AGENT_SETTING",
                        EditorialPrioritySettingsService.SETTING_KEY,
                        task.taskId(),
                        task.correlationId());
                priorityService.recalculate(task.taskId(), "PRIORITY_SETTINGS_CHANGE", actor);
            } catch (IllegalArgumentException error) {
                throw new MarketingTaskExecutionException(
                        "INVALID_EDITORIAL_PRIORITY_SETTINGS", error.getMessage());
            }
            return;
        }
        throw new MarketingTaskExecutionException(
                "UNSUPPORTED_TASK_TYPE", "Unsupported editorial priority task type");
    }
}
