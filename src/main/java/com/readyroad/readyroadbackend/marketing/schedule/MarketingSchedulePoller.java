package com.readyroad.readyroadbackend.marketing.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "readyroad.marketing.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class MarketingSchedulePoller {

    private final MarketingScheduleService scheduleService;

    @Scheduled(fixedDelayString = "${readyroad.marketing.worker.poll-interval-ms:5000}")
    public void poll() {
        int enqueued = scheduleService.enqueueDueSchedules();
        if (enqueued > 0) {
            log.info("Marketing scheduler enqueued {} task(s)", enqueued);
        }
    }
}
