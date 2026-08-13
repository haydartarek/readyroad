package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditorialSourceCollectionService {

    static final String TASK_TYPE = "ARTICLE_SOURCE_COLLECT";
    private static final String APPROVAL_SOURCE = "MASTER_SPEC_V3_PART_06_SOURCE_REGISTRY";

    private final EditorialSourceStore store;
    private final TaskCreationService taskCreationService;
    private final ObjectMapper objectMapper;
    private final EditorialSourcePolicy policy = new EditorialSourcePolicy();

    @Transactional(readOnly = true)
    public List<EditorialSourceDtos.Source> sources(Long articleTopicId) {
        if (articleTopicId != null) {
            store.requireTopic(articleTopicId);
        }
        return store.list(articleTopicId);
    }

    @Transactional
    public MarketingTaskLifecycleResponse request(
            EditorialSourceDtos.SourceCollectionRequest request,
            String actor) {
        policy.validate(request);
        store.requireTopic(request.articleTopicId());
        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                EditorialPrioritySettingsService.AGENT_TYPE,
                TASK_TYPE,
                objectMapper.valueToTree(request),
                TaskPriority.HIGH,
                null,
                actor,
                "source-collect:" + request.idempotencyKey().trim(),
                null,
                null,
                "ARTICLE_TOPIC",
                String.valueOf(request.articleTopicId()),
                ApprovalMetadata.humanApproval(APPROVAL_SOURCE)));
        return MarketingTaskLifecycleResponse.from(result.task());
    }
}
