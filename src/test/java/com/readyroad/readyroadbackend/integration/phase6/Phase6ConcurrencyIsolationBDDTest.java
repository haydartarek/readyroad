package com.readyroad.readyroadbackend.integration.phase6;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.config.TestDataSeederConfig;
import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Phase 6: Concurrency & Isolation - API-Based BDD Tests
 *
 * ✅ API-Contract Testing Only
 * ❌ No DTO imports (dto.*)
 * ✅ HTTP endpoints only
 * ✅ JSON path assertions
 *
 * Ensures:
 * - Two users can start exams concurrently without collision
 * - User isolation: cannot access other user's exam data
 * - Answers update only the correct exam instance
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestDataSeederConfig.class)
@TestPropertySource(properties = "spring.security.mode=dev")
// ❌ Removed @Transactional to allow cleanup to persist
@DisplayName("Phase 6: Concurrency & Isolation - BDD Tests")
public class Phase6ConcurrencyIsolationBDDTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizQuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TrafficSignRepository trafficSignRepository;

    @Autowired
    private ExamSimulationRepository examSimulationRepository;

    @Autowired
    private ExamSimulationQuestionRepository examSimulationQuestionRepository;

    @Autowired
    private ExamSimulationAnswerRepository examAnswerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private User userA;
    private User userB;
    private Category testCategory;
    private TrafficSign testSign;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        // ✅ Clean up any existing exams to prevent 409 conflicts (FK-safe order)
        examAnswerRepository.deleteAllInBatch();
        examSimulationQuestionRepository.deleteAllInBatch();
        examSimulationRepository.deleteAllInBatch();

        // ✅ Clean up existing users to prevent duplicate constraint violations
        userRepository.deleteAll();
        userRepository.flush();

        // Create users
        userA = createUser("usera", "usera@test.com");
        userB = createUser("userb", "userb@test.com");

        // ✅ Use existing seeded category or create with unique code
        var existingCategories = categoryRepository.findAll();
        if (!existingCategories.isEmpty()) {
            testCategory = existingCategories.get(0);
        } else {
            testCategory = new Category();
            testCategory.setCode("TRAFFIC_" + System.currentTimeMillis());
            testCategory.setNameEn("Traffic Rules");
            testCategory.setNameAr("قواعد المرور");
            testCategory.setNameNl("Verkeersregels");
            testCategory.setNameFr("Règles de circulation");
            testCategory.setIsActive(true);
            testCategory.setDisplayOrder(1);
            testCategory = categoryRepository.save(testCategory);
        }

        // Create test traffic sign
        testSign = new TrafficSign();
        testSign.setSignCode("A10");
        testSign.setNameEn("Stop Sign");
        testSign.setNameAr("إشارة قف");
        testSign.setNameNl("Stopbord");
        testSign.setNameFr("Panneau d'arrêt");
        testSign.setCategory(testCategory);
        testSign.setIsActive(true);
        testSign = trafficSignRepository.save(testSign);

        // TestDataSeederConfig has already seeded 60 published questions
        // Verify they exist
        long questionCount = questionRepository.count();
        if (questionCount < 50) {
            throw new IllegalStateException("Test data seeder should have created at least 50 questions");
        }
    }

    // ==========================================
    // Scenario: Two users can start exams concurrently without examId collision
    // ==========================================

    @Test
    @DisplayName("Two users start exams concurrently without ID collision")
    void twoUsersStartExamsConcurrentlyWithoutCollision() throws Exception {
        // When: User A and User B start exams concurrently
        CompletableFuture<MvcResult> futureA = CompletableFuture.supplyAsync(() -> {
            try {
                return mockMvc.perform(post("/api/exams/simulations/start")
                        .param("userId", userA.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<MvcResult> futureB = CompletableFuture.supplyAsync(() -> {
            try {
                return mockMvc.perform(post("/api/exams/simulations/start")
                        .param("userId", userB.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Wait for both to complete
        MvcResult resultA = futureA.get();
        MvcResult resultB = futureB.get();

        // Parse JSON responses without DTO mapping
        JsonNode responseA = objectMapper.readTree(resultA.getResponse().getContentAsString());
        JsonNode responseB = objectMapper.readTree(resultB.getResponse().getContentAsString());

        // Then: Both should have unique examIds
        Long examIdA = responseA.path("examId").asLong();
        Long examIdB = responseB.path("examId").asLong();

        assertThat(examIdA)
            .as("User A should have valid examId")
            .isNotNull()
            .isPositive();

        assertThat(examIdB)
            .as("User B should have valid examId")
            .isNotNull()
            .isPositive();

        assertThat(examIdA)
            .as("User A and B should have different examIds")
            .isNotEqualTo(examIdB);
    }

    // ==========================================
    // Scenario: User isolation - cannot access other user's exam results
    // ==========================================

    @Test
    @DisplayName("User isolation - cannot access other user's exam results")
    void userCannotAccessOtherUsersExamResults() throws Exception {
        // Given: User A and B have started exams
        MvcResult resultA = mockMvc.perform(post("/api/exams/simulations/start")
                .param("userId", userA.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        MvcResult resultB = mockMvc.perform(post("/api/exams/simulations/start")
                .param("userId", userB.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        // Parse JSON responses without DTO mapping
        JsonNode responseA = objectMapper.readTree(resultA.getResponse().getContentAsString());
        JsonNode responseB = objectMapper.readTree(resultB.getResponse().getContentAsString());

        Long examIdA = responseA.path("examId").asLong();
        Long examIdB = responseB.path("examId").asLong();

        // When: User A tries to access User B's exam results
        // Note: In dev mode, userId is fallback to 1, so we simulate by checking DB isolation
        mockMvc.perform(get("/api/exams/simulations/" + examIdB + "/results")
                .param("userId", userA.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError()); // Should be 403 or 404

        // When: User B tries to access User A's exam results
        mockMvc.perform(get("/api/exams/simulations/" + examIdA + "/results")
                .param("userId", userB.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError()); // Should be 403 or 404
    }

    // ==========================================
    // Scenario: Exam answers update only the correct exam instance
    // ==========================================

    @Test
    @DisplayName("Answers update only the correct exam instance")
    void answersUpdateOnlyCorrectExamInstance() throws Exception {
        // Given: User A and B have started exams
        MvcResult resultA = mockMvc.perform(post("/api/exams/simulations/start")
                .param("userId", userA.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        MvcResult resultB = mockMvc.perform(post("/api/exams/simulations/start")
                .param("userId", userB.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

        // Parse JSON responses without DTO mapping
        JsonNode responseA = objectMapper.readTree(resultA.getResponse().getContentAsString());
        JsonNode responseB = objectMapper.readTree(resultB.getResponse().getContentAsString());

        Long examIdA = responseA.path("examId").asLong();
        Long examIdB = responseB.path("examId").asLong();

        // Extract question and option IDs from JSON
        JsonNode firstQuestionA = responseA.path("questions").get(0);
        Long questionIdA = firstQuestionA.path("questionId").asLong();
        Long optionIdA = firstQuestionA.path("options").get(0).path("optionId").asLong();

        // When: User A submits 1 answer for their exam
        mockMvc.perform(post("/api/exams/simulations/" + examIdA + "/questions/" + questionIdA + "/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"selectedOptionId\":%d}", optionIdA)))
            .andExpect(status().isOk());

        // Then: User A exam should record 1 answer
        long answersForExamA = examAnswerRepository.findAll().stream()
            .filter(a -> a.getExam().getId().equals(examIdA))
            .count();

        assertThat(answersForExamA)
            .as("User A exam should have 1 answer")
            .isEqualTo(1);

        // And: User B exam should record 0 answers
        long answersForExamB = examAnswerRepository.findAll().stream()
            .filter(a -> a.getExam().getId().equals(examIdB))
            .count();

        assertThat(answersForExamB)
            .as("User B exam should have 0 answers")
            .isEqualTo(0);
    }

    // ==========================================
    // Helper Methods
    // ==========================================

    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setFullName("Test User " + username);
        user.setRole(Role.USER);
        user.setIsActive(true);
        user.setIsLocked(false);
        return userRepository.save(user);
    }
}
