package com.readyroad.readyroadbackend.marketing.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationResult;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketingStrategyChangeService {

    public static final String AGENT_TYPE = "STRATEGY";
    public static final String TASK_TYPE = "STRATEGY_CHANGE";

    private final TaskCreationService taskCreationService;
    private final ObjectMapper objectMapper;
    private final StrategyChangeValidator validator;

    public TaskCreationResult requestChange(StrategyChangeRequest request, String actor) {
        var data = objectMapper.valueToTree(request.data());
        validator.validate(request.resourceType(), request.resourceId(), data);
        var payload = objectMapper.createObjectNode()
                .put("resourceType", request.resourceType().name())
                .put("resourceId", request.resourceId())
                .set("data", data);

        return taskCreationService.create(new CreateMarketingTaskCommand(
                AGENT_TYPE,
                TASK_TYPE,
                payload,
                TaskPriority.NORMAL,
                null,
                actor,
                request.idempotencyKey(),
                null,
                null,
                "MARKETING_STRATEGY",
                request.resourceId(),
                ApprovalMetadata.humanApproval("MASTER_SPEC_V3")));
    }
}
