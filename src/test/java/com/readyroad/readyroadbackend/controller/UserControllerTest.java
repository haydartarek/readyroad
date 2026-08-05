package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.repository.AuthIdentityRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.UpdateUserProfileRequest;
import com.readyroad.readyroadbackend.dto.UpdatePreferredLanguageRequest;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import com.readyroad.readyroadbackend.service.SocialAuthService;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import java.util.Optional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthIdentityRepository authIdentityRepository;

    @Mock
    private AuthenticationUtil authenticationUtil;

    @Mock
    private SocialAuthService socialAuthService;

    @Mock
    private BackendMessageService messages;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @Test
    void getCurrentUserProfileThrowsNotFoundStatusWhenUserIsMissing() {
        when(authenticationUtil.extractUserId(authentication)).thenReturn(7L);
        when(userRepository.findById(7L)).thenReturn(Optional.empty());
        when(messages.get("auth.user_not_found")).thenReturn("User not found");

        assertThatThrownBy(() -> userController.getCurrentUserProfile(authentication))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).isEqualTo("User not found");
                });
    }

    @Test
    void updateCurrentUserProfileThrowsConflictStatusWhenEmailAlreadyExists() {
        com.readyroad.readyroadbackend.domain.entity.User user = new com.readyroad.readyroadbackend.domain.entity.User();
        user.setId(7L);
        user.setEmail("current@example.com");
        user.setFullName("Current User");

        when(authenticationUtil.extractUserId(authentication)).thenReturn(7L);
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCase("taken@example.com")).thenReturn(true);
        when(messages.get("user.email_in_use")).thenReturn("Email already in use");

        UpdateUserProfileRequest request = new UpdateUserProfileRequest("Updated User", "taken@example.com");

        assertThatThrownBy(() -> userController.updateCurrentUserProfile(authentication, request))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason()).isEqualTo("Email already in use");
                });
    }

    @Test
    void deleteCurrentUserThrowsNotFoundStatusWhenUserIsMissing() {
        when(authenticationUtil.extractUserId(authentication)).thenReturn(9L);
        when(userRepository.findById(9L)).thenReturn(Optional.empty());
        when(messages.get("auth.user_not_found")).thenReturn("User not found");

        assertThatThrownBy(() -> userController.deleteCurrentUser(authentication))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).isEqualTo("User not found");
                });
    }

    @Test
    void deleteCurrentUserRejectsAdministratorAndModeratorRoles() {
        for (com.readyroad.readyroadbackend.domain.enums.Role role : List.of(
                com.readyroad.readyroadbackend.domain.enums.Role.ADMIN,
                com.readyroad.readyroadbackend.domain.enums.Role.MODERATOR)) {
            com.readyroad.readyroadbackend.domain.entity.User user =
                    new com.readyroad.readyroadbackend.domain.entity.User();
            user.setId(9L);
            user.setRole(role);
            when(authenticationUtil.extractUserId(authentication)).thenReturn(9L);
            when(userRepository.findById(9L)).thenReturn(Optional.of(user));
            when(messages.get("user.role_account_deletion_forbidden"))
                    .thenReturn("Protected role");

            assertThatThrownBy(() -> userController.deleteCurrentUser(authentication))
                    .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
        }
    }

    @Test
    void updatePreferredLanguagePersistsExplicitChoice() {
        com.readyroad.readyroadbackend.domain.entity.User user =
                new com.readyroad.readyroadbackend.domain.entity.User();
        user.setId(7L);
        user.setUsername("learner");
        user.setEmail("learner@example.com");
        user.setFullName("Learner User");
        user.setRole(com.readyroad.readyroadbackend.domain.enums.Role.USER);
        user.setIsActive(true);

        when(authenticationUtil.extractUserId(authentication)).thenReturn(7L);
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(authIdentityRepository.findByUserId(7L)).thenReturn(List.of());

        var response = userController.updatePreferredLanguage(
                authentication,
                new UpdatePreferredLanguageRequest("ar"));

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPreferredLanguage()).isEqualTo("ar");
        assertThat(user.getPreferredLanguage()).isEqualTo("ar");
        verify(userRepository).save(user);
    }
}
