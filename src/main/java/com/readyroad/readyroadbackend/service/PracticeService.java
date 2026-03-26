package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.repository.*;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerRequest;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerResponse;
import com.readyroad.readyroadbackend.exception.InvalidAnswerException;
import com.readyroad.readyroadbackend.exception.QuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Practice Service - Story B1: Submit Practice Answer
 *
 * Handles:
 * - Practice answer submission with immediate feedback
 * - User question history recording (24h cooldown enforcement)
 * - Category progress tracking
 * - Mastery level calculation
 *
 * @author ReadyRoad Team
 * @since Phase 5 - Story B1
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PracticeService {

        private final QuizQuestionRepository questionRepository;
        private final QuizAnswerOptionRepository optionRepository;
        private final UserQuestionHistoryRepository historyRepository;
        private final UserCategoryProgressRepository progressRepository;
        private final StreakService streakService; // Story N3: Study streak notifications

        /**
         * Submit practice answer and get immediate feedback
         * Story B1: Submit Practice Answer
         *
         * Flow:
         * 1. Validate question exists
         * 2. Validate selected option exists and belongs to question
         * 3. Check correctness
         * 4. Record in user_question_history (Law #1: 24h cooldown)
         * 5. Update category progress
         * 6. Return comprehensive feedback
         *
         * @param userId     User ID
         * @param questionId Question ID
         * @param request    Answer submission request
         * @return Response with immediate feedback and updated progress
         * @throws QuestionNotFoundException if question doesn't exist
         * @throws InvalidAnswerException    if selected option is invalid
         */
        @Transactional
        public SubmitPracticeAnswerResponse submitPracticeAnswer(
                        Long userId,
                        Long questionId,
                        SubmitPracticeAnswerRequest request) {

                log.info("Processing practice answer submission: userId={}, questionId={}, optionId={}",
                                userId, questionId, request.getSelectedOptionId());

                // 1. Load and validate question
                QuizQuestion question = loadQuestion(questionId);

                // 1b. Eligibility gate — reject answers for inactive or unpublished questions
                validateQuestionEligibility(question);

                // 2. Validate selected option
                QuizAnswerOption selectedOption = validateSelectedOption(request.getSelectedOptionId(), questionId);

                // 3. Check correctness
                boolean isCorrect = selectedOption.getIsCorrect();
                log.debug("Answer is {}: questionId={}, selectedOption={}",
                                isCorrect ? "CORRECT" : "INCORRECT", questionId, selectedOption.getId());

                // 4. Get correct option for response
                QuizAnswerOption correctOption = getCorrectOption(question);

                // 5. Record in user_question_history
                recordAnswerHistory(userId, question.getId(), isCorrect, request.getTimeTakenSeconds());

                // 5b. Check study streak milestone and fire notification if needed
                streakService.updateStreakAndNotify(userId);

                // 6. Update category progress
                UserCategoryProgress progress = updateCategoryProgress(
                                userId,
                                question.getCategory(),
                                isCorrect,
                                question.getDifficultyLevel(),
                                getTimeTaken(request));

                // 7. Build and return response
                SubmitPracticeAnswerResponse response = buildResponse(
                                questionId,
                                isCorrect,
                                selectedOption,
                                correctOption,
                                question,
                                progress);

                log.info("Practice answer processed: userId={}, questionId={}, isCorrect={}, accuracy={}",
                                userId, questionId, isCorrect, progress.getAccuracyRate());

                return response;
        }

        /**
         * Load question by ID
         *
         * @param questionId Question ID
         * @return QuizQuestion entity
         * @throws QuestionNotFoundException if question not found
         */
        private QuizQuestion loadQuestion(Long questionId) {
                return questionRepository.findById(questionId)
                                .orElseThrow(() -> new QuestionNotFoundException(
                                                String.format("Question %d not found", questionId)));
        }

        /**
         * Validate that the question is eligible for answer submission.
         * Rejects inactive or unpublished questions to prevent tampered requests
         * targeting questions that were never delivered.
         *
         * @param question QuizQuestion entity
         * @throws InvalidAnswerException if question is not active or not published
         */
        private void validateQuestionEligibility(QuizQuestion question) {
                if (!Boolean.TRUE.equals(question.getIsActive())) {
                        log.warn("Submission rejected: question {} is inactive", question.getId());
                        throw new InvalidAnswerException(
                                        String.format("Question %d is not available for answers", question.getId()));
                }
                if (question.getStatus() != QuizQuestion.QuestionStatus.PUBLISHED) {
                        log.warn("Submission rejected: question {} has status {}", question.getId(),
                                        question.getStatus());
                        throw new InvalidAnswerException(
                                        String.format("Question %d is not available for answers", question.getId()));
                }
        }

        /**
         * Validate selected option exists and belongs to question
         *
         * @param selectedOptionId Selected option ID
         * @param questionId       Question ID
         * @return Validated QuizAnswerOption
         * @throws InvalidAnswerException if option invalid or doesn't belong to
         *                                question
         */
        private QuizAnswerOption validateSelectedOption(Long selectedOptionId, Long questionId) {
                QuizAnswerOption selectedOption = optionRepository
                                .findById(selectedOptionId)
                                .orElseThrow(() -> new InvalidAnswerException(
                                                String.format("Invalid option ID: %d", selectedOptionId)));

                if (!selectedOption.getQuestion().getId().equals(questionId)) {
                        throw new InvalidAnswerException(
                                        String.format("Option %d does not belong to question %d",
                                                        selectedOptionId, questionId));
                }

                return selectedOption;
        }

        /**
         * Get correct option from question
         *
         * @param question QuizQuestion entity
         * @return Correct QuizAnswerOption
         * @throws IllegalStateException if question has no correct answer
         */
        private QuizAnswerOption getCorrectOption(QuizQuestion question) {
                return question.getOptions().stream()
                                .filter(QuizAnswerOption::getIsCorrect)
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException(
                                                String.format("Question %d has no correct answer", question.getId())));
        }

        /**
         * Record answer in user question history
         *
         * @param userId           User ID
         * @param questionId       Question ID
         * @param isCorrect        Whether answer was correct
         * @param timeTakenSeconds Time taken (nullable)
         */
        private void recordAnswerHistory(Long userId, Long questionId, boolean isCorrect, Integer timeTakenSeconds) {
                int timeTaken = timeTakenSeconds != null ? timeTakenSeconds : 0;

                // Use upsert to handle the case where the question was already shown previously.
                // historyRepository.save() causes DataIntegrityViolationException (Duplicate Key)
                // because a prior delivery may already have INSERT'd a row for the same (user_id, question_ref_id).
                historyRepository.upsertQuestionAnswered(userId, questionId, LocalDateTime.now(), isCorrect, timeTaken);

                log.debug("Recorded answer in history: userId={}, questionId={}, isCorrect={}",
                                userId, questionId, isCorrect);
        }

        /**
         * Get time taken from request with default value
         *
         * @param request SubmitPracticeAnswerRequest
         * @return Time taken in seconds (0 if null)
         */
        private int getTimeTaken(SubmitPracticeAnswerRequest request) {
                return request.getTimeTakenSeconds() != null ? request.getTimeTakenSeconds() : 0;
        }

        /**
         * Convert accuracy rate to BigDecimal with proper precision
         *
         * @param progress UserCategoryProgress entity
         * @return BigDecimal accuracy with 2 decimal places, or ZERO if null
         */
        private BigDecimal getAccuracyAsBigDecimal(UserCategoryProgress progress) {
                if (progress.getAccuracyRate() == null) {
                        return BigDecimal.ZERO;
                }

                // Handle both Double and BigDecimal types from entity
                Object accuracyRate = progress.getAccuracyRate();

                if (accuracyRate instanceof BigDecimal) {
                        return ((BigDecimal) accuracyRate).setScale(2, RoundingMode.HALF_UP);
                } else if (accuracyRate instanceof Double) {
                        return BigDecimal.valueOf((Double) accuracyRate)
                                        .setScale(2, RoundingMode.HALF_UP);
                } else {
                        log.warn("Unexpected accuracy rate type: {}", accuracyRate.getClass().getName());
                        return BigDecimal.ZERO;
                }
        }

        /**
         * Build comprehensive response with all feedback data
         *
         * @param questionId     Question ID
         * @param isCorrect      Whether answer was correct
         * @param selectedOption Selected answer option
         * @param correctOption  Correct answer option
         * @param question       Full question entity
         * @param progress       Updated user progress
         * @return Complete response DTO
         */
        private SubmitPracticeAnswerResponse buildResponse(
                        Long questionId,
                        boolean isCorrect,
                        QuizAnswerOption selectedOption,
                        QuizAnswerOption correctOption,
                        QuizQuestion question,
                        UserCategoryProgress progress) {

                return SubmitPracticeAnswerResponse.builder()
                                // Question info
                                .questionId(questionId)
                                .isCorrect(isCorrect)

                                // Selected option (multi-language)
                                .selectedOptionId(selectedOption.getId())
                                .selectedOptionTextEn(selectedOption.getOptionTextEn())
                                .selectedOptionTextAr(selectedOption.getOptionTextAr())
                                .selectedOptionTextNl(selectedOption.getOptionTextNl())
                                .selectedOptionTextFr(selectedOption.getOptionTextFr())

                                // Correct option (multi-language)
                                .correctOptionId(correctOption.getId())
                                .correctOptionTextEn(correctOption.getOptionTextEn())
                                .correctOptionTextAr(correctOption.getOptionTextAr())
                                .correctOptionTextNl(correctOption.getOptionTextNl())
                                .correctOptionTextFr(correctOption.getOptionTextFr())

                                // Explanation (multi-language)
                                .explanationEn(question.getExplanationEn())
                                .explanationAr(question.getExplanationAr())
                                .explanationNl(question.getExplanationNl())
                                .explanationFr(question.getExplanationFr())

                                // Category info (multi-language)
                                .categoryId(question.getCategory().getId())
                                .categoryNameEn(question.getCategory().getNameEn())
                                .categoryNameAr(question.getCategory().getNameAr())
                                .categoryNameNl(question.getCategory().getNameNl())
                                .categoryNameFr(question.getCategory().getNameFr())

                                // Updated progress
                                .updatedAccuracy(getAccuracyAsBigDecimal(progress))
                                .totalAttempts(progress.getQuestionsAttempted())
                                .correctAttempts(progress.getCorrectAnswers())
                                .masteryLevel(progress.getMasteryLevel().name())

                                .build();
        }

        /**
         * Update user's category progress
         *
         * Creates new progress record if doesn't exist,
         * otherwise updates existing with new attempt.
         *
         * @param userId           User ID
         * @param category         Category entity
         * @param isCorrect        Whether answer was correct
         * @param difficulty       Question difficulty level (reserved for future use)
         * @param timeTakenSeconds Time taken to answer (reserved for future use)
         * @return Updated progress record
         */
        private UserCategoryProgress updateCategoryProgress(
                        Long userId,
                        Category category,
                        boolean isCorrect,
                        QuizQuestion.DifficultyLevel difficulty,
                        int timeTakenSeconds) {

                // Find existing progress or create new
                UserCategoryProgress progress = progressRepository
                                .findByUserIdAndCategoryId(userId, category.getId())
                                .orElseGet(() -> createNewProgress(userId, category));

                // Update attempt counters
                progress.setQuestionsAttempted(progress.getQuestionsAttempted() + 1);
                if (isCorrect) {
                        progress.setCorrectAnswers(progress.getCorrectAnswers() + 1);
                }

                // Update last practiced timestamp
                progress.setLastPracticed(LocalDateTime.now());

                // Recalculate accuracy and mastery level
                progress.updateAccuracy();

                // Save and return
                progress = progressRepository.save(progress);

                log.debug("Updated category progress: userId={}, categoryId={}, accuracy={}, mastery={}",
                                userId, category.getId(), progress.getAccuracyRate(), progress.getMasteryLevel());

                return progress;
        }

        /**
         * Create new progress record for user and category
         *
         * @param userId   User ID
         * @param category Category entity
         * @return New UserCategoryProgress with initial values
         */
        private UserCategoryProgress createNewProgress(Long userId, Category category) {
                log.debug("Creating new progress record: userId={}, categoryId={}",
                                userId, category.getId());

                UserCategoryProgress newProgress = new UserCategoryProgress();
                newProgress.setUserId(userId);
                newProgress.setCategoryId(category.getId());
                newProgress.setCategory(category);
                newProgress.setQuestionsAttempted(0);
                newProgress.setCorrectAnswers(0);
                newProgress.setMasteryLevel(UserCategoryProgress.MasteryLevel.BEGINNER);

                return newProgress;
        }
}
