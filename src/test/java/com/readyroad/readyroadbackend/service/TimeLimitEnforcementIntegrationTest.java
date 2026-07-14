package com.readyroad.readyroadbackend.service;

import java.math.BigDecimal;

import com.readyroad.readyroadbackend.BaseIntegrationTest;
import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.repository.*;
import com.readyroad.readyroadbackend.dto.exam.ExamResultsDTO;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerRequest;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerResponse;
import com.readyroad.readyroadbackend.exception.ExamExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Story A4: Time Limit Enforcement Integration Tests
 *
 * BDD Feature: Exam time limit enforcement (30 minutes)
 *
 * Scenarios:
 * 1. Exam starts with correct time limit
 * 2. Submitting answer before expiry is allowed
 * 3. Submitting answer after expiry is rejected
 * 4. Expired exam auto-changes status to EXPIRED
 * 5. Results available for expired exams
 * 6. Expired exam cannot be resumed
 */
@SpringBootTest
@Transactional // ✅ Added to fix LazyInitializationException
@ActiveProfiles("test")
class TimeLimitEnforcementIntegrationTest extends BaseIntegrationTest {

        @Autowired
        private ExamService examService;

        @Autowired
        private ExamSimulationRepository examRepository;

        @Autowired
        private QuizQuestionRepository questionRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private UserCategoryProgressRepository progressRepository;

        private Long testUserId;
        private Category testCategory;
        private List<QuizQuestion> testQuestions;

        @BeforeEach
        void setUp() {
                testUserId = 100L;

                // Resolve the parent fixture from the database, never from the cached
                // findAll() result or an assumed numeric ID.
                testCategory = getOrCreateCategory("SIGNS", "Traffic Signs");

                // ✅ BaseIntegrationTest already seeded 200 PUBLISHED questions
                testQuestions = questionRepository.findAll();
                assertThat(testQuestions).hasSizeGreaterThanOrEqualTo(50)
                                .as("BaseIntegrationTest should have seeded at least 50 questions");

                // Create user progress for exam generation
                UserCategoryProgress progress = new UserCategoryProgress();
                progress.setUserId(testUserId);
                progress.setCategoryId(testCategory.getId());
                progress.setQuestionsAttempted(10);
                progress.setCorrectAnswers(7);
                progress.setAccuracyRate(BigDecimal.valueOf(70.0));
                progress.setMasteryLevel(UserCategoryProgress.MasteryLevel.INTERMEDIATE);
                progressRepository.saveAndFlush(progress);
        }

        @Test
        @DisplayName("Story A4: Exam starts with countdown timer")
        void testExamStartsWithTimeLimit() {
                // When
                Instant beforeStart = Instant.now();
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                Instant afterStart = Instant.now();

                // Then
                assertThat(exam.getStartedAt()).isNotNull();
                assertThat(exam.getExpiresAt()).isNotNull();
                assertThat(exam.getStatus()).isEqualTo(ExamSimulation.ExamStatus.IN_PROGRESS);

                // Time limit should be 30 minutes
                long timeLimitMinutes = Duration.between(
                                exam.getStartedAt(),
                                exam.getExpiresAt()).toMinutes();

                assertThat(timeLimitMinutes).isEqualTo(30);

                // Start time should be now (within 1 second tolerance)
                assertThat(exam.getStartedAt()).isBetween(beforeStart, afterStart);
        }

        @Test
        @DisplayName("Story A4: Submitting answer before expiry is allowed")
        void testSubmitAnswerBeforeExpiry() {
                // Given - Fresh exam (30 minutes remaining)
                ExamSimulation exam = examService.startExamSimulation(testUserId);

                // Get the first question from the actual exam
                List<ExamSimulationQuestion> examQuestions = examService.getExamQuestions(exam.getId());
                Long firstQuestionId = examQuestions.get(0).getQuestionId();

                // Load question with options
                QuizQuestion question = questionRepository.findById(firstQuestionId).orElseThrow();
                Long correctOptionId = question.getOptions().stream()
                                .filter(QuizAnswerOption::getIsCorrect)
                                .findFirst()
                                .orElseThrow()
                                .getId();

                SubmitExamAnswerRequest request = new SubmitExamAnswerRequest();
                request.setSelectedOptionId(correctOptionId);

                // When - Submit answer immediately (well within time limit)
                SubmitExamAnswerResponse response = examService.submitAnswer(
                                exam.getId(),
                                firstQuestionId,
                                request, testUserId);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getAnswerId()).isNotNull();
                assertThat(response.getMessage()).isEqualTo("Answer submitted successfully.");

