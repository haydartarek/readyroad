package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditorialPriorityAdminService {

    private final EditorialPriorityService priorityService;
    private final EditorialPrioritySettingsService settingsService;
    private final EditorialPriorityTaskService priorityTaskService;
    private final TaskCreationService taskCreationService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<EditorialDtos.Priority> priorities() {
        return priorityService.priorities();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> settings() {
        return objectMapper.convertValue(
                settingsService.raw(),
                new TypeReference<Map<String, Object>>() {});
    }

    @Transactional
    public MarketingTaskLifecycleResponse requestRecalculation(String idempotencyKey, String actor) {
        return MarketingTaskLifecycleResponse.from(
                priorityTaskService.enqueue("MANUAL", idempotencyKey, actor).task());
    }

    @Transactional
    public MarketingTaskLifecycleResponse requestSettingsUpdate(
            EditorialDtos.SettingsUpdateRequest request,
            String actor) {
        var payload = objectMapper.createObjectNode();
        payload.set("settings", request.settings().deepCopy());
        payload.put("actor", actor);
        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                EditorialPrioritySettingsService.AGENT_TYPE,
                EditorialPriorityTaskHandler.SETTINGS_UPDATE,
                payload,
                TaskPriority.HIGH,
                null,
                actor,
                request.idempotencyKey(),
                null,
                null,
                "AGENT_SETTING",
                EditorialPrioritySettingsService.SETTING_KEY,
                ApprovalMetadata.standingOwnerAuthorization()));
        return MarketingTaskLifecycleResponse.from(result.task());
    }
}
