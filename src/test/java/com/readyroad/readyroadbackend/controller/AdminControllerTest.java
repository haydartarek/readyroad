package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamResultRepository;
import com.readyroad.readyroadbackend.domain.repository.SignPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignRandomPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.service.AdminQuizService;
import com.readyroad.readyroadbackend.service.AdminUserService;
import com.readyroad.readyroadbackend.service.AdminSystemSettingsService;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import com.readyroad.readyroadbackend.service.FileUploadService;
import com.readyroad.readyroadbackend.service.NotificationService;
import com.readyroad.readyroadbackend.service.SignGovernanceService;
import com.readyroad.readyroadbackend.service.TrafficSignService;
import java.util.Map;
import java.util.Optional;
import java.util.Arrays;
import java.security.Principal;
import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private RoadSignRepository signRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExamSimulationRepository examSimulationRepository;

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private SignQuestionRepository signQuestionRepository;

    @Mock
    private SignPracticeSessionRepository signPracticeSessionRepository;

    @Mock
    private SignExamResultRepository signExamResultRepository;

    @Mock
    private SignRandomPracticeSessionRepository signRandomPracticeSessionRepository;

    @Mock
    private UserCategoryProgressRepository categoryProgressRepository;

    @Mock
    private TrafficSignService trafficSignService;

    @Mock
    private AdminQuizService adminQuizService;

    @Mock
    private AdminUserService adminUserService;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private SignGovernanceService signGovernanceService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AdminSystemSettingsService adminSystemSettingsService;

    @Mock
    private BackendMessageService messages;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private Principal principal;

    @InjectMocks
    private AdminController adminController;

    @Test
    void exposesNoAnswerShuffleEndpoint() {
        boolean shuffleEndpointExists = Arrays.stream(AdminController.class.getDeclaredMethods())
                .map(method -> method.getAnnotation(PostMapping.class))
                .filter(annotation -> annotation != null)
                .flatMap(annotation -> Arrays.stream(annotation.value()))
                .anyMatch(path -> path.contains("shuffle-answer-order"));

        assertThat(shuffleEndpointExists).isFalse();
    }

    @Test
    void healthReportsActualDatabaseConnectivity() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(true);

        var response = adminController.getSystemHealth();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) response.getBody()).get("database")).isEqualTo("Connected");
    }

    @Test
    void healthReturnsServiceUnavailableWhenDatabaseCannotBeReached() throws Exception {
        when(dataSource.getConnection()).thenThrow(new java.sql.SQLException("offline"));

        var response = adminController.getSystemHealth();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) response.getBody()).get("status")).isEqualTo("DOWN");
    }

    @Test
    void updateUserRoleThrowsBadRequestWhenRoleIsMissing() {
        when(messages.get("admin.user.role_required")).thenReturn("Role is required");

        assertThatThrownBy(() -> adminController.updateUserRole(5L, Map.of(), principal))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getReason()).isEqualTo("Role is required");
                });
    }

    @Test
    void updateUserRoleThrowsNotFoundWhenUserIsMissing() {
        when(messages.get("auth.user_not_found")).thenReturn("User not found");
        when(userRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminController.updateUserRole(7L, Map.of("role", "ADMIN"), principal))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).isEqualTo("User not found");
                });
    }

    @Test
    void toggleUserLockThrowsBadRequestWhenFlagIsMissing() {
        when(messages.get("admin.user.lock_required")).thenReturn("Lock flag is required");

        assertThatThrownBy(() -> adminController.toggleUserLock(8L, Map.of(), principal))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ex.getReason()).isEqualTo("Lock flag is required");
                });
    }

    @Test
    void toggleUserLockThrowsNotFoundWhenUserIsMissing() {
        when(messages.get("auth.user_not_found")).thenReturn("User not found");
        when(userRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminController.toggleUserLock(9L, Map.of("isLocked", true), principal))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> {
                    assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(ex.getReason()).isEqualTo("User not found");
                });
    }

    @Test
    void updateUserRoleStillUpdatesExistingUser() {
        User user = new User();
        user.setId(11L);
        user.setUsername("moderator1");
        user.setEmail("moderator@example.com");
        user.setRole(Role.USER);
        when(userRepository.findById(11L)).thenReturn(Optional.of(user));
        when(messages.get("admin.user.role_updated")).thenReturn("Role updated");

        var response = adminController.updateUserRole(11L, Map.of("role", "MODERATOR"), principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(user.getRole()).isEqualTo(Role.MODERATOR);
    }

    @Test
    void updateUserRoleRejectsSelfDemotion() {
        User admin = user(20L, "admin", Role.ADMIN);
        when(principal.getName()).thenReturn("admin");
        when(userRepository.findById(20L)).thenReturn(Optional.of(admin));
        when(messages.get("admin.user.self_role_change_forbidden")).thenReturn("Self demotion forbidden");

        assertThatThrownBy(() -> adminController.updateUserRole(20L, Map.of("role", "USER"), principal))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void updateUserRoleRejectsRemovingLastAdmin() {
        User admin = user(21L, "other-admin", Role.ADMIN);
        when(principal.getName()).thenReturn("admin");
        when(userRepository.findById(21L)).thenReturn(Optional.of(admin));
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L);
        when(messages.get("admin.user.last_admin_required")).thenReturn("Last admin required");

        assertThatThrownBy(() -> adminController.updateUserRole(21L, Map.of("role", "USER"), principal))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void toggleUserLockRejectsLockingCurrentAdmin() {
        User admin = user(22L, "admin", Role.ADMIN);
        when(principal.getName()).thenReturn("admin");
        when(userRepository.findById(22L)).thenReturn(Optional.of(admin));
        when(messages.get("admin.user.self_lock_forbidden")).thenReturn("Self lock forbidden");

        assertThatThrownBy(() -> adminController.toggleUserLock(22L, Map.of("isLocked", true), principal))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex ->
                        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void getUserByIdReturnsUnifiedNotFoundPayloadWhenUserIsMissing() {
        when(messages.get("auth.user_not_found")).thenReturn("User not found");
        when(userRepository.findById(15L)).thenReturn(Optional.empty());

        var response = adminController.getUserById(15L);
        @SuppressWarnings("unchecked")
        var body = (Map<String, Object>) response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body).isEqualTo(Map.of(
                "error", "User not found",
                "message", "User not found",
                "timestamp", body.get("timestamp")));
    }

    private User user(Long id, String username, Role role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setFullName(username);
        user.setRole(role);
        user.setIsActive(true);
        user.setIsLocked(false);
        return user;
    }
}
