package com.readyroad.readyroadbackend.marketing.schedule;

import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMode;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.domain.AgentSchedule;
import com.readyroad.readyroadbackend.marketing.repository.AgentScheduleRepository;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketingScheduleService {

    private static final String SCHEDULER_ACTOR = "MARKETING_SCHEDULER";

    private final AgentScheduleRepository scheduleRepository;
    private final TaskCreationService taskCreationService;
    private final MarketingProperties properties;

    @Transactional
    public int enqueueDueSchedules() {
        Instant now = Instant.now();
        var schedules = scheduleRepository.claimDueSchedules(now, properties.getWorker().getBatchSize());
        for (AgentSchedule schedule : schedules) {
            Instant fireTime = schedule.getNextRunAt();
            taskCreationService.create(toCommand(schedule, fireTime));
            schedule.setLastRunAt(fireTime);
            schedule.setNextRunAt(nextRun(schedule, fireTime));
        }
        return schedules.size();
    }

    private static CreateMarketingTaskCommand toCommand(AgentSchedule schedule, Instant fireTime) {
        ApprovalMetadata approval = schedule.isRequiresApproval()
                ? ApprovalMetadata.humanApproval(schedule.getApprovalSource())
                : new ApprovalMetadata(
                        schedule.getApprovalMode() == null
                                ? ApprovalMode.STANDING_OWNER_AUTHORIZATION
                                : schedule.getApprovalMode(),
                        schedule.getApprovalSource(),
                        false);
        return new CreateMarketingTaskCommand(
                schedule.getAgentType(),
                schedule.getTaskType(),
                schedule.getPayload(),
                schedule.getPriority(),
                null,
                SCHEDULER_ACTOR,
                "schedule:" + schedule.getId() + ":" + fireTime,
                "schedule:" + schedule.getId() + ":" + fireTime,
                null,
                "AGENT_SCHEDULE",
                String.valueOf(schedule.getId()),
                approval);
    }

    private static Instant nextRun(AgentSchedule schedule, Instant after) {
        ZoneId zone = ZoneId.of(schedule.getZoneId());
        ZonedDateTime next = CronExpression.parse(schedule.getCronExpression()).next(after.atZone(zone));
        if (next == null) {
            throw new IllegalStateException("Schedule has no next execution: " + schedule.getId());
        }
        return next.toInstant();
    }
}
