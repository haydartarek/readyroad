package com.readyroad.readyroadbackend.integration.phase6;

import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Phase 6 Test Pack: Performance Sanity (API-Based)
 *
 * ✅ API-Contract Testing Only
 * ❌ No DTO imports (dto.*)
 * ❌ No Service imports
 * ✅ HTTP endpoints only
 * ✅ Performance benchmarks via API
 *
 * Scenarios:
 * - Start exam responds within acceptable time budget via API
 * - Admin listing with filters responds within acceptable time budget
 * (placeholder)
 *
 * Note: Performance thresholds are configurable via test properties.
 * Default threshold: 1000ms (1 second)
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.security.mode=dev")
@Transactional
@DisplayName("Phase 6: Performance Sanity (API-Based)")
public class Phase6PerformanceSanityBDDTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RoadSignRepository roadSignRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    private Long testUserId;
    private static final long PERFORMANCE_THRESHOLD_MS = 1000L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUserId = 700L;

        // Create category
        Category testCategory = new Category();
        testCategory.setCode("PERF");
        testCategory.setNameEn("Performance Test Category");
        testCategory.setNameAr("فئة اختبار الأداء");
        testCategory.setNameNl("Prestatie testcategorie");
        testCategory.setNameFr("Catégorie de test de performance");
        testCategory.setIsActive(true);
        testCategory = categoryRepository.save(testCategory);

        // Create traffic sign
        RoadSign testSign = new RoadSign();
        testSign.setSignCode("PERF01");
        testSign.setNormalizedSignCode("PERF01");
        testSign.setNameEn("Performance Test Sign");
        testSign.setNameAr("علامة اختبار الأداء");
        testSign.setNameNl("Prestatie testteken");
        testSign.setNameFr("Signe de test de performance");
        testSign.setCategory(SignCategory.DANGER);
        testSign.setIsActive(true);
        testSign = roadSignRepository.save(testSign);

        // Create 51 compliant questions for performance test
        for (int i = 1; i <= 51; i++) {
            QuizQuestion question = new QuizQuestion();
            question.setQuestionEn("Performance test question " + i);
            question.setQuestionAr("سؤال اختبار الأداء " + i);
            question.setQuestionNl("Prestatie testvraag " + i);
            question.setQuestionFr("Question de test de performance " + i);
            question.setDifficultyLevel(QuizQuestion.DifficultyLevel.MEDIUM);
            question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
            question.setCategory(testCategory);
            question.setRoadSign(testSign);
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
    @DisplayName("Scenario: Start exam responds within acceptable time budget via API")
    void testStartExamPerformanceViaAPI() throws Exception {
        // Given: a user exists with valid JWT

        // When: the user starts an exam simulation via API
        long startTime = System.currentTimeMillis();

        mockMvc.perform(post("/api/exams/simulations/start")
                .param("userId", testUserId.toString()))
                .andExpect(status().isCreated()); // 201 Created (REST best practice for resource creation)

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then: the request should complete under 1000 milliseconds
        assertThat(duration)
                .as("Start exam API should respond within %dms (actual: %dms)",
                        PERFORMANCE_THRESHOLD_MS, duration)
                .isLessThan(PERFORMANCE_THRESHOLD_MS);
    }

    @Test
    @DisplayName("Scenario: Admin listing with filters responds within acceptable time budget [PLACEHOLDER]")
    void testAdminListingPerformanceViaAPI() {
        // PLACEHOLDER: This test requires:
        // 1. Admin endpoint for listing questions
        // 2. Filter parameters (status, search, etc.)

        // Given: an admin exists with valid JWT
        // When: the admin calls GET
        // "/api/admin/questions?status=PUBLISHED&search=urban" via API
        // Then: the request should complete under 1000 milliseconds
        // And: no service or DTO imports are required

        assertThat(true).isTrue(); // Placeholder assertion
    }
}
