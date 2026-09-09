package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.readyroad.readyroadbackend.domain.entity.Notification;
import com.readyroad.readyroadbackend.domain.repository.NotificationRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock private LearningNotificationOutbox outbox;

    @Test
    void defersPersistenceToDurableOutboxWhenEnabled() {
        org.springframework.test.util.ReflectionTestUtils.setField(notificationService, "outboxEnabled", true);
        notificationService.createExamPassedNotification(17L, 12L, 41, 50);
        verify(outbox).enqueue(org.mockito.ArgumentMatchers.argThat(notification ->
                notification.getUserId() == 17L && notification.getLink().equals("/exam/results/12")));
        org.mockito.Mockito.verifyNoInteractions(notificationRepository);
    }

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private NotificationService notificationService;

    @Test
    void weakAreaNotificationStoresAllLocalizedCategoryNames() {
        notificationService.createWeakAreaNotification(
                17L,
                "Information signs",
                "علامات المعلومات",
                "Informatieborden",
                "Signaux d'information");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        assertThat(captor.getValue().getMessageParams())
                .contains("\"categoryEn\":\"Information signs\"")
                .contains("\"categoryAr\":\"علامات المعلومات\"")
                .contains("\"categoryNl\":\"Informatieborden\"")
                .contains("\"categoryFr\":\"Signaux d'information\"");
    }

    @Test
    void lessonNotificationStoresEveryLocalizedLessonTitle() {
        notificationService.createLessonProgressNotification(
                17L,
                "Priority rules",
                "قواعد الأولوية",
                "Voorrangsregels",
                "Règles de priorité");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        assertThat(captor.getValue().getMessageParams())
                .contains("\"lessonEn\":\"Priority rules\"")
                .contains("\"lessonAr\":\"قواعد الأولوية\"")
                .contains("\"lessonNl\":\"Voorrangsregels\"")
                .contains("\"lessonFr\":\"Règles de priorité\"");
    }

    @Test
    void localizedNamesRemainValidJsonWithNewlinesAndControlCharacters() throws Exception {
        notificationService.createWeakAreaNotification(17L, "Priority\nRules", "A\tB", "A\rB", "A\"B");
        var captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        var params = new com.fasterxml.jackson.databind.ObjectMapper().readTree(captor.getValue().getMessageParams());
        assertThat(params.path("categoryEn").asText()).isEqualTo("Priority\nRules");
        assertThat(params.path("categoryFr").asText()).isEqualTo("A\"B");
    }
}
