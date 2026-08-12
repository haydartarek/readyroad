package com.readyroad.readyroadbackend.marketing.worker;

import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.task.TaskClaimService;
import com.readyroad.readyroadbackend.marketing.task.TaskExecutionService;
import java.net.InetAddress;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "readyroad.marketing.enabled", havingValue = "true")
@Slf4j
public class MarketingTaskWorker {

    private final TaskClaimService claimService;
    private final MarketingTaskDispatcher dispatcher;
    private final TaskExecutionService executionService;
    private final String workerId;

    public MarketingTaskWorker(
            TaskClaimService claimService,
            MarketingTaskDispatcher dispatcher,
            TaskExecutionService executionService) {
        this.claimService = claimService;
        this.dispatcher = dispatcher;
        this.executionService = executionService;
        this.workerId = resolveWorkerId();
    }

    @Scheduled(fixedDelayString = "${readyroad.marketing.worker.poll-interval-ms:5000}")
    public void poll() {
        for (ClaimedTask task : claimService.claimNextBatch(workerId)) {
            execute(task);
        }
    }

    private void execute(ClaimedTask task) {
        try {
            dispatcher.dispatch(task);
            executionService.complete(task.taskId(), workerId);
        } catch (MarketingTaskExecutionException expected) {
            executionService.fail(task.taskId(), workerId, expected);
        } catch (RuntimeException unexpected) {
            log.error("Marketing task {} failed with unexpected type {}",
                    task.taskId(), unexpected.getClass().getSimpleName());
            executionService.fail(task.taskId(), workerId, new MarketingTaskExecutionException(
                    "UNEXPECTED_EXECUTION_ERROR", "Unexpected task execution error"));
        }
    }

    private static String resolveWorkerId() {
        try {
            return InetAddress.getLocalHost().getHostName() + "-" + UUID.randomUUID();
        } catch (Exception ignored) {
            return "readyroad-worker-" + UUID.randomUUID();
        }
    }
}
