package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TheoryExamTimingTest {

    @Test
    void derivesTheAnsweringWindowFromTheActualQuestionCount() {
        assertThat(TheoryExamTiming.totalSeconds(50)).isEqualTo(750);
        assertThat(TheoryExamTiming.totalMinutes(50)).isEqualByComparingTo("12.50");
        assertThat(TheoryExamTiming.totalSeconds(20)).isEqualTo(300);
    }

    @Test
    void doesNotCreateNegativeDurationsForInvalidCounts() {
        assertThat(TheoryExamTiming.totalSeconds(-1)).isZero();
    }
}
