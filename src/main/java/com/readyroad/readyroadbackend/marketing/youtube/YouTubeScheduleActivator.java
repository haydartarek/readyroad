package com.readyroad.readyroadbackend.marketing.youtube;

import com.readyroad.readyroadbackend.marketing.repository.AgentScheduleRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class YouTubeScheduleActivator {

    private final AgentScheduleRepository repository;

    @Transactional
    public void activateAfterSuccessfulSync(Instant now) {
        repository.findByAgentTypeOrderByScheduleKeyAsc(YouTubeAdminService.AGENT_TYPE)
                .forEach(schedule -> {
                    schedule.setIntervalDays((short) 1);
                    schedule.setEnabled(true);
                    schedule.setNextRunAt(now.plus(24, ChronoUnit.HOURS));
                });
    }
}
