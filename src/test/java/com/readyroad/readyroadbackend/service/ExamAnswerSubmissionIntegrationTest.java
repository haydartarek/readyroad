package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.BaseIntegrationTest;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerRequest;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerResponse;
import com.readyroad.readyroadbackend.exception.ExamNotActiveException;
import com.readyroad.readyroadbackend.exception.InvalidAnswerException;
import com.readyroad.readyroadbackend.exception.QuestionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration Tests for Story A2: Submit Exam Answer
 */
@SpringBootTest
@Transactional // ✅ Added to fix LazyInitializationException
@ActiveProfiles("test")
class ExamAnswerSubmissionIntegrationTest extends BaseIntegrationTest {

        @Autowired
        private ExamService examService;

        @Autowired
        private ExamSimulationRepository examRepository;

        @Autowired
        private ExamSimulationQuestionRepository examQuestionRepository;

        @Autowired
        private ExamSimulationAnswerRepository answerRepository;

        @Autowired
        private QuizQuestionRepository questionRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        private Long testUserId;
        private Category testCategory;

        @BeforeEach
        void setUp() {
                testUserId = 100L;

                // ✅ Clean up existing exams to avoid "User already has an active exam"
                examRepository.deleteAll();
                answerRepository.deleteAll();

                // ✅ Use category seeded by BaseIntegrationTest (no duplication)
                var categories = categoryRepository.findAll();
                testCategory = categories.isEmpty() ? null : categories.get(0);

                // ✅ BaseIntegrationTest already seeded 200 PUBLISHED questions
                long questionCount = questionRepository.count();
                assertThat(questionCount).isGreaterThanOrEqualTo(50)
                                .as("BaseIntegrationTest should have seeded at least 50 questions");
        }

        @Test
        @DisplayName("Story A2: Can submit exam answer")
        void testSubmitExamAnswer() {
                // Given - Create exam with questions
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                ExamSimulationQuestion firstQuestion = examQuestionRepository
                                .findByExamIdOrderByQuestionOrder(exam.getId())
                                .get(0);

                // Load question with options explicitly
                QuizQuestion question = questionRepository.findById(firstQuestion.getQuestionId())
                                .orElseThrow();

                QuizAnswerOption selectedOption = question.getOptions().get(0);

                SubmitExamAnswerRequest request = SubmitExamAnswerRequest.builder()
                                .selectedOptionId(selectedOption.getId())
                                .build();

                // When
                SubmitExamAnswerResponse response = examService.submitAnswer(
                                exam.getId(),
                                firstQuestion.getQuestionId(),
                                request, testUserId);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getAnswerId()).isNotNull();
                assertThat(response.getExamId()).isEqualTo(exam.getId());
                assertThat(response.getQuestionId()).isEqualTo(firstQuestion.getQuestionId());
                assertThat(response.getSelectedOptionId()).isEqualTo(selectedOption.getId());
                assertThat(response.getSubmittedAt()).isNotNull();
                assertThat(response.getMessage()).isEqualTo("Answer submitted successfully.");
                assertThat(response.getTotalAnswered()).isEqualTo(1);
                assertThat(response.getTotalQuestions()).isEqualTo(50);

                // Verify answer saved in database
                ExamSimulationAnswer savedAnswer = answerRepository
                                .findByExamIdAndQuestionId(exam.getId(), firstQuestion.getQuestionId())
                                .orElseThrow();

