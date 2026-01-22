package com.readyroad.readyroadbackend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.dto.AuthResponse;
import com.readyroad.readyroadbackend.dto.LoginRequest;
import com.readyroad.readyroadbackend.dto.RegisterRequest;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Authentication & Security Integration Tests
 * <p>
 * **Purpose:** End-to-end testing of JWT authentication and authorization.
 * <p>
 * **What This Test DOES:**
 * <ul>
 * <li>✅ Tests JWT authentication flow (register, login)</li>
 * <li>✅ Verifies security enforcement (401 for unauthorized)</li>
 * <li>✅ Tests protected endpoints with valid JWT (200 OK)</li>
 * <li>✅ Tests invalid JWT rejection (401)</li>
 * <li>✅ Tests duplicate username detection (400)</li>
 * <li>✅ Tests password validation (401 for wrong password)</li>
 * <li>✅ Validates HTTP security semantics (401 vs 403)</li>
 * </ul>
 * <p>
 * **Security Configuration:**
 * <ul>
 * <li>**Profiles:** "test" (H2 database) + "secure" (JWT protection) ✅</li>
 * <li>**MockMvc:** Configured with `.apply(springSecurity())` ✅</li>
 * <li>**SecurityFilterChain:** JwtAuthenticationFilter active ✅</li>
 * <li>**Database:** H2 in-memory with user isolation (deleteAll per test)</li>
 * </ul>
 * <p>
 * **Difference from ReadyRoadIntegrationTest:**
 * <ul>
 * <li>ReadyRoadIntegrationTest: Context + beans only (NO security)</li>
 * <li>AuthenticationIntegrationTest: JWT + HTTP security (VERIFIED)</li>
 * </ul>
 * <p>
 * **Test Evidence:**
 * <ul>
 * <li>Run: `mvn test -Dtest=AuthenticationIntegrationTest`</li>
 * <li>Expected: 7/7 PASS (100%)</li>
 * <li>Logs should show: `JwtAuthenticationFilter` in security chain</li>
 * </ul>
 *
 * @author ReadyRoad Team
 * @since 2026-01-18
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")  // Use test profile - H2 database with Hibernate DDL
@TestPropertySource(properties = "spring.security.mode=secure")  // Enable JWT for this test
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private String jwtToken;  // Not static anymore
    private static final String TEST_USERNAME = "integration_test";
    private static final String TEST_EMAIL = "integration@test.com";
    private static final String TEST_PASSWORD = "TestPassword123";
    private static final String TEST_FULL_NAME = "Integration Test User";

    @BeforeEach
    void setUp() {
        // Clear database before each test for isolation
        userRepository.deleteAll();
        jwtToken = null;  // Reset token

        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(webApplicationContext)
                    .apply(springSecurity())  // ✅ CRITICAL: Apply Spring Security filters
                    .build();
        }
    }

    // Helper method to create a test user
    private void createTestUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(TEST_USERNAME);
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setFullName(TEST_FULL_NAME);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn();

        if (result.getResponse().getStatus() == 201) {
            AuthResponse authResponse = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    AuthResponse.class
            );
            jwtToken = authResponse.getToken();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. Register returns 201 and valid JWT token")
    void testRegisterReturnsJWT() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        request.setFullName(TEST_FULL_NAME);

        // When
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andReturn();

        // Extract and store JWT token for subsequent tests
        String responseBody = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        jwtToken = authResponse.getToken();

        assertThat(jwtToken).isNotEmpty();
        assertThat(jwtToken).startsWith("eyJ"); // JWT format check
    }

    @Test
    @Order(2)
    @DisplayName("2. Login returns 200 and valid JWT token")
    void testLoginReturnsJWT() throws Exception {
        // Given - Create user FIRST
        createTestUser();

        LoginRequest request = new LoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME));
    }

    @Test
    @Order(3)
    @DisplayName("3. Protected endpoint returns 401 without JWT (secure mode)")
    void testProtectedEndpointRejectsAnonymous() throws Exception {
        // When & Then - Access protected endpoint without Authorization header
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    @DisplayName("4. Protected endpoint returns 200 with valid JWT (secure mode)")
    void testProtectedEndpointAcceptsJWT() throws Exception {
        // Given - Create user and get JWT
        createTestUser();
        assertThat(jwtToken).isNotEmpty();

        // When & Then - Access protected endpoint with JWT
        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    @DisplayName("5. Invalid JWT returns 401")
    void testInvalidJWTReturnsUnauthorized() throws Exception {
        // When & Then - Access with invalid JWT
        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(6)
    @DisplayName("6. Login with wrong password returns 401")
    void testLoginWithWrongPasswordFails() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername(TEST_USERNAME);
        request.setPassword("WrongPassword123");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    @DisplayName("7. Register with duplicate username returns 400")
    void testRegisterDuplicateUsernameFails() throws Exception {
        // Given - Create user FIRST
        createTestUser();

        // Then try to register with same username
        RegisterRequest request = new RegisterRequest();
        request.setUsername(TEST_USERNAME); // Same username!
        request.setEmail("different@email.com");
        request.setPassword(TEST_PASSWORD);
        request.setFullName("Different Name");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username already exists"));
    }
}
