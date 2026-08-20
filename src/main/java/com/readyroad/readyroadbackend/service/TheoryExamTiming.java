package com.readyroad.readyroadbackend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class TheoryExamTiming {

    public static final int QUESTION_TIME_SECONDS = 15;

    private TheoryExamTiming() {
    }

    public static int totalSeconds(int questionCount) {
        return Math.max(questionCount, 0) * QUESTION_TIME_SECONDS;
    }

    public static BigDecimal totalMinutes(int questionCount) {
        return BigDecimal.valueOf(totalSeconds(questionCount))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.UNNECESSARY);
    }
}