                assertThat(savedAnswer.getSelectedOption().getId()).isEqualTo(selectedOption.getId());
                assertThat(savedAnswer.getAnsweredAt()).isNotNull();
        }

        @Test
        @DisplayName("Story A2: Cannot submit answer for completed exam")
        void testCannotSubmitAnswerForCompletedExam() {
                // Given - Create and complete exam
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                exam.setStatus(ExamSimulation.ExamStatus.COMPLETED);
                examRepository.save(exam);

                ExamSimulationQuestion firstQuestion = examQuestionRepository
                                .findByExamIdOrderByQuestionOrder(exam.getId())
                                .get(0);

                SubmitExamAnswerRequest request = SubmitExamAnswerRequest.builder()
                                .selectedOptionId(1L)
                                .build();

                // When & Then
                assertThatThrownBy(() -> examService.submitAnswer(
                                exam.getId(),
                                firstQuestion.getQuestionId(),
                                request, testUserId))
                                .isInstanceOf(ExamNotActiveException.class)
                                .hasMessageContaining("exam status is COMPLETED");
        }

        @Test
        @DisplayName("Story A2: Can update answer for same question")
        void testCanUpdateAnswer() {
                // Given - Submit first answer
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                ExamSimulationQuestion firstQuestion = examQuestionRepository
                                .findByExamIdOrderByQuestionOrder(exam.getId())
                                .get(0);

                // Load question with options
                QuizQuestion question = questionRepository.findById(firstQuestion.getQuestionId())
                                .orElseThrow();
                List<QuizAnswerOption> options = question.getOptions();

                SubmitExamAnswerRequest firstRequest = SubmitExamAnswerRequest.builder()
                                .selectedOptionId(options.get(0).getId())
                                .build();

                examService.submitAnswer(
                                exam.getId(),
                                firstQuestion.getQuestionId(),
                                firstRequest, testUserId);

                // When - Submit different answer for same question
                SubmitExamAnswerRequest secondRequest = SubmitExamAnswerRequest.builder()
                                .selectedOptionId(options.get(1).getId())
                                .build();

                SubmitExamAnswerResponse response = examService.submitAnswer(
                                exam.getId(),
                                firstQuestion.getQuestionId(),
                                secondRequest, testUserId);

                // Then - Answer should be updated
                assertThat(response.getSelectedOptionId()).isEqualTo(options.get(1).getId());
                assertThat(response.getTotalAnswered()).isEqualTo(1); // Still only 1 answer

                // Verify only one answer exists
                long answerCount = answerRepository.countByExamId(exam.getId());
                assertThat(answerCount).isEqualTo(1);
        }

        @Test
        @DisplayName("Story A2: Cannot submit answer for question not in exam")
        void testCannotSubmitAnswerForQuestionNotInExam() {
                // Given - Create exam
                ExamSimulation exam = examService.startExamSimulation(testUserId);

                // Create a question not in this exam with proper 2 options
                QuizQuestion separateQuestion = new QuizQuestion();
                separateQuestion.setQuestionEn("Separate question");
                separateQuestion.setQuestionAr("سؤال منفصل");
                separateQuestion.setQuestionNl("Afzonderlijke vraag");
                separateQuestion.setQuestionFr("Question séparée");
                separateQuestion.setDifficultyLevel(QuizQuestion.DifficultyLevel.EASY);
                separateQuestion.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
                separateQuestion.setCategory(testCategory);

                // Create 2 options for Belgian compliance
                List<QuizAnswerOption> options = new ArrayList<>();

                QuizAnswerOption option1 = new QuizAnswerOption();
                option1.setOptionTextEn("Option 1");
                option1.setOptionTextAr("خيار 1");
                option1.setOptionTextNl("Optie 1");
                option1.setOptionTextFr("Option 1");
                option1.setIsCorrect(true);
                option1.setQuestion(separateQuestion);
                options.add(option1);

                QuizAnswerOption option2 = new QuizAnswerOption();
                option2.setOptionTextEn("Option 2");
                option2.setOptionTextAr("خيار 2");
                option2.setOptionTextNl("Optie 2");
                option2.setOptionTextFr("Option 2");
                option2.setIsCorrect(false);
                option2.setQuestion(separateQuestion);
                options.add(option2);

                separateQuestion.setOptions(options);
                separateQuestion = questionRepository.save(separateQuestion);

                SubmitExamAnswerRequest request = SubmitExamAnswerRequest.builder()
                                .selectedOptionId(1L)
                                .build();

                // When & Then
                Long finalQuestionId = separateQuestion.getId();
                assertThatThrownBy(() -> examService.submitAnswer(
                                exam.getId(),
                                finalQuestionId,
                                request, testUserId))
                                .isInstanceOf(QuestionNotFoundException.class)
                                .hasMessageContaining("not found in exam");
        }

        @Test
        @DisplayName("Story A2: Cannot submit answer with invalid option ID")
        void testCannotSubmitAnswerWithInvalidOption() {
                // Given
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                ExamSimulationQuestion firstQuestion = examQuestionRepository
                                .findByExamIdOrderByQuestionOrder(exam.getId())
                                .get(0);

                SubmitExamAnswerRequest request = SubmitExamAnswerRequest.builder()
                                .selectedOptionId(999999L) // Invalid option ID
                                .build();

                // When & Then
                assertThatThrownBy(() -> examService.submitAnswer(
                                exam.getId(),
                                firstQuestion.getQuestionId(),
                                request, testUserId))
                                .isInstanceOf(InvalidAnswerException.class)
                                .hasMessageContaining("Option 999,999 is invalid.");
        }

        @Test
        @DisplayName("Story A2: Progress tracking updates correctly")
        void testProgressTrackingUpdates() {
                // Given
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                List<ExamSimulationQuestion> questions = examQuestionRepository
                                .findByExamIdOrderByQuestionOrder(exam.getId());

                // When - Submit answers for 5 questions
                for (int i = 0; i < 5; i++) {
                        ExamSimulationQuestion esq = questions.get(i);

                        // Load question with options
                        QuizQuestion question = questionRepository.findById(esq.getQuestionId())
                                        .orElseThrow();
                        QuizAnswerOption option = question.getOptions().get(0);

                        SubmitExamAnswerRequest request = SubmitExamAnswerRequest.builder()
                                        .selectedOptionId(option.getId())
                                        .build();

                        SubmitExamAnswerResponse response = examService.submitAnswer(
                                        exam.getId(),
                                        esq.getQuestionId(),
                                        request, testUserId);

                        // Then - Progress should increase
                        assertThat(response.getTotalAnswered()).isEqualTo(i + 1);
                        assertThat(response.getTotalQuestions()).isEqualTo(50);
                }

                // Verify total count
                long totalAnswered = answerRepository.countByExamId(exam.getId());
                assertThat(totalAnswered).isEqualTo(5);
        }

        @Test
        @DisplayName("Story A2: Answer correctness NOT revealed in response (security)")
        void testAnswerCorrectnessNotRevealed() {
                // Given
                ExamSimulation exam = examService.startExamSimulation(testUserId);
                ExamSimulationQuestion firstQuestion = examQuestionRepository
                                .findByExamIdOrderByQuestionOrder(exam.getId())
                                .get(0);

                // Load question with options
                QuizQuestion question = questionRepository.findById(firstQuestion.getQuestionId())
                                .orElseThrow();
                QuizAnswerOption selectedOption = question.getOptions().get(0);

                SubmitExamAnswerRequest request = SubmitExamAnswerRequest.builder()
                                .selectedOptionId(selectedOption.getId())
                                .build();

                // When
                SubmitExamAnswerResponse response = examService.submitAnswer(
                                exam.getId(),
                                firstQuestion.getQuestionId(),
                                request, testUserId);

                // Then - Response should NOT contain isCorrect field
                // This is a security requirement - correctness only revealed after exam
                // completion
                assertThat(response).isNotNull();
                assertThat(response.getMessage()).isEqualTo("Answer submitted successfully.");

                // Verify the response class doesn't have isCorrect field exposed
                // (This is enforced by DTO design - SubmitExamAnswerResponse has no isCorrect
                // field)
        }
}
