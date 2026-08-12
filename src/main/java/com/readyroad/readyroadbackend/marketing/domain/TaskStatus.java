package com.readyroad.readyroadbackend.marketing.domain;

public enum TaskStatus {
    PENDING,
    SCHEDULED,
    WAITING_APPROVAL,
    APPROVED,
    RUNNING,
    COMPLETED,
    RETRY_SCHEDULED,
    FAILED,
    REJECTED,
    CANCELLED
}
