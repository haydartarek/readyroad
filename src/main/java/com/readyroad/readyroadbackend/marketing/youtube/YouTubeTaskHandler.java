package com.readyroad.readyroadbackend.marketing.youtube;

import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class YouTubeTaskHandler implements MarketingTaskHandler {

    public static final String CHANNEL_SYNC = "YOUTUBE_CHANNEL_SYNC";

    private final YouTubeSyncService syncService;

    @Override
    public boolean supports(String agentType, String taskType) {
        return YouTubeAdminService.AGENT_TYPE.equals(agentType) && CHANNEL_SYNC.equals(taskType);
    }

    @Override
    public void execute(ClaimedTask task) {
        if (!CHANNEL_SYNC.equals(task.taskType())) {
            throw new MarketingTaskExecutionException(
                    "UNSUPPORTED_TASK_TYPE", "Unsupported YouTube task type");
        }
        syncService.synchronize(task.taskId(), task.correlationId());
    }
}
