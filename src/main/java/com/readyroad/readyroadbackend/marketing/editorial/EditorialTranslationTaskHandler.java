package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.content.ContentValidationException;
import com.readyroad.readyroadbackend.marketing.content.OpenAIContentGenerationException;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditorialTranslationTaskHandler implements MarketingTaskHandler {

    private final EditorialTranslationService service;

    @Override
    public boolean supports(String agentType, String taskType) {
        return EditorialTranslationService.AGENT_TYPE.equals(agentType)
                && EditorialTranslationService.TASK_TYPE.equals(taskType);
    }

    @Override
    public void execute(ClaimedTask task) {
        try {
            service.create(task);
        } catch (ContentValidationException error) {
            throw new MarketingTaskExecutionException(error.errorCode(), safe(error));
        } catch (OpenAIContentGenerationException error) {
            throw new MarketingTaskExecutionException(error.errorCode(), safe(error));
        } catch (IllegalArgumentException | IllegalStateException error) {
            throw new MarketingTaskExecutionException(
                    "ARTICLE_TRANSLATION_VALIDATION_FAILED",
                    safe(error));
        }
    }

    private static String safe(RuntimeException error) {
        String message = error.getMessage();

        if (message == null || message.isBlank()) {
            return "Article translation adaptation failed validation";
        }

        return message.length() <= 300
                ? message
                : message.substring(0, 300);
    }
}