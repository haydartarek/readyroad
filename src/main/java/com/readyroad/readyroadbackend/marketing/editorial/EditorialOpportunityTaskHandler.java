package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EditorialOpportunityTaskHandler implements MarketingTaskHandler {

    private final EditorialOpportunityStore store;
    private final EditorialPriorityTaskService priorityTaskService;
    private final MarketingAuditService auditService;

    @Override
    public boolean supports(String agentType, String taskType) {
        return EditorialPrioritySettingsService.AGENT_TYPE.equals(agentType)
                && EditorialOpportunityDiscoveryService.TASK_TYPE.equals(taskType);
    }

    @Override
    @Transactional
    public void execute(ClaimedTask task) {
        long opportunityId = task.payload().path("sourceOpportunityId").asLong(0);
        if (opportunityId <= 0
                || !task.payload().path("queryEvidencePresent").asBoolean(false)
                || !task.payload().path("cannibalizationCheckPassed").asBoolean(false)
                || !task.payload().path("searchIntentCheckPassed").asBoolean(false)
                || !task.payload().path("duplicateCheckPassed").asBoolean(false)
                || !task.payload().path("legalCheckRequired").asBoolean(false)
                || !task.payload().path("humanApprovalRequired").asBoolean(false)) {
            throw new MarketingTaskExecutionException(
                    "INVALID_ARTICLE_OPPORTUNITY_PAYLOAD",
                    "Article opportunity payload is incomplete");
        }
        EditorialOpportunityStore.OpportunityEvidence opportunity;
        try {
            opportunity = store.requireEligible(opportunityId);
        } catch (IllegalStateException error) {
            throw new MarketingTaskExecutionException(
                    "ARTICLE_OPPORTUNITY_NO_LONGER_ELIGIBLE", error.getMessage());
        }
        long topicId = store.createTopic(opportunity);
        auditService.recordEntityEvent(
                "EDITORIAL_OPPORTUNITY_TOPIC_CREATED",
                "EDITORIAL_WORKER",
                "ARTICLE_TOPIC",
                String.valueOf(topicId),
                task.taskId(),
                task.correlationId(),
                auditService.detail("sourceOpportunityId", opportunityId));
        priorityTaskService.enqueueForNewTopic(topicId, "EDITORIAL_WORKER");
    }
}
