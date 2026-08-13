package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EditorialSourceCollectionTaskHandler implements MarketingTaskHandler {

    private final EditorialSourceStore store;
    private final AgentTaskRepository taskRepository;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;
    private final EditorialSourcePolicy policy = new EditorialSourcePolicy();

    @Override
    public boolean supports(String agentType, String taskType) {
        return EditorialPrioritySettingsService.AGENT_TYPE.equals(agentType)
                && EditorialSourceCollectionService.TASK_TYPE.equals(taskType);
    }

    @Override
    @Transactional
    public void execute(ClaimedTask task) {
        AgentTask persistedTask = taskRepository.findById(task.taskId())
                .orElseThrow(() -> new MarketingTaskExecutionException(
                        "ARTICLE_SOURCE_TASK_NOT_FOUND", "Source collection task no longer exists"));
        String approvedBy = persistedTask.getApprovedBy();
        if (approvedBy == null || approvedBy.isBlank()) {
            throw new MarketingTaskExecutionException(
                    "ARTICLE_SOURCE_APPROVAL_REQUIRED", "Source collection requires explicit human approval");
        }
        if (store.collectionCompleted(task.taskId())) {
            return;
        }

        EditorialSourceDtos.SourceCollectionRequest request = payload(task);
        try {
            policy.validate(request);
            store.requireTopic(request.articleTopicId());
        } catch (IllegalArgumentException error) {
            throw new MarketingTaskExecutionException("INVALID_ARTICLE_SOURCE_PAYLOAD", error.getMessage());
        }

        int supported = 0;
        int missing = 0;
        int requiresReview = 0;
        int rejected = 0;
        int changedSources = 0;
        for (EditorialSourceDtos.ClaimInput claim : request.claims()) {
            long claimId = store.upsertClaim(request.articleTopicId(), request.briefReference(), claim);
            for (EditorialSourceDtos.SourceInput input : claim.sources()) {
                EditorialSourceStore.StoredSource source = store.registerOrRefresh(input, approvedBy, policy);
                if (source.fingerprintChanged()) {
                    changedSources++;
                }
                String relationship = relationship(claim.claimType(), source);
                store.linkClaim(
                        claimId,
                        source,
                        relationship,
                        policy.evidencePurpose(claim.claimType()),
                        approvedBy);
            }
            switch (store.refreshClaimEvidence(claimId)) {
                case "SUPPORTED" -> supported++;
                case "MISSING" -> missing++;
                case "REJECTED" -> rejected++;
                default -> requiresReview++;
            }
        }

        var details = objectMapper.createObjectNode()
                .put("articleTopicId", request.articleTopicId())
                .put("briefReference", request.briefReference())
                .put("claimsProcessed", request.claims().size())
                .put("supportedClaims", supported)
                .put("missingClaims", missing)
                .put("claimsRequiringReview", requiresReview)
                .put("rejectedClaims", rejected)
                .put("changedSourcesMarkedStale", changedSources);
        auditService.recordEntityEvent(
                "EDITORIAL_SOURCE_COLLECTION_COMPLETED",
                approvedBy,
                "ARTICLE_TOPIC",
                String.valueOf(request.articleTopicId()),
                task.taskId(),
                task.correlationId(),
                details);
    }

    private EditorialSourceDtos.SourceCollectionRequest payload(ClaimedTask task) {
        try {
            return objectMapper.treeToValue(
                    task.payload(), EditorialSourceDtos.SourceCollectionRequest.class);
        } catch (JsonProcessingException error) {
            throw new MarketingTaskExecutionException(
                    "INVALID_ARTICLE_SOURCE_PAYLOAD", "Source collection payload cannot be read");
        }
    }

    private String relationship(String claimType, EditorialSourceStore.StoredSource source) {
        if (policy.supportsClaim(claimType, source)) {
            return "SUPPORTS";
        }
        if ("REJECTED".equals(source.verificationStatus())
                || "REJECTED".equals(source.legalReviewStatus())) {
            return "REJECTED";
        }
        return "REQUIRES_REVIEW";
    }
}
