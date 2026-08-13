package com.readyroad.readyroadbackend.marketing.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.strategy.BlockedStrategyContextException;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentTaskHandler implements MarketingTaskHandler {

    public static final String AGENT_TYPE = "CONTENT";
    public static final String GENERATE_PACKAGE = "CONTENT_PACKAGE_GENERATE";

    private final ObjectMapper objectMapper;
    private final ContentAgentService service;

    @Override
    public boolean supports(String agentType, String taskType) {
        return AGENT_TYPE.equals(agentType) && GENERATE_PACKAGE.equals(taskType);
    }

    @Override
    public void execute(ClaimedTask task) {
        try {
            ContentTaskPayload payload = objectMapper.treeToValue(task.payload(), ContentTaskPayload.class);
            service.generate(payload, task.taskId());
        } catch (BlockedStrategyContextException error) {
            throw new MarketingTaskExecutionException("BLOCKED_STRATEGY_CONTEXT", safe(error));
        } catch (BlockedContentSourceException error) {
            throw new MarketingTaskExecutionException("BLOCKED_CONTENT_SOURCE", safe(error));
        } catch (ContentValidationException error) {
            throw new MarketingTaskExecutionException(error.errorCode(), safe(error));
        } catch (OpenAIContentGenerationException error) {
            throw new MarketingTaskExecutionException(error.errorCode(), safe(error));
        } catch (com.fasterxml.jackson.core.JsonProcessingException error) {
            throw new MarketingTaskExecutionException("INVALID_TASK_PAYLOAD", "Content task payload is invalid");
        }
    }

    private static String safe(RuntimeException error) {
        String message = error.getMessage();
        if (message == null || message.isBlank()) {
            return "Content task failed validation";
        }
        return message.length() <= 300 ? message : message.substring(0, 300);
    }
}
