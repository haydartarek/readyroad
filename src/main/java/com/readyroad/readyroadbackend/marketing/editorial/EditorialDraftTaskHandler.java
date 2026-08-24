package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.content.BlockedContentSourceException;
import com.readyroad.readyroadbackend.marketing.content.ContentValidationException;
import com.readyroad.readyroadbackend.marketing.content.OpenAIContentGenerationException;
import com.readyroad.readyroadbackend.marketing.strategy.BlockedStrategyContextException;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditorialDraftTaskHandler implements MarketingTaskHandler {

    private final EditorialDraftService service;

    @Override
    public boolean supports(String agentType, String taskType) {
        return EditorialDraftService.AGENT_TYPE.equals(agentType)
                && EditorialDraftService.TASK_TYPE.equals(taskType);
    }

    @Override
    public void execute(ClaimedTask task) {
        try {
            service.create(task);
        } catch (BlockedStrategyContextException error) {
            throw new MarketingTaskExecutionException("BLOCKED_STRATEGY_CONTEXT", safe(error));
        } catch (BlockedContentSourceException error) {
            throw new MarketingTaskExecutionException("BLOCKED_CONTENT_SOURCE", safe(error));
        } catch (ContentValidationException error) {
            throw new MarketingTaskExecutionException(error.errorCode(), safe(error));
        } catch (OpenAIContentGenerationException error) {
            throw new MarketingTaskExecutionException(error.errorCode(), safe(error));
        } catch (IllegalArgumentException | IllegalStateException error) {
            throw new MarketingTaskExecutionException("ARTICLE_DRAFT_VALIDATION_FAILED", safe(error));
        }
    }

    private static String safe(RuntimeException error) {
        String message = error.getMessage();
        if (message == null || message.isBlank()) {
            return "Article draft failed validation";
        }
        return message.length() <= 300 ? message : message.substring(0, 300);
    }
}
