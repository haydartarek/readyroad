package com.readyroad.readyroadbackend.marketing.analytics;

import com.readyroad.readyroadbackend.marketing.domain.AgentSchedule;
import com.readyroad.readyroadbackend.marketing.repository.AgentScheduleRepository;
import java.time.Instant;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsScheduleActivator {

    private final AgentScheduleRepository repository;
    private final AnalyticsSettingsService settingsService;

    @Transactional
    public void activateAfterSuccessfulSync(Instant now) {
        AnalyticsSettings settings = settingsService.current();
        for (AgentSchedule schedule : repository.findByAgentTypeOrderByScheduleKeyAsc(
                AnalyticsSettingsService.AGENT_TYPE)) {
            boolean needsInitialSchedule = !schedule.isEnabled() || schedule.getNextRunAt() == null;
            schedule.setEnabled(true);
            if ("analytics-full-sync".equals(schedule.getScheduleKey())) {
                schedule.setIntervalDays((short) settings.intervalDays());
                if (needsInitialSchedule) {
                    schedule.setNextRunAt(now.atZone(ZoneId.of(schedule.getZoneId()))
                            .plusDays(settings.intervalDays()).toInstant());
                }
            } else if (needsInitialSchedule) {
                var next = CronExpression.parse(schedule.getCronExpression())
                        .next(now.atZone(ZoneId.of(schedule.getZoneId())));
                schedule.setNextRunAt(next == null ? null : next.toInstant());
            }
        }
    }
}
