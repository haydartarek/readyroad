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
}
