package com.readyroad.readyroadbackend.marketing.domain;

import java.util.Arrays;

public enum TaskPriority {
    LOW(0),
    NORMAL(1),
    HIGH(2),
    CRITICAL(3);

    private final int value;

    TaskPriority(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static TaskPriority fromValue(int value) {
        return Arrays.stream(values())
                .filter(priority -> priority.value == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported task priority: " + value));
    }
}
