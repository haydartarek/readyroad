package com.readyroad.readyroadbackend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.repository.AuthIdentityRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.LoginRequest;
import com.readyroad.readyroadbackend.dto.RegisterRequest;
import com.readyroad.readyroadbackend.dto.ResetPasswordRequest;
import com.readyroad.readyroadbackend.service.AdminSystemSettingsService;
import com.readyroad.readyroadbackend.service.AuthenticationTokenService;
import com.readyroad.readyroadbackend.service.AuthService;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import com.readyroad.readyroadbackend.service.PasswordResetService;
import com.readyroad.readyroadbackend.service.SocialAuthService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.junit.jupiter.api.AfterEach;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthIdentityRepository authIdentityRepository;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private AdminSystemSettingsService adminSystemSettingsService;

    @Mock
    private SocialAuthService socialAuthService;

    @Mock
    private AuthenticationTokenService authenticationTokenService;

    @Mock
    private BackendMessageService messages;

    @InjectMocks
    private AuthController authController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void logoutRevokesTheCurrentAuthenticatedToken() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null, java.util.List.of()));

        ResponseEntity<Void> response = authController.logout("Bearer admin-token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        org.mockito.Mockito.verify(authenticationTokenService).revoke("admin-token", "admin");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void loginReturnsUnauthorizedForAuthenticationFailures() {
        LoginRequest request = new LoginRequest();
        request.setUsername("learner@example.com");
        request.setPassword("wrong-password");

        when(authService.login(request)).thenThrow(new BadCredentialsException("bad credentials"));
        when(messages.get("auth.login.invalid_credentials")).thenReturn("Invalid credentials");

        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo(Map.of(
                "error", "Invalid credentials",
                "message", "Invalid credentials"));
    }

    @Test
    void loginPropagatesUnexpectedRuntimeExceptions() {
        LoginRequest request = new LoginRequest();
        request.setUsername("learner@example.com");
        request.setPassword("correct-password");

        when(authService.login(request)).thenThrow(new IllegalStateException("database offline"));

        assertThatThrownBy(() -> authController.login(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("database offline");
    }

    @Test
    void registerReturnsForbiddenPayloadWhenRegistrationsAreDisabled() {
        RegisterRequest request = new RegisterRequest();
        when(adminSystemSettingsService.areRegistrationsAllowed()).thenReturn(false);
        when(messages.get("auth.register.disabled")).thenReturn("Registration is disabled");

        ResponseEntity<?> response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(Map.of(
                "error", "Registration is disabled",
                "message", "Registration is disabled"));
    }

    @Test
    void resetPasswordReturnsBadRequestPayloadWithMessageAndError() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("expired-token");
        request.setNewPassword("NewPassword123!");
        doThrow(new IllegalArgumentException("Reset token expired"))
                .when(passwordResetService)
                .resetPassword(request.getToken(), request.getNewPassword());

        ResponseEntity<Map<String, String>> response = authController.resetPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(Map.of(
                "error", "Reset token expired",
                "message", "Reset token expired"));
    }
}
