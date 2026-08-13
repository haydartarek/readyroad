package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.text.Normalizer;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EditorialOpportunityDiscoveryService {

    static final String TASK_TYPE = "ARTICLE_OPPORTUNITY_DISCOVERY";
    private static final String SOURCE_TYPE = "SEARCH_CONSOLE_OPPORTUNITY";

    private final EditorialOpportunityStore store;
    private final TaskCreationService taskCreationService;
    private final ObjectMapper objectMapper;

    public int enqueueCandidates(Long analyticsTaskId) {
        int created = 0;
        for (EditorialOpportunityStore.OpportunityEvidence opportunity : store.eligibleOpportunities()) {
            if (duplicatesExistingTopic(opportunity)) {
                continue;
            }
            var payload = objectMapper.createObjectNode()
                    .put("sourceOpportunityId", opportunity.id())
                    .put("query", opportunity.query())
                    .put("language", opportunity.language())
                    .put("page", opportunity.page())
                    .put("searchIntent", opportunity.searchIntent())
                    .put("impressions", opportunity.impressions())
                    .put("clicks", opportunity.clicks())
                    .put("ctr", opportunity.ctr())
                    .put("averagePosition", opportunity.averagePosition())
                    .put("longTail", opportunity.longTail())
                    .put("queryEvidencePresent", true)
                    .put("cannibalizationCheckPassed", true)
                    .put("searchIntentCheckPassed", true)
                    .put("duplicateCheckPassed", true)
                    .put("legalCheckRequired", true)
                    .put("humanApprovalRequired", true);
            var result = taskCreationService.create(new CreateMarketingTaskCommand(
                    EditorialPrioritySettingsService.AGENT_TYPE,
                    TASK_TYPE,
                    payload,
                    TaskPriority.HIGH,
                    null,
                    "ANALYTICS_WORKER",
                    "article-opportunity:" + opportunity.id(),
                    null,
                    analyticsTaskId,
                    SOURCE_TYPE,
                    String.valueOf(opportunity.id()),
                    ApprovalMetadata.humanApproval("MASTER_SPEC_V3_PART_06_NEW_OPPORTUNITIES")));
            if (result.created()) {
                created++;
            }
        }
        return created;
    }

    boolean duplicatesExistingTopic(EditorialOpportunityStore.OpportunityEvidence opportunity) {
        String candidate = normalize(opportunity.query());
        return store.existingTitlesAndQueries(opportunity.language()).stream()
                .map(EditorialOpportunityDiscoveryService::normalize)
                .anyMatch(existing -> !existing.isBlank()
                        && (existing.equals(candidate)
                                || existing.contains(candidate)
                                || candidate.contains(existing)));
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }
}
