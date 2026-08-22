package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditorialArticleApprovalTaskHandler implements MarketingTaskHandler {

    private final EditorialArticleApprovalService service;
    private final AgentTaskRepository taskRepository;

    @Override
    public boolean supports(String agentType, String taskType) {
        return "EDITORIAL".equals(agentType)
                && EditorialArticleApprovalService.TASK_TYPE.equals(taskType);
    }

    @Override
    public void execute(ClaimedTask task) {
        AgentTask persisted = taskRepository.findById(task.taskId())
                .orElseThrow(() -> new MarketingTaskExecutionException(
                        "ARTICLE_APPROVAL_TASK_NOT_FOUND",
                        "Article approval task no longer exists"));
        if (persisted.getApprovedBy() == null || persisted.getApprovedBy().isBlank()) {
            throw new MarketingTaskExecutionException(
                    "ARTICLE_APPROVAL_REQUIRED",
                    "Article publication requires explicit human approval");
        }
        service.complete(task, persisted.getApprovedBy());
    }
}
