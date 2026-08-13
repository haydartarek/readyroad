package com.readyroad.readyroadbackend.marketing.content;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentTaskHandlerTest {

    @Mock ContentAgentService service;

    @Test
    void preservesRetryableOpenAIErrorCodeForUnifiedRetryPolicy() {
        ObjectMapper mapper = new ObjectMapper();
        var payload = new ContentTaskPayload(ContentSourceType.ROAD_SIGN, "A1", ContentTestFixtures.request());
        ClaimedTask task = new ClaimedTask(
                10L, "CONTENT", "CONTENT_PACKAGE_GENERATE", mapper.valueToTree(payload), 1,
                TaskPriority.NORMAL, 1, "correlation");
        when(service.generate(payload, 10L))
                .thenThrow(new OpenAIContentGenerationException("HTTP_503", "OpenAI unavailable"));

        var handler = new ContentTaskHandler(mapper, service);
        assertThatThrownBy(() -> handler.execute(task))
                .isInstanceOf(MarketingTaskExecutionException.class)
                .extracting(error -> ((MarketingTaskExecutionException) error).errorCode())
                .isEqualTo("HTTP_503");
    }
}
