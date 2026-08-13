package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import org.junit.jupiter.api.Test;

class EditorialOpportunityTaskHandlerTest {

    @Test
    void refusesIncompleteDiscoveryPayloads() {
        var handler = new EditorialOpportunityTaskHandler(
                mock(EditorialOpportunityStore.class),
                mock(EditorialPriorityTaskService.class),
                mock(MarketingAuditService.class));
        var payload = new ObjectMapper().createObjectNode()
                .put("sourceOpportunityId", 7)
                .put("queryEvidencePresent", true);

        assertThatThrownBy(() -> handler.execute(new ClaimedTask(
                1L, "EDITORIAL", "ARTICLE_OPPORTUNITY_DISCOVERY", payload,
                1, TaskPriority.HIGH, 1, "test-correlation")))
                .isInstanceOf(MarketingTaskExecutionException.class)
                .extracting(error -> ((MarketingTaskExecutionException) error).errorCode())
                .isEqualTo("INVALID_ARTICLE_OPPORTUNITY_PAYLOAD");
    }
}
