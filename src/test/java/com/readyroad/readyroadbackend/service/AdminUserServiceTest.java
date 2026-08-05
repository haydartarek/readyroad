package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.AdminCreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private BackendMessageService messages;

    @Test
    void createsValidatedUserWithRequestedPermissions() {
        when(passwordEncoder.encode("Strong#Pass1")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(42L);
            return user;
        });
        AdminUserService service = new AdminUserService(userRepository, passwordEncoder, messages);

        User user = service.createUser(request(), "admin");

        assertThat(user.getFullName()).isEqualTo("Ada Lovelace");
        assertThat(user.getRole()).isEqualTo(Role.MODERATOR);
        assertThat(user.getPreferredLanguage()).isEqualTo("nl");
        assertThat(user.getEmailVerified()).isTrue();
        assertThat(user.getPasswordHash()).isEqualTo("encoded");
    }

    @Test
    void rejectsDuplicateUsernameBeforeSaving() {
        when(userRepository.existsByUsernameIgnoreCase("ada_admin")).thenReturn(true);
        when(messages.get("auth.username_exists")).thenReturn("duplicate");
        AdminUserService service = new AdminUserService(userRepository, passwordEncoder, messages);

        assertThatThrownBy(() -> service.createUser(request(), "admin"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("duplicate");
    }

    private AdminCreateUserRequest request() {
        return new AdminCreateUserRequest(
                "Ada", "Lovelace", "Ada@example.com", "ada_admin",
                "Strong#Pass1", "nl", "MODERATOR", true, true);
    }
}
