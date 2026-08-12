package com.readyroad.readyroadbackend.marketing.worker;

import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;

public interface MarketingTaskHandler {
    boolean supports(String agentType, String taskType);

    void execute(ClaimedTask task);
}
