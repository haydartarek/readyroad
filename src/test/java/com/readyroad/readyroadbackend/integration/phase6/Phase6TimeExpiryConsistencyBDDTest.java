package com.readyroad.readyroadbackend.integration.phase6;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Phase 6 Test Pack: Time Expiry Consistency (API-Based)
 *
 * ✅ API-Contract Testing Only
 * ❌ No DTO imports (dto.*)
 * ❌ No Service imports
 * ✅ HTTP endpoints only
 * ✅ JSON assertions
 *
 * Scenarios:
 * - Answer submission after expiry is rejected consistently via API
 * - Expired exam results remain accessible via API
 * - Time-based validation is enforced at API boundary
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.security.mode=dev")
@Transactional
@DisplayName("Phase 6: Time Expiry Consistency (API-Based)")
public class Phase6TimeExpiryConsistencyBDDTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExamSimulationRepository examSimulationRepository;

    @Autowired
    private ExamSimulationQuestionRepository examSimulationQuestionRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizAnswerOptionRepository optionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TrafficSignRepository trafficSignRepository;

    private MockMvc mockMvc;
    private Long testUserId;
    private Category testCategory;
    private TrafficSign testSign;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        testUserId = 500L;

        // Create category
        testCategory = new Category();
        testCategory.setCode("EXPIRY");
        testCategory.setNameEn("Expiry Test Category");
        testCategory.setNameAr("فئة اختبار الانتهاء");
        testCategory.setNameNl("Vervaltestcategorie");
        testCategory.setNameFr("Catégorie de test d'expiration");
        testCategory.setIsActive(true);
        testCategory = categoryRepository.save(testCategory);

        // Create traffic sign
        testSign = new TrafficSign();
        testSign.setSignCode("EXP01");
        testSign.setNameEn("Expiry Test Sign");
        testSign.setNameAr("علامة اختبار الانتهاء");
        testSign.setNameNl("Verval testteken");
        testSign.setNameFr("Signe de test d'expiration");
        testSign.setCategory(testCategory);
        testSign.setIsActive(true);
        testSign = trafficSignRepository.save(testSign);

        // Create 51 compliant questions
        for (int i = 1; i <= 51; i++) {
            QuizQuestion question = new QuizQuestion();
            question.setQuestionEn("Expiry test question " + i);
            question.setQuestionAr("سؤال اختبار الانتهاء " + i);
            question.setQuestionNl("Verval testvraag " + i);
            question.setQuestionFr("Question de test d'expiration " + i);
            question.setDifficultyLevel(QuizQuestion.DifficultyLevel.MEDIUM);
            question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
            question.setCategory(testCategory);
            question.setTrafficSign(testSign);
            question.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
            question.setPublishedAt(LocalDateTime.now().minusDays(1));
            question.setIsActive(true);

            // ✅ Add 2 options BEFORE saving (Belgian validation requires 2-3 options)
            QuizAnswerOption option1 = new QuizAnswerOption();
            option1.setQuestion(question);
            option1.setOptionTextEn("Option A");
            option1.setOptionTextAr("الخيار أ");
            option1.setOptionTextNl("Optie A");
            option1.setOptionTextFr("Option A");
            option1.setIsCorrect(true);
            option1.setDisplayOrder(1);

            QuizAnswerOption option2 = new QuizAnswerOption();
            option2.setQuestion(question);
            option2.setOptionTextEn("Option B");
            option2.setOptionTextAr("الخيار ب");
            option2.setOptionTextNl("Optie B");
            option2.setOptionTextFr("Option B");
            option2.setIsCorrect(false);
            option2.setDisplayOrder(2);

            // Add options to question collection before saving
            question.getOptions().add(option1);
            question.getOptions().add(option2);

            // Now save question with options (validation will pass)
            question = quizQuestionRepository.save(question);
        }
    }

    @Test
    @Disabled("Phase 6 - API integration: Requires REST controller implementation")
    @DisplayName("Scenario: Submitting an answer after exam expiry is rejected via API")
    void testAnswerSubmissionAfterExpiryRejectedViaAPI() throws Exception {
        // Given: a user exists with valid JWT
        // And the user has an exam that is expired
        ExamSimulation exam = new ExamSimulation();
        exam.setUserId(testUserId);
        exam.setStartedAt(Instant.now().minus(Duration.ofHours(1)));
        exam.setExpiresAt(Instant.now().minus(Duration.ofMinutes(5))); // Expired 5 minutes ago
        exam.setTotalQuestions(50);
        exam.setStatus(ExamSimulation.ExamStatus.IN_PROGRESS);
        exam = examSimulationRepository.save(exam);

        Long examId = exam.getId();
        Long questionId = 1L; // Any question ID

        // When: the user submits an answer via the exam answer API
        String requestBody = """
                {
                    "selectedOptionId": 1
                }
                """;

        // Then: the response status should be 409/410
        MvcResult result = mockMvc.perform(post("/api/exams/simulations/" + examId + "/questions/" + questionId + "/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError()) // 409 or 410
                .andReturn();

        // And: the response body should mention expiration
        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody.toLowerCase())
                .as("Error message should mention expiration")
                .contains("expir");
    }

    @Test
    @Disabled("Phase 6 - API integration: Requires REST controller implementation with proper exam status handling")
    @DisplayName("Scenario: Expired exam results remain accessible via API")
    void testExpiredExamResultsAccessibleViaAPI() throws Exception {
        // Given: a user exists with valid JWT
        // And the user has an exam with status "EXPIRED"
        ExamSimulation exam = new ExamSimulation();
        exam.setUserId(testUserId);
        exam.setStartedAt(Instant.now().minus(Duration.ofHours(1)));
        exam.setExpiresAt(Instant.now().minus(Duration.ofMinutes(5)));
        exam.setTotalQuestions(50);
        exam.setCorrectAnswers(0);
        exam.setStatus(ExamSimulation.ExamStatus.EXPIRED);
        exam.setCompletedAt(Instant.now().minus(Duration.ofMinutes(4)));
        exam = examSimulationRepository.save(exam);

        Long examId = exam.getId();

        // When: the user requests exam results via the results API
        MvcResult result = mockMvc.perform(get("/api/exams/simulations/" + examId + "/results")
                .param("userId", testUserId.toString())) // ✅ Add userId parameter
                .andExpect(status().isOk())
                .andReturn();

        // Then: the response status should be 200
        // And: the JSON response should indicate exam status "EXPIRED"
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode resultsJson = objectMapper.readTree(jsonResponse);

        assertThat(resultsJson.path("status").asText())
                .as("Exam status should be EXPIRED")
                .isEqualTo("EXPIRED");
    }

    @Test
    @Disabled("Phase 6 - API integration: Requires REST controller implementation")
    @DisplayName("Scenario: Time-based validation is enforced at API boundary")
    void testTimeLimitEnforcedAtAPIBoundary() throws Exception {
        // Given: an expired exam exists
        ExamSimulation exam = new ExamSimulation();
        exam.setUserId(testUserId);
        exam.setStartedAt(Instant.now().minus(Duration.ofHours(1)));
        exam.setExpiresAt(Instant.now().minus(Duration.ofMinutes(10))); // Expired 10 minutes ago
        exam.setTotalQuestions(50);
        exam.setStatus(ExamSimulation.ExamStatus.IN_PROGRESS);
        exam = examSimulationRepository.save(exam);

        Long examId = exam.getId();

        // When: any modifying API is called after expiry
        String answerRequest = """
                {
                    "questionId": 1,
                    "selectedOptionId": 1,
                    "timeTakenSeconds": 30
                }
                """;

        // Then: the API must reject the request
        mockMvc.perform(post("/api/exams/simulations/" + examId + "/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(answerRequest))
                .andExpect(status().is4xxClientError());

        // And: no internal service or DTO assumptions are required
        // Test validates behavior purely through HTTP status codes
    }
}
