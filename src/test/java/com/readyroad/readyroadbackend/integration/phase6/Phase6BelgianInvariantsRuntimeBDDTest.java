package com.readyroad.readyroadbackend.integration.phase6;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.repository.*;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Phase 6: Belgian Compliance Runtime Invariants - API-Based BDD Tests
 *
 * ✅ API-Contract Testing Only
 * ❌ No DTO imports (dto.*)
 * ✅ HTTP endpoints only
 * ✅ JSON path assertions
 *
 * Ensures Belgian compliance rules are enforced at runtime:
 * - 2-3 options per question
 * - NL/FR translations required
 * - Traffic sign linkage required
 * - Only PUBLISHED + active questions appear in user flows
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.security.mode=dev")
@Transactional
@DisplayName("Phase 6: Belgian Invariants Runtime - BDD Tests")
public class Phase6BelgianInvariantsRuntimeBDDTest {

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
        private RoadSignRepository roadSignRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        private MockMvc mockMvc;
        private User testUser;
        private Category testCategory;
        private RoadSign testSign;
        private String testUserJwt;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                // Create test user
                testUser = new User();
                testUser.setUsername("phase6user");
                testUser.setEmail("phase6@test.com");
                testUser.setPasswordHash(passwordEncoder.encode("password123"));
                testUser.setFullName("Phase 6 Test User");
                testUser.setRole(Role.USER);
                testUser.setIsActive(true);
                testUser.setIsLocked(false);
                testUser = userRepository.save(testUser);
                testUserJwt = loginAndGetJwt(testUser.getUsername(), "password123");

                // Create test category
                testCategory = new Category();
                testCategory.setCode("URBAN");
                testCategory.setNameEn("Urban Driving");
                testCategory.setNameAr("القيادة الحضرية");
                testCategory.setNameNl("Stedelijk rijden");
                testCategory.setNameFr("Conduite urbaine");
                testCategory.setIsActive(true);
                testCategory.setDisplayOrder(1);
                testCategory = categoryRepository.save(testCategory);

                // Create test traffic sign
                testSign = new RoadSign();
                testSign.setSignCode("B1");
                testSign.setNormalizedSignCode("B1");
                testSign.setNameEn("Priority Sign");
                testSign.setNameAr("إشارة الأولوية");
                testSign.setNameNl("Voorrangsbord");
                testSign.setNameFr("Panneau de priorité");
                testSign.setCategory(SignCategory.PRIORITY);
                testSign.setIsActive(true);
                testSign = roadSignRepository.save(testSign);

                // Seed compliant published question pool (60 questions for exam)
                seedCompliantPublishedQuestionPool(60);

