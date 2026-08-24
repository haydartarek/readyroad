package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.strategy.BlockedStrategyContextException;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditorialBriefTaskHandler implements MarketingTaskHandler {

    private final EditorialBriefService service;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(String agentType, String taskType) {
        return EditorialBriefService.AGENT_TYPE.equals(agentType)
                && EditorialBriefService.TASK_TYPE.equals(taskType);
    }

    @Override
    public void execute(ClaimedTask task) {
        try {
            ObjectNode requestPayload = task.payload().deepCopy();
            requestPayload.remove("articleTopicId");
            EditorialBriefDtos.CreateRequest request = objectMapper.treeToValue(
                    requestPayload, EditorialBriefDtos.CreateRequest.class);
            service.create(task, request);
        } catch (BlockedStrategyContextException error) {
            throw new MarketingTaskExecutionException("BLOCKED_STRATEGY_CONTEXT", safe(error));
        } catch (JsonProcessingException error) {
            throw new MarketingTaskExecutionException(
                    "INVALID_ARTICLE_BRIEF_PAYLOAD", "Article brief task payload cannot be read");
        } catch (IllegalArgumentException | IllegalStateException error) {
            throw new MarketingTaskExecutionException("ARTICLE_BRIEF_VALIDATION_FAILED", safe(error));
        }
    }

    private static String safe(RuntimeException error) {
        String message = error.getMessage();
        if (message == null || message.isBlank()) {
            return "Article brief failed validation";
        }
        return message.length() <= 300 ? message : message.substring(0, 300);
    }
}
