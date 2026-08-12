package com.readyroad.readyroadbackend.marketing.worker;

import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketingTaskDispatcher {

    private final List<MarketingTaskHandler> handlers;

    public void dispatch(ClaimedTask task) {
        MarketingTaskHandler handler = handlers.stream()
                .filter(candidate -> candidate.supports(task.agentType(), task.taskType()))
                .findFirst()
                .orElseThrow(() -> new MarketingTaskExecutionException(
                        "UNSUPPORTED_TASK_TYPE", "No handler is registered for this task type"));
        handler.execute(task);
    }
}