                // Seed some DRAFT and inactive questions (should never appear)
                seedDraftQuestion();
                seedInactivePublishedQuestion();
        }

        // ==========================================
        // Scenario: Exam generation always returns Belgian-compliant questions
        // ==========================================

        @Test
        @DisplayName("Exam generation always returns Belgian-compliant questions")
        void examGenerationReturnsBelgianCompliantQuestions() throws Exception {
                // When: User starts exam simulation via API
                MvcResult result = mockMvc.perform(post("/api/exams/simulations/start")
                                .header("Authorization", "Bearer " + testUserJwt)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isCreated())
                                .andReturn();

                // Parse JSON response without DTO mapping
                String responseBody = result.getResponse().getContentAsString();
                JsonNode response = objectMapper.readTree(responseBody);

                // Then: Verify Belgian compliance
                JsonNode questionsArray = response.path("questions");
                assertThat(questionsArray.isArray()).isTrue();
                assertThat(questionsArray.size()).isEqualTo(50);

                for (JsonNode questionNode : questionsArray) {
                        Long questionId = questionNode.path("questionId").asLong();

                        // 2-3 options
                        JsonNode optionsArray = questionNode.path("options");
                        assertThat(optionsArray.isArray()).isTrue();
                        int optionCount = optionsArray.size();
                        assertThat(optionCount)
                                        .as("Question %d should have 2-3 options", questionId)
                                        .isBetween(2, 3);

                        // NL translation required
                        String questionNl = questionNode.path("questionTextNl").asText();
                        assertThat(questionNl)
                                        .as("Question %d should have NL translation", questionId)
                                        .isNotNull()
                                        .isNotBlank();

                        // FR translation required
                        String questionFr = questionNode.path("questionTextFr").asText();
                        assertThat(questionFr)
                                        .as("Question %d should have FR translation", questionId)
                                        .isNotNull()
                                        .isNotBlank();

                        // Verify from DB
                        QuizQuestion dbQuestion = questionRepository.findById(questionId).orElseThrow();

                        // Should be published
                        assertThat(dbQuestion.getStatus())
                                        .as("Question %d should be PUBLISHED", questionId)
                                        .isEqualTo(QuizQuestion.QuestionStatus.PUBLISHED);

                        // Should be active
                        assertThat(dbQuestion.getIsActive())
                                        .as("Question %d should be active", questionId)
                                        .isTrue();

                        // Traffic sign linkage
                        assertThat(dbQuestion.getRoadSign())
                                        .as("Question %d should be linked to traffic sign", questionId)
                                        .isNotNull();
                }
        }

        // ==========================================
        // Scenario: Draft and inactive questions never appear in user payloads
        // ==========================================

        @Test
        @DisplayName("Draft and inactive questions never appear in user payloads")
        void draftAndInactiveQuestionsNeverAppearInExam() throws Exception {
                // Given: We have DRAFT and inactive questions seeded in setUp()
                List<Long> draftQuestionIds = questionRepository.findAll().stream()
                                .filter(q -> q.getStatus() == QuizQuestion.QuestionStatus.DRAFT)
                                .map(QuizQuestion::getId)
                                .toList();

                List<Long> inactiveQuestionIds = questionRepository.findAll().stream()
                                .filter(q -> q.getStatus() == QuizQuestion.QuestionStatus.PUBLISHED && !q.getIsActive())
                                .map(QuizQuestion::getId)
                                .toList();

                assertThat(draftQuestionIds).as("Setup should create DRAFT questions").isNotEmpty();
                assertThat(inactiveQuestionIds).as("Setup should create inactive questions").isNotEmpty();

                // When: User starts exam simulation via API
                MvcResult result = mockMvc.perform(post("/api/exams/simulations/start")
                                .header("Authorization", "Bearer " + testUserJwt)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isCreated())
                                .andReturn();

                // Parse JSON response without DTO mapping
                String responseBody = result.getResponse().getContentAsString();
                JsonNode response = objectMapper.readTree(responseBody);

                // Then: None of the returned questions should be DRAFT or inactive
                JsonNode questionsArray = response.path("questions");
                assertThat(questionsArray.isArray()).isTrue();

                List<Long> returnedQuestionIds = new ArrayList<>();
                for (JsonNode questionNode : questionsArray) {
                        Long questionId = questionNode.path("questionId").asLong();
                        returnedQuestionIds.add(questionId);
                }

                assertThat(returnedQuestionIds)
                                .as("Exam should not contain DRAFT questions")
                                .doesNotContainAnyElementsOf(draftQuestionIds);

                assertThat(returnedQuestionIds)
                                .as("Exam should not contain inactive questions")
                                .doesNotContainAnyElementsOf(inactiveQuestionIds);
        }

        // ==========================================
        // Helper Methods
        // ==========================================

        private void seedCompliantPublishedQuestionPool(int count) {
                for (int i = 0; i < count; i++) {
                        QuizQuestion question = new QuizQuestion();
                        question.setQuestionEn("Belgian compliant question " + i);
                        question.setQuestionAr("سؤال متوافق " + i);
                        question.setQuestionNl("Belgische vraag " + i);
                        question.setQuestionFr("Question belge " + i);
                        question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
                        question.setDifficultyLevel(difficultyForIndex(i));
                        question.setCategory(testCategory);
                        question.setRoadSign(testSign);
                        question.setIsActive(true);
                        question.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);

                        // ✅ Add 2 options BEFORE saving (Belgian validation requires 2-3 options)
                        addOption(question, "Option A " + i, true);
                        addOption(question, "Option B " + i, false);

                        // Now save question with options attached (validation will pass)
                        questionRepository.save(question);
                }
                questionRepository.flush();
        }

        private void seedDraftQuestion() {
                QuizQuestion draft = new QuizQuestion();
                draft.setQuestionEn("Draft question - should not appear");
                draft.setQuestionAr("سؤال مسودة");
                draft.setQuestionNl("Concept vraag");
                draft.setQuestionFr("Question brouillon");
                draft.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
                draft.setDifficultyLevel(QuizQuestion.DifficultyLevel.EASY);
                draft.setCategory(testCategory);
                draft.setRoadSign(testSign);
                draft.setIsActive(true);
                draft.setStatus(QuizQuestion.QuestionStatus.DRAFT);

                addOption(draft, "Draft A", true);
                addOption(draft, "Draft B", false);

                questionRepository.save(draft);
                questionRepository.flush();
        }

        private void seedInactivePublishedQuestion() {
                QuizQuestion inactive = new QuizQuestion();
                inactive.setQuestionEn("Inactive question - should not appear");
                inactive.setQuestionAr("سؤال غير نشط");
                inactive.setQuestionNl("Inactieve vraag");
                inactive.setQuestionFr("Question inactive");
                inactive.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
                inactive.setDifficultyLevel(QuizQuestion.DifficultyLevel.EASY);
                inactive.setCategory(testCategory);
                inactive.setRoadSign(testSign);
                inactive.setIsActive(false); // ❌ Inactive
                inactive.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);

                addOption(inactive, "Inactive A", true);
                addOption(inactive, "Inactive B", false);

                questionRepository.save(inactive);
                questionRepository.flush();
        }

        private void addOption(QuizQuestion question, String text, boolean correct) {
                QuizAnswerOption option = new QuizAnswerOption();
                option.setOptionTextEn(text);
                option.setOptionTextAr(text + " AR");
                option.setOptionTextNl(text + " NL");
                option.setOptionTextFr(text + " FR");
                option.setIsCorrect(correct);
                option.setDisplayOrder(question.getOptions().size() + 1);
                question.addOption(option);
        }

        private QuizQuestion.DifficultyLevel difficultyForIndex(int index) {
                if (index < 20) {
                        return QuizQuestion.DifficultyLevel.EASY;
                }
                if (index < 50) {
                        return QuizQuestion.DifficultyLevel.MEDIUM;
                }
                return QuizQuestion.DifficultyLevel.HARD;
        }

        private String loginAndGetJwt(String username, String password) {
                try {
                        String loginJson = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username,
                                        password);

                        MvcResult result = mockMvc.perform(post("/api/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(loginJson))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
                        return jsonNode.get("token").asText();
                } catch (Exception exception) {
                        throw new IllegalStateException("Failed to authenticate test user", exception);
                }
        }
}
