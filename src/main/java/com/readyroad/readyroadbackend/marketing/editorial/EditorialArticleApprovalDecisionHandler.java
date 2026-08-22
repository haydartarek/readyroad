package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.approval.ApprovalDecisionHandler;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class EditorialArticleApprovalDecisionHandler implements ApprovalDecisionHandler {

    private final EditorialArticleApprovalService service;

    @Override
    public boolean supports(AgentTask task) {
        return task != null
                && "EDITORIAL".equals(task.getAgentType())
                && EditorialArticleApprovalService.TASK_TYPE.equals(task.getTaskType());
    }

    @Override
    public void validateApproval(AgentTask task, String actor, String reason) {
        service.validateCurrentSnapshot(task, reason);
    }

    @Override
    public void validateRejection(AgentTask task, String actor, String reason) {
        service.validateCurrentSnapshot(task, reason);
    }

    @Override
    public void afterRejection(AgentTask task, String actor, String reason) {
        service.reject(task, actor, reason);
    }
}
