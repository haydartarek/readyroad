package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.NotificationType;
import com.readyroad.readyroadbackend.domain.repository.NotificationRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudyReminderSchedulerTest {

    @Mock UserRepository userRepository;
    @Mock AdminLearningStore learningStore;
    @Mock NotificationRepository notificationRepository;
    @Mock NotificationService notificationService;
    @InjectMocks StudyReminderScheduler scheduler;

    @Test
    void requiresACompleteInactivityWindowBeforeSending() {
        Long userId = 17L;
        LocalDateTime threshold = LocalDateTime.of(2026, 8, 17, 10, 0);
        Instant cooldownCutoff = Instant.parse("2026-08-19T10:00:00Z");
        when(notificationRepository.findByUserIdAndTypeAndCreatedAtAfter(
                userId, NotificationType.STUDY_REMINDER, cooldownCutoff)).thenReturn(List.of());

        assertThat(scheduler.shouldSendReminder(
                userId, threshold.minusSeconds(1), threshold, cooldownCutoff)).isTrue();
        assertThat(scheduler.shouldSendReminder(
                userId, threshold, threshold, cooldownCutoff)).isFalse();
        assertThat(scheduler.shouldSendReminder(
                userId, threshold.plusHours(1), threshold, cooldownCutoff)).isFalse();
        assertThat(scheduler.shouldSendReminder(
                userId, null, threshold, cooldownCutoff)).isFalse();
    }

    @Test
    void recentReminderPreventsDuplicateDelivery() {
        Long userId = 17L;
        LocalDateTime threshold = LocalDateTime.of(2026, 8, 17, 10, 0);
        Instant cooldownCutoff = Instant.parse("2026-08-19T10:00:00Z");
        when(notificationRepository.findByUserIdAndTypeAndCreatedAtAfter(
                userId, NotificationType.STUDY_REMINDER, cooldownCutoff))
                .thenReturn(List.of(new com.readyroad.readyroadbackend.domain.entity.Notification()));

        assertThat(scheduler.shouldSendReminder(
                userId, threshold.minusDays(1), threshold, cooldownCutoff)).isFalse();
    }
}