                // Exam should still be IN_PROGRESS
                ExamSimulation updatedExam = examRepository.findById(exam.getId()).orElseThrow();
                assertThat(updatedExam.getStatus()).isEqualTo(ExamSimulation.ExamStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Story A4: Submitting answer after expiry is rejected")
        void testSubmitAnswerAfterExpiry() {
                // Given - Create exam and manually set expiry to past
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                exam.setExpiresAt(Instant.now().minus(Duration.ofMinutes(1))); // Expired 1 minute ago
                examRepository.save(exam);

                Long firstQuestionId = testQuestions.get(0).getId();
                QuizQuestion question = questionRepository.findById(firstQuestionId).orElseThrow();
                Long correctOptionId = question.getOptions().stream()
                                .filter(QuizAnswerOption::getIsCorrect)
                                .findFirst()
                                .orElseThrow()
                                .getId();

                SubmitExamAnswerRequest request = new SubmitExamAnswerRequest();
                request.setSelectedOptionId(correctOptionId);

                // When/Then - Submit answer should throw ExamExpiredException
                assertThatThrownBy(() -> examService.submitAnswer(
                                exam.getId(),
                                firstQuestionId,
                                request, testUserId))
                                .isInstanceOf(ExamExpiredException.class)
                                .hasMessageContaining("The exam has expired")
                                .hasMessageContaining("30 minutes");
        }

        @Test
        @DisplayName("Story A4: Expired exam auto-changes status to EXPIRED")
        void testExpiredExamStatusChange() {
                // Given - Exam with past expiry
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                exam.setExpiresAt(Instant.now().minus(Duration.ofMinutes(1)));
                examRepository.save(exam);

                Long firstQuestionId = testQuestions.get(0).getId();
                QuizQuestion question = questionRepository.findById(firstQuestionId).orElseThrow();
                Long correctOptionId = question.getOptions().stream()
                                .filter(QuizAnswerOption::getIsCorrect)
                                .findFirst()
                                .orElseThrow()
                                .getId();

                SubmitExamAnswerRequest request = new SubmitExamAnswerRequest();
                request.setSelectedOptionId(correctOptionId);

                // When - Try to submit answer (will be rejected)
                try {
                        examService.submitAnswer(exam.getId(), firstQuestionId, request, testUserId);
                } catch (ExamExpiredException e) {
                        // Expected
                }

                // Then - Exam status should be EXPIRED
                ExamSimulation expiredExam = examRepository.findById(exam.getId()).orElseThrow();
                assertThat(expiredExam.getStatus()).isEqualTo(ExamSimulation.ExamStatus.EXPIRED);
        }

        @Test
        @DisplayName("Story A4: Results available for expired exams")
        void testResultsAvailableForExpiredExam() {
                // Given - Start exam and answer 25 questions
                ExamSimulation exam = examService.startExamSimulation(testUserId);

                // Get the actual questions in this exam
                List<ExamSimulationQuestion> examQuestions = examService.getExamQuestions(exam.getId());

                // Submit 25 answers (using actual exam questions)
                for (int i = 0; i < 25; i++) {
                        ExamSimulationQuestion examQuestion = examQuestions.get(i);

                        // Use questionId directly instead of trying to access through lazy-loaded
                        // relationship
                        Long questionId = examQuestion.getQuestionId();
                        QuizQuestion question = questionRepository.findById(questionId).orElseThrow();
                        Long correctOptionId = question.getOptions().stream()
                                        .filter(QuizAnswerOption::getIsCorrect)
                                        .findFirst()
                                        .orElseThrow()
                                        .getId();

                        SubmitExamAnswerRequest request = new SubmitExamAnswerRequest();
                        request.setSelectedOptionId(correctOptionId);

                        examService.submitAnswer(exam.getId(), questionId, request, testUserId);
                }

                // Manually expire the exam
                exam.setStatus(ExamSimulation.ExamStatus.EXPIRED);
                exam.setCompletedAt(Instant.now());
                examRepository.save(exam);

                // When - Request results
                ExamResultsDTO results = examService.getExamResults(exam.getId(), testUserId);

                // Then - Results should be available
                assertThat(results).isNotNull();
                assertThat(results.getExamId()).isEqualTo(exam.getId());
                assertThat(results.getCorrectAnswers()).isEqualTo(25);
                assertThat(results.getWrongAnswers()).isEqualTo(0);
                assertThat(results.getUnansweredCount()).isEqualTo(25);
                assertThat(results.getPassed()).isFalse(); // Only 25/50
        }

        @Test
        @DisplayName("Story A4: Expired exam cannot be resumed")
        void testExpiredExamCannotBeResumed() {
                // Given - Expired exam
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                exam.setStatus(ExamSimulation.ExamStatus.EXPIRED);
                exam.setExpiresAt(Instant.now().minus(Duration.ofMinutes(1)));
                examRepository.save(exam);

                Long firstQuestionId = testQuestions.get(0).getId();
                QuizQuestion question = questionRepository.findById(firstQuestionId).orElseThrow();
                Long correctOptionId = question.getOptions().stream()
                                .filter(QuizAnswerOption::getIsCorrect)
                                .findFirst()
                                .orElseThrow()
                                .getId();

                SubmitExamAnswerRequest request = new SubmitExamAnswerRequest();
                request.setSelectedOptionId(correctOptionId);

                // When/Then - Any action should be rejected
                assertThatThrownBy(() -> examService.submitAnswer(
                                exam.getId(),
                                firstQuestionId,
                                request, testUserId))
                                .isInstanceOf(com.readyroad.readyroadbackend.exception.ExamNotActiveException.class)
                                .hasMessageContaining("exam status is EXPIRED");
        }

        @Test
        @DisplayName("Story A4: Time enforcement is consistent across actions")
        void testTimeEnforcementConsistency() {
                // Given - Expired exam
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                exam.setExpiresAt(Instant.now().minus(Duration.ofMinutes(5))); // Expired 5 minutes ago
                examRepository.save(exam);

                Long questionId = testQuestions.get(0).getId();
                QuizQuestion question = questionRepository.findById(questionId).orElseThrow();
                Long optionId = question.getOptions().stream()
                                .filter(QuizAnswerOption::getIsCorrect)
                                .findFirst()
                                .orElseThrow()
                                .getId();

                SubmitExamAnswerRequest request = new SubmitExamAnswerRequest();
                request.setSelectedOptionId(optionId);

                // When/Then - All actions should consistently reject
                // Action 1: Submit answer
                assertThatThrownBy(() -> examService.submitAnswer(exam.getId(), questionId, request, testUserId))
                                .isInstanceOf(ExamExpiredException.class);

                // Verify exam was auto-expired
                ExamSimulation expiredExam = examRepository.findById(exam.getId()).orElseThrow();
                assertThat(expiredExam.getStatus()).isEqualTo(ExamSimulation.ExamStatus.EXPIRED);

                // Action 2: Try to submit another answer (should fail with EXPIRED status)
                Long secondQuestionId = testQuestions.get(1).getId();
                QuizQuestion secondQuestion = questionRepository.findById(secondQuestionId).orElseThrow();
                Long secondOptionId = secondQuestion.getOptions().stream()
                                .filter(QuizAnswerOption::getIsCorrect)
                                .findFirst()
                                .orElseThrow()
                                .getId();

                request.setSelectedOptionId(secondOptionId);

                assertThatThrownBy(() -> examService.submitAnswer(exam.getId(), secondQuestionId, request, testUserId))
                                .hasMessageContaining("exam status is EXPIRED");
        }
}
