package com.readyroad.readyroadbackend.marketing.task;

import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TaskStateMachine {

    private static final Map<TaskStatus, EnumSet<TaskStatus>> TRANSITIONS = buildTransitions();

    public void validate(TaskStatus from, TaskStatus to) {
        if (!TRANSITIONS.getOrDefault(from, EnumSet.noneOf(TaskStatus.class)).contains(to)) {
            throw new InvalidTaskStateTransitionException(from, to);
        }
    }

    private static Map<TaskStatus, EnumSet<TaskStatus>> buildTransitions() {
        Map<TaskStatus, EnumSet<TaskStatus>> transitions = new EnumMap<>(TaskStatus.class);
        transitions.put(TaskStatus.PENDING,
                EnumSet.of(TaskStatus.RUNNING, TaskStatus.SCHEDULED, TaskStatus.WAITING_APPROVAL,
                        TaskStatus.CANCELLED));
        transitions.put(TaskStatus.SCHEDULED, EnumSet.of(TaskStatus.RUNNING, TaskStatus.CANCELLED));
        transitions.put(TaskStatus.WAITING_APPROVAL,
                EnumSet.of(TaskStatus.APPROVED, TaskStatus.REJECTED, TaskStatus.CANCELLED));
        transitions.put(TaskStatus.APPROVED,
                EnumSet.of(TaskStatus.RUNNING, TaskStatus.SCHEDULED, TaskStatus.WAITING_APPROVAL,
                        TaskStatus.CANCELLED));
        transitions.put(TaskStatus.RUNNING,
                EnumSet.of(TaskStatus.COMPLETED, TaskStatus.RETRY_SCHEDULED, TaskStatus.FAILED));
        transitions.put(TaskStatus.RETRY_SCHEDULED, EnumSet.of(TaskStatus.RUNNING, TaskStatus.CANCELLED));
        return Map.copyOf(transitions);
    }
}
