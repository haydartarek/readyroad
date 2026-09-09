package com.readyroad.readyroadbackend.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.service.LearningNotificationTransport;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

class LearningNotificationChannelControllerTest {
    JdbcTemplate jdbc = mock(JdbcTemplate.class);
    UserRepository users = mock(UserRepository.class);
    LearningNotificationTransport transport = mock(LearningNotificationTransport.class);
    LearningNotificationChannelController controller = new LearningNotificationChannelController(jdbc, users, transport);

    User learner() {
        User user = new User();
        user.setId(7L); user.setUsername("learner"); user.setRole(Role.USER);
        when(users.findByUsername("learner")).thenReturn(Optional.of(user));
        when(users.findById(7L)).thenReturn(Optional.of(user));
        ReflectionTestUtils.setField(controller, "enabled", true);
        return user;
    }
    @Test
    void writesPreferenceOnlyForTheAuthenticatedOwner() {
        User user = learner();
        controller.email(user, new LearningNotificationChannelController.EmailPreference(false));
        verify(jdbc).update(contains("INSERT INTO learning_notification_preferences"), eq(7L), eq(false));
    }
    @Test
    void rejectsAnonymousAccessWithoutQueryingSubscriptions() {
        assertThatThrownBy(() -> controller.settings(null)).isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode().value()).isEqualTo(401));
        verifyNoInteractions(jdbc);
    }
    @Test
    void adminCannotEnableLearnerDelivery() {
        User user = learner(); user.setRole(Role.ADMIN);
        assertThatThrownBy(() -> controller.email(user, new LearningNotificationChannelController.EmailPreference(true)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode().value()).isEqualTo(403));
        verifyNoInteractions(jdbc);
    }
}
