package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.service.UserFavoriteRoadSignService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserFavoriteRoadSignControllerTest {
    UserFavoriteRoadSignService service = mock(UserFavoriteRoadSignService.class);
    UserFavoriteRoadSignController controller = new UserFavoriteRoadSignController(service);
    User user(long id) { User user = new User(); user.setId(id); return user; }

    @Test void listUsesAuthenticatedUserNotClientInput() {
        when(service.list(7L)).thenReturn(List.of(21L));
        assertThat(controller.list(user(7L), 7L)).containsExactly(21L);
        verify(service).list(7L);
    }
    @Test void differentUserCannotBeUsedAfterTokenSwitch() {
        assertThatThrownBy(() -> controller.add(user(8L), 7L, 21L))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(e -> assertThat(((ResponseStatusException)e).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verifyNoInteractions(service);
    }
    @Test void anonymousIsRejected() {
        assertThatThrownBy(() -> controller.list(null, null))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(e -> assertThat(((ResponseStatusException)e).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }
    @Test void invalidIdRejectedWithoutWrite() {
        assertThatThrownBy(() -> controller.add(user(7L), 7L, 0L))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(e -> assertThat(((ResponseStatusException)e).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verifyNoInteractions(service);
    }
    @Test void repeatedDeleteIsSafe() {
        assertThat(controller.remove(user(7L), 7L, 21L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).remove(7L,21L);
    }
}
