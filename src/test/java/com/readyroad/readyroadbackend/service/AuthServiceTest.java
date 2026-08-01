package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.repository.AuthIdentityRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.dto.AuthResponse;
import com.readyroad.readyroadbackend.dto.RegisterRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthIdentityRepository authIdentityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BackendMessageService messages;

    @Mock
    private AdminSystemSettingsService adminSystemSettingsService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerTranslatesUniqueConstraintFailureToUsernameConflict() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(" learner ");
        request.setEmail("LEARNER@example.com");
        request.setFullName("Learner User");
        request.setPassword("Secret123!");

        when(userRepository.existsByUsernameIgnoreCase("learner")).thenReturn(false, true);
        when(userRepository.existsByEmailIgnoreCase("learner@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123!")).thenReturn("encoded-password");
        when(userRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint"));
        when(messages.get("auth.username_exists")).thenReturn("Username already exists.");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists.");

        verify(userRepository).save(any());
    }

    @Test
    void registerPersistsAndReturnsPreferredLanguage() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("learner");
        request.setEmail("learner@example.com");
        request.setFullName("Learner User");
        request.setPassword("Secret123!");
        request.setPreferredLanguage("nl");

        when(userRepository.existsByUsernameIgnoreCase("learner")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("learner@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(12L);
            return user;
        });
        when(authIdentityRepository.findByUserId(12L)).thenReturn(List.of());
        when(jwtService.generateToken(any(), any())).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getPreferredLanguage()).isEqualTo("nl");
        assertThat(response.getRole()).isEqualTo(Role.USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUsesAdminDefaultLanguageWhenRequestOmitsIt() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("learner");
        request.setEmail("learner@example.com");
        request.setFullName("Learner User");
        request.setPassword("Secret123!");

        when(adminSystemSettingsService.getDefaultLanguage()).thenReturn("fr");
        when(passwordEncoder.encode("Secret123!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(13L);
            return user;
        });
        when(authIdentityRepository.findByUserId(13L)).thenReturn(List.of());
        when(jwtService.generateToken(any(), any())).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getPreferredLanguage()).isEqualTo("fr");
    }
}
