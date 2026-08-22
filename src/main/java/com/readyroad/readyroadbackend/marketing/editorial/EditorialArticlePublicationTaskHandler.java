package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditorialArticlePublicationTaskHandler implements MarketingTaskHandler {

    private final EditorialArticlePublicationService service;

    @Override
    public boolean supports(String agentType, String taskType) {
        return "EDITORIAL".equals(agentType)
                && EditorialArticlePublicationService.TASK_TYPE.equals(taskType);
    }

    @Override
    public void execute(ClaimedTask task) {
        service.publish(task);
    }
}

