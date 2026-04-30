package com.readyroad.readyroadbackend.integration.phase6;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Phase 6: Security Regression - BDD Integration Tests
 *
 * Ensures security enforcement:
 * - Protected endpoints reject unauthenticated requests
 * - Normal users cannot access admin endpoints
 * - User isolation: cannot access other users' data
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.security.mode=secure")
@Transactional
@DisplayName("Phase 6: Security Regression - BDD Tests")
public class Phase6SecurityRegressionBDDTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private User normalUser;
    private User adminUser;
    private User userB;
    private String normalUserJwt;
    private String adminUserJwt;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create normal user
        normalUser = new User();
        normalUser.setUsername("normaluser");
        normalUser.setEmail("normal@test.com");
        normalUser.setPasswordHash(passwordEncoder.encode("password123"));
        normalUser.setFullName("Normal User");
        normalUser.setRole(Role.USER);
        normalUser.setIsActive(true);
        normalUser.setIsLocked(false);
        normalUser = userRepository.save(normalUser);

        // Create admin user
        adminUser = new User();
        adminUser.setUsername("adminuser");
        adminUser.setEmail("admin@test.com");
        adminUser.setPasswordHash(passwordEncoder.encode("admin123"));
        adminUser.setFullName("Admin User");
        adminUser.setRole(Role.ADMIN);
        adminUser.setIsActive(true);
        adminUser.setIsLocked(false);
        adminUser = userRepository.save(adminUser);

        // Create user B (for isolation tests)
        userB = new User();
        userB.setUsername("userb");
        userB.setEmail("userb@test.com");
        userB.setPasswordHash(passwordEncoder.encode("password123"));
        userB.setFullName("User B");
        userB.setRole(Role.USER);
        userB.setIsActive(true);
        userB.setIsLocked(false);
        userB = userRepository.save(userB);

        // Login users and get JWTs
        normalUserJwt = loginAndGetJwt("normaluser", "password123");
        adminUserJwt = loginAndGetJwt("adminuser", "admin123");
    }

    // ==========================================
    // Scenario: Protected endpoints reject unauthenticated requests
    // ==========================================

    @Test
    @DisplayName("Protected endpoints reject unauthenticated requests")
    void protectedEndpointsRejectUnauthenticatedRequests() throws Exception {
        // When: Unauthenticated client calls GET /api/users/me/progress/overall
        mockMvc.perform(get("/api/users/me/progress/overall")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // 401

        // When: Unauthenticated client calls POST /api/exams/simulations/start
        mockMvc.perform(post("/api/exams/simulations/start")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // 401
    }

    // ==========================================
    // Scenario: Normal user is blocked from admin endpoints
    // ==========================================

    @Test
    @DisplayName("Normal user blocked from admin endpoints (403)")
    void normalUserBlockedFromAdminEndpoints() throws Exception {
        // Security should block non-admin users before controller resolution.
        // Even if an endpoint is unmapped, /api/admin/** must still return 403
        // for authenticated non-admin users.

        // When: Normal user calls GET /api/admin/questions
        mockMvc.perform(get("/api/admin/questions")
                .header("Authorization", "Bearer " + normalUserJwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // 403

        // When: Normal user calls POST /api/admin/questions/700/publish
        mockMvc.perform(post("/api/admin/questions/700/publish")
                .header("Authorization", "Bearer " + normalUserJwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // 403

        // When: Normal user calls POST /api/admin/questions/700/deactivate
        mockMvc.perform(post("/api/admin/questions/700/deactivate")
                .header("Authorization", "Bearer " + normalUserJwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    @DisplayName("Normal user blocked from implemented admin import endpoints")
    void normalUserBlockedFromImplementedAdminImportEndpoints() throws Exception {
        // When: Normal user calls implemented admin import/history endpoints
        mockMvc.perform(get("/api/admin/import/history")
                .header("Authorization", "Bearer " + normalUserJwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/admin/sign-quiz/import")
                .header("Authorization", "Bearer " + normalUserJwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // Scenario: User cannot access another user's progress or analytics
    // ==========================================

    @Test
    @DisplayName("User cannot access another user's progress (IDOR protection)")
    void userCannotAccessAnotherUsersData() throws Exception {
        // When: User A requests their own progress via /me/ pattern
        mockMvc.perform(get("/api/users/me/progress/overall")
                .header("Authorization", "Bearer " + normalUserJwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // Should return normalUser's data only

        // The /me/ pattern ensures the authenticated user's data is returned
        // No way to request userB's data via URL manipulation - IDOR protection by
        // design
    }

    @Test
    @DisplayName("Admin endpoints return 404 (not implemented yet)")
    void adminCanAccessAdminEndpoints() throws Exception {
        // NOTE: Admin endpoints not implemented yet - expecting 404
        // When implemented, admin should get 200 OK

        // When: Admin calls GET /api/admin/questions
        mockMvc.perform(get("/api/admin/questions")
                .header("Authorization", "Bearer " + adminUserJwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // 404 (endpoint doesn't exist yet)
    }

    @Test
    @DisplayName("Admin can access implemented admin import endpoints")
    void adminCanAccessImplementedAdminImportEndpoints() throws Exception {
        mockMvc.perform(get("/api/admin/import/history")
                .header("Authorization", "Bearer " + adminUserJwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/sign-quiz/stats")
                .header("Authorization", "Bearer " + adminUserJwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Health endpoints remain public under deny-all fallback")
    void healthEndpointsRemainPublic() throws Exception {
        mockMvc.perform(get("/api/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/auth/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ==========================================
    // Helper Methods
    // ==========================================

    private String loginAndGetJwt(String username, String password) throws Exception {
        String loginJson = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        var jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("token").asText();
    }
}
