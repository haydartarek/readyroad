package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.BaseIntegrationTest;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;

import com.readyroad.readyroadbackend.domain.repository.ExamSimulationQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.dto.exam.ExamResultsDTO;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerRequest;
import com.readyroad.readyroadbackend.exception.ExamNotCompletedException;
import com.readyroad.readyroadbackend.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration Tests for Story A3: View Exam Results
 *
 * Tests comprehensive exam results retrieval including:
 * - Overall score and pass/fail status
 * - Category breakdown
 * - Incorrect questions with explanations
 */
@SpringBootTest
@Transactional // ✅ Prevents LazyInitializationException & ensures test isolation
@ActiveProfiles("test")
class ExamResultsIntegrationTest extends BaseIntegrationTest {

        @Autowired
        private ExamService examService;

        @Autowired
        private ExamSimulationRepository examRepository;

        @Autowired
        private ExamSimulationQuestionRepository examQuestionRepository;

        @Autowired
        private QuizQuestionRepository quizQuestionRepository;

        private Long testUserId;

        @BeforeEach
        void setUp() {
                testUserId = 999L;

                // ✅ BaseIntegrationTest already seeded 200 PUBLISHED questions
                long questionCount = quizQuestionRepository.count();
                assertThat(questionCount).isGreaterThanOrEqualTo(50)
                                .as("BaseIntegrationTest should have seeded at least 50 questions");
        }

        @Test
        @DisplayName("Story A3: Can view exam results for completed exam")
        void testGetExamResultsForCompletedExam() {
                // Given - Create and complete exam
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                List<ExamSimulationQuestion> questions = examQuestionRepository
                                .findByExamIdOrderByQuestionOrder(exam.getId());

                // Submit 45 correct answers (90% - PASS)
                for (int i = 0; i < 45; i++) {
                        ExamSimulationQuestion esq = questions.get(i);
                        QuizQuestion question = quizQuestionRepository.findById(esq.getQuestionId())
                                        .orElseThrow();
                        QuizAnswerOption correctOption = question.getOptions().stream()
                                        .filter(QuizAnswerOption::getIsCorrect)
                                        .findFirst()
                                        .orElseThrow();

                        SubmitExamAnswerRequest request = SubmitExamAnswerRequest.builder()
                                        .selectedOptionId(correctOption.getId())
                                        .build();

                        examService.submitAnswer(exam.getId(), esq.getQuestionId(), request, testUserId);
                }

                // Submit 5 wrong answers
                for (int i = 45; i < 50; i++) {
                        ExamSimulationQuestion esq = questions.get(i);
                        QuizQuestion question = quizQuestionRepository.findById(esq.getQuestionId())
                                        .orElseThrow();
                        QuizAnswerOption wrongOption = question.getOptions().stream()
                                        .filter(opt -> !opt.getIsCorrect())
                                        .findFirst()
                                        .orElseThrow();

                        SubmitExamAnswerRequest request = SubmitExamAnswerRequest.builder()
                                        .selectedOptionId(wrongOption.getId())
                                        .build();

                        examService.submitAnswer(exam.getId(), esq.getQuestionId(), request, testUserId);
                }

                // Mark exam as completed
                Instant completedAt = Instant.now();
                exam.setStartedAt(completedAt.minusSeconds(600));
                exam.setStatus(ExamSimulation.ExamStatus.COMPLETED);
                exam.setCompletedAt(completedAt);
                exam.setCorrectAnswers(45);
                exam.setScorePercentage(90.0);
                examRepository.save(exam);

                // When
                ExamResultsDTO results = examService.getExamResults(exam.getId(), testUserId);

                // Then - Verify overall results
                assertThat(results).isNotNull();
                assertThat(results.getExamId()).isEqualTo(exam.getId());
                assertThat(results.getUserId()).isEqualTo(testUserId);
                assertThat(results.getTotalQuestions()).isEqualTo(50);
                assertThat(results.getCorrectAnswers()).isEqualTo(45);
                assertThat(results.getWrongAnswers()).isEqualTo(5);
                assertThat(results.getScorePercentage()).isEqualTo(90.0);
                assertThat(results.getPassed()).isTrue(); // 45/50 >= 41 (Belgian passing threshold)
                assertThat(results.getPassingScore()).isEqualTo(41);
                assertThat(results.getTimeTakenSeconds()).isEqualTo(600);
                assertThat(results.getAverageTimePerQuestion()).isEqualTo(12);

                // Verify category breakdown exists
                assertThat(results.getCategoryBreakdown()).isNotEmpty();

                // Verify incorrect questions (should be 5)
                assertThat(results.getIncorrectQuestions()).hasSize(5);
                assertThat(results.getIncorrectQuestions().get(0).getQuestionTextEn()).isNotNull();
                assertThat(results.getIncorrectQuestions().get(0).getCorrectOptionText()).isNotNull();
                assertThat(results.getIncorrectQuestions().get(0).getSelectedOptionTextAr()).isNotBlank();
                assertThat(results.getIncorrectQuestions().get(0).getCorrectOptionTextNl()).isNotBlank();
                assertThat(results.getIncorrectQuestions().get(0).getCorrectOptionTextFr()).isNotBlank();
                assertThat(results.getIncorrectQuestions().get(0).getCategoryNameAr()).isNotBlank();
                assertThat(results.getAllAnswers()).hasSize(50);
                assertThat(results.getAllAnswers().get(0).getSelectedOptionTextAr()).isNotBlank();
        }

