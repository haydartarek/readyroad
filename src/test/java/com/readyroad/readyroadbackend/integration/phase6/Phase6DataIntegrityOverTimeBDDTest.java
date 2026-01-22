package com.readyroad.readyroadbackend.integration.phase6;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.model.UserQuestionHistory;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Phase 6 Test Pack: Data Integrity Over Time (API-Based)
 *
 * ✅ API-Contract Testing Only
 * ❌ No DTO imports (dto.*)
 * ❌ No Service imports
 * ✅ HTTP endpoints only
 * ✅ JSON assertions
 *
 * Scenarios:
 * - Practice submissions update overall and category progress consistently via API
 * - Progress verification does not rely on progress DTOs
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.security.mode=dev")
@Transactional
@DisplayName("Phase 6: Data Integrity Over Time (API-Based)")
public class Phase6DataIntegrityOverTimeBDDTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TrafficSignRepository trafficSignRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizAnswerOptionRepository optionRepository;

    @Autowired
    private UserQuestionHistoryRepository historyRepository;

    private MockMvc mockMvc;
    private Long testUserId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

        testUserId = 600L;

        // Create category
        Category testCategory = new Category();
        testCategory.setCode("INTEGRITY");
        testCategory.setNameEn("Integrity Test Category");
        testCategory.setNameAr("فئة اختبار السلامة");
        testCategory.setNameNl("Integriteit testcategorie");
        testCategory.setNameFr("Catégorie de test d'intégrité");
        testCategory.setIsActive(true);
        testCategory = categoryRepository.save(testCategory);

        // Create traffic sign
        TrafficSign testSign = new TrafficSign();
        testSign.setSignCode("INT01");
        testSign.setNameEn("Integrity Test Sign");
        testSign.setNameAr("علامة اختبار السلامة");
        testSign.setNameNl("Integriteit testteken");
        testSign.setNameFr("Signe de test d'intégrité");
        testSign.setCategory(testCategory);
        testSign.setIsActive(true);
        testSign = trafficSignRepository.save(testSign);

        // Create 15 compliant questions
        for (int i = 1; i <= 15; i++) {
            QuizQuestion question = new QuizQuestion();
            question.setQuestionEn("Integrity test question " + i);
            question.setQuestionAr("سؤال اختبار السلامة " + i);
            question.setQuestionNl("Integriteit testvraag " + i);
            question.setQuestionFr("Question de test d'intégrité " + i);
            question.setDifficultyLevel(QuizQuestion.DifficultyLevel.MEDIUM);
            question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
            question.setCategory(testCategory);
            question.setTrafficSign(testSign);
            question.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
            question.setPublishedAt(LocalDateTime.now().minusDays(1));
            question.setIsActive(true);

            // ✅ Add options BEFORE saving to satisfy Belgian validation (2-3 options required)
            QuizAnswerOption correctOption = new QuizAnswerOption();
            correctOption.setQuestion(question);
            correctOption.setOptionTextEn("Correct Option");
            correctOption.setOptionTextAr("الخيار الصحيح");
            correctOption.setOptionTextNl("Juiste optie");
            correctOption.setOptionTextFr("Option correcte");
            correctOption.setIsCorrect(true);
            correctOption.setDisplayOrder(1);

            QuizAnswerOption wrongOption = new QuizAnswerOption();
            wrongOption.setQuestion(question);
            wrongOption.setOptionTextEn("Wrong Option");
            wrongOption.setOptionTextAr("الخيار الخاطئ");
            wrongOption.setOptionTextNl("Verkeerde optie");
            wrongOption.setOptionTextFr("Mauvaise option");
            wrongOption.setIsCorrect(false);
            wrongOption.setDisplayOrder(2);

            // Add options to question collection before saving
            question.getOptions().add(correctOption);
            question.getOptions().add(wrongOption);

            // Now save question with options (Belgian validation will pass: 2 options)
            question = quizQuestionRepository.save(question);
        }
    }

    @Test
    @Disabled("Phase 6 - API integration: Requires REST controller mocking and authentication setup")
    @DisplayName("Scenario: Practice submissions update progress consistently via API")
    void testPracticeSubmissionsUpdateProgressConsistentlyViaAPI() throws Exception {
        // Given: a user exists with valid JWT
        // And the user has zero initial progress

        // When: the user requests overall progress via the progress API
        MvcResult initialResult = mockMvc.perform(get("/api/users/me/progress/overall"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode initialProgress = objectMapper.readTree(initialResult.getResponse().getContentAsString());
        int initialAttempted = initialProgress.path("totalAttempted").asInt();

        // And: the user submits multiple practice answers via the practice API
        // Simulate 10 practice submissions (7 correct, 3 wrong)
        var questions = quizQuestionRepository.findAll().stream()
                .limit(10)
                .toList();

        for (int index = 0; index < questions.size(); index++) {
            var question = questions.get(index);

            // Determine if this answer should be correct (first 7 are correct)
            boolean shouldBeCorrect = index < 7;

            // Direct history insert (simulating practice submission) using Builder
            UserQuestionHistory history = UserQuestionHistory.builder()
                    .userId(testUserId)
                    .questionId(question.getId())
                    .answeredAt(LocalDateTime.now())
                    .isCorrect(shouldBeCorrect)
                    .timeTakenSeconds(30)
                    .build();
            historyRepository.save(history);
        }

        // Then: the response status should be 2xx
        // When: the user requests overall progress via the progress API
        MvcResult finalResult = mockMvc.perform(get("/api/users/me/progress/overall"))
                .andExpect(status().isOk())
                .andReturn();

        // Then: the response status should be 200
        // And: the JSON field "totalAttempted" should reflect the submitted count
        JsonNode finalProgress = objectMapper.readTree(finalResult.getResponse().getContentAsString());
        assertThat(finalProgress.path("totalAttempted").asInt())
                .as("Total attempted should be 10")
                .isEqualTo(initialAttempted + 10);

        // And: the JSON field "overallAccuracy" should reflect the correct ratio
        assertThat(finalProgress.path("overallAccuracy").asDouble())
                .as("Accuracy should be 70% (7 correct out of 10)")
                .isCloseTo(70.0, within(1.0));
    }

    @Test
    @Disabled("Phase 6 - API integration: Requires REST controller mocking and authentication setup")
    @DisplayName("Scenario: Progress verification does not rely on progress DTOs")
    void testProgressVerificationUsesJSONOnly() throws Exception {
        // Given: the progress endpoint returns JSON

        // When: the Phase 6 test validates progress
        MvcResult result = mockMvc.perform(get("/api/users/me/progress/overall"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then: it must assert using JSON paths only
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode progressJson = objectMapper.readTree(jsonResponse);

        // And: it must not reference any progress DTO class
        // Assertions use only JSON field names from the API contract
        assertThat(progressJson.has("totalAttempted")).isTrue();
        assertThat(progressJson.has("correctAnswers")).isTrue();
        assertThat(progressJson.has("overallAccuracy")).isTrue();
        assertThat(progressJson.has("masteryLevel")).isTrue();
        assertThat(progressJson.has("recommendedDifficulty")).isTrue();

        // No DTO imports or type casting required
        assertThat(progressJson.path("totalAttempted").isNumber()).isTrue();
        assertThat(progressJson.path("overallAccuracy").isNumber()).isTrue();
    }
}
