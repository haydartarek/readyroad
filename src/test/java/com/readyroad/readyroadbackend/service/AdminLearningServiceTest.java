package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class AdminLearningServiceTest {

    @Test
    void reportsInsufficientDataUntilTwoCompletedExamsExist() {
        assertThat(AdminLearningService.trend(List.of())).isEqualTo("INSUFFICIENT_DATA");
        assertThat(AdminLearningService.trend(List.of(82.0))).isEqualTo("INSUFFICIENT_DATA");
    }

    @Test
    void comparesRecentCompletedExamScoresWithThePreviousWindow() {
        assertThat(AdminLearningService.trend(List.of(90.0, 80.0, 70.0, 60.0)))
                .isEqualTo("IMPROVING");
        assertThat(AdminLearningService.trend(List.of(75.0, 75.0, 75.0, 75.0)))
                .isEqualTo("STABLE");
        assertThat(AdminLearningService.trend(List.of(55.0, 60.0, 75.0, 80.0)))
                .isEqualTo("DECLINING");
    }
}
