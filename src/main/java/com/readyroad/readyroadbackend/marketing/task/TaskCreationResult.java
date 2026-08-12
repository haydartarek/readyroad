package com.readyroad.readyroadbackend.marketing.task;

import com.readyroad.readyroadbackend.marketing.domain.AgentTask;

public record TaskCreationResult(AgentTask task, boolean created) {
}
