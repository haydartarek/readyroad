package com.readyroad.readyroadbackend.domain.enums;

public enum CategoryContentScope {
    TRAFFIC_SIGN,
    THEORETICAL_EXAM,
    BOTH;

    public boolean supportsTheoreticalExam() {
        return this == THEORETICAL_EXAM || this == BOTH;
    }
}
