package com.readyroad.readyroadbackend.marketing.approval;

import com.readyroad.readyroadbackend.marketing.domain.AgentTask;

public interface ApprovalDecisionHandler {

    boolean supports(AgentTask task);

    default void validateApproval(AgentTask task, String actor, String reason) {
    }

    default void validateRejection(AgentTask task, String actor, String reason) {
    }

    default void afterRejection(AgentTask task, String actor, String reason) {
    }
}