        @Test
        @DisplayName("Story A3: Completing an exam persists its elapsed time")
        void testCompleteExamPersistsElapsedTime() {
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                exam.setStartedAt(Instant.now().minusSeconds(120));
                examRepository.save(exam);

                examService.completeExam(exam.getId(), testUserId);

                ExamSimulation completed = examRepository.findById(exam.getId()).orElseThrow();
                assertThat(completed.getTimeTakenSeconds()).isBetween(119, 121);
                assertThat(completed.getCompletedAt()).isNotNull();
        }

        @Test
        @DisplayName("Story A3: Invalid historical timing remains unavailable")
        void testInvalidHistoricalTimingIsUnavailable() {
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                Instant completedAt = Instant.now();
                exam.setStartedAt(completedAt.plusSeconds(30));
                exam.setCompletedAt(completedAt);
                exam.setTimeTakenSeconds(null);
                exam.setStatus(ExamSimulation.ExamStatus.COMPLETED);
                examRepository.save(exam);

                ExamResultsDTO results = examService.getExamResults(exam.getId(), testUserId);

                assertThat(results.getTimeTakenSeconds()).isNull();
                assertThat(results.getAverageTimePerQuestion()).isNull();
        }

        @Test
        @DisplayName("Story A3: Cannot view other user's exam results")
        void testCannotViewOtherUsersExam() {
                // Given
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                exam.setStatus(ExamSimulation.ExamStatus.COMPLETED);
                exam.setCompletedAt(Instant.now());
                examRepository.save(exam);

                Long otherUserId = 888L;

                // When/Then
                assertThatThrownBy(() -> examService.getExamResults(exam.getId(), otherUserId))
                                .isInstanceOf(UnauthorizedException.class)
                                .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("Story A3: Cannot view incomplete exam results")
        void testCannotViewIncompleteExamResults() {
                // Given - exam still IN_PROGRESS
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                // Status is still IN_PROGRESS (not completed)

                // When/Then
                assertThatThrownBy(() -> examService.getExamResults(exam.getId(), testUserId))
                                .isInstanceOf(ExamNotCompletedException.class)
                                .hasMessageContaining("IN_PROGRESS");
        }

        @Test
        @DisplayName("Story A3: Category breakdown calculated correctly")
        void testCategoryBreakdownCalculatedCorrectly() {
                // Given - Create exam and submit all answers
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                List<ExamSimulationQuestion> questions = examQuestionRepository
                                .findByExamIdOrderByQuestionOrder(exam.getId());

                // Submit all correct answers
                for (ExamSimulationQuestion esq : questions) {
                        QuizQuestion question = quizQuestionRepository.findById(esq.getQuestionId())
                                        .orElseThrow();
                        QuizAnswerOption correctOption = question.getOptions().stream()
                                        .filter(QuizAnswerOption::getIsCorrect)
                                        .findFirst()
                                        .orElseThrow();

                        SubmitExamAnswerRequest request = SubmitExamAnswerRequest.builder()
                                        .selectedOptionId(correctOption.getId())
                                        .build();

                        examService.submitAnswer(exam.getId(), esq.getQuestionId(), request, testUserId);
                }

                // Mark as completed
                exam.setStatus(ExamSimulation.ExamStatus.COMPLETED);
                exam.setCompletedAt(Instant.now());
                exam.setCorrectAnswers(50);
                exam.setScorePercentage(100.0);
                examRepository.save(exam);

                // When
                ExamResultsDTO results = examService.getExamResults(exam.getId(), testUserId);

                // Then
                assertThat(results.getCategoryBreakdown()).isNotEmpty();

                // ✅ Fixed: Category name varies based on BaseIntegrationTest seeded data
                // Instead of hardcoding "Traffic Signs", verify any valid category exists
                assertThat(results.getCategoryBreakdown().get(0).getCategoryNameEn())
                                .isNotEmpty()
                                .as("Category name should be present from seeded data");

                assertThat(results.getCategoryBreakdown().get(0).getTotalQuestions()).isGreaterThan(0);
                assertThat(results.getCategoryBreakdown().get(0).getCorrectAnswers()).isGreaterThan(0);
                assertThat(results.getCategoryBreakdown().get(0).getAccuracyPercentage()).isGreaterThan(0.0);
        }
}
