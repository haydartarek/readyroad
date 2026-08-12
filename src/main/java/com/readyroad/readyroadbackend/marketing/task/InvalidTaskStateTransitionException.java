package com.readyroad.readyroadbackend.marketing.task;

import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;

public class InvalidTaskStateTransitionException extends IllegalStateException {

    public InvalidTaskStateTransitionException(TaskStatus from, TaskStatus to) {
        super("Invalid marketing task state transition: " + from + " -> " + to);
    }
}
