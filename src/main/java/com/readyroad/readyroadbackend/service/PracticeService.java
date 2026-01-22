package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.model.UserQuestionHistory;
import com.readyroad.readyroadbackend.domain.repository.*;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerRequest;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerResponse;
import com.readyroad.readyroadbackend.exception.InvalidAnswerException;
import com.readyroad.readyroadbackend.exception.QuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @param userId User ID
     * @param questionId Question ID
     * @param request Answer submission request
     * @return Response with immediate feedback and updated progress
     * @throws QuestionNotFoundException if question doesn't exist
     * @throws InvalidAnswerException if selected option is invalid
     */
    @Transactional
    public SubmitPracticeAnswerResponse submitPracticeAnswer(
            Long userId,
            Long questionId,
            SubmitPracticeAnswerRequest request) {

        log.info("Processing practice answer submission: userId={}, questionId={}, optionId={}",
            userId, questionId, request.getSelectedOptionId());

        // 1. Load question with all relationships
        QuizQuestion question = questionRepository.findById(questionId)
            .orElseThrow(() -> new QuestionNotFoundException(
                String.format("Question %d not found", questionId)
            ));

        // 2. Validate selected option
        QuizAnswerOption selectedOption = optionRepository
            .findById(request.getSelectedOptionId())
            .orElseThrow(() -> new InvalidAnswerException(
                String.format("Invalid option ID: %d", request.getSelectedOptionId())
            ));

        // Verify option belongs to this question
        if (!selectedOption.getQuestion().getId().equals(questionId)) {
            throw new InvalidAnswerException(
                String.format("Option %d does not belong to question %d",
                    request.getSelectedOptionId(), questionId)
            );
        }

        // 3. Check correctness
        boolean isCorrect = selectedOption.getIsCorrect();
        log.debug("Answer is {}: questionId={}, selectedOption={}",
            isCorrect ? "CORRECT" : "INCORRECT", questionId, selectedOption.getId());

        // 4. Get correct option for response
        QuizAnswerOption correctOption = question.getOptions().stream()
            .filter(QuizAnswerOption::getIsCorrect)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                String.format("Question %d has no correct answer", questionId)
            ));

        // 5. Record in user_question_history (Law #1: 24h cooldown enforcement)
        int timeTaken = request.getTimeTakenSeconds() != null ? request.getTimeTakenSeconds() : 0;
        UserQuestionHistory history = UserQuestionHistory.builder()
            .userId(userId)
            .questionId(question.getId())
            .answeredAt(LocalDateTime.now())
            .isCorrect(isCorrect)
            .timeTakenSeconds(timeTaken)
            .build();
        historyRepository.save(history);
        log.debug("Recorded answer in history: userId={}, questionId={}, isCorrect={}",
            userId, questionId, isCorrect);

        // 6. Update category progress
        UserCategoryProgress progress = updateCategoryProgress(
            userId,
            question.getCategory(),
            isCorrect,
            question.getDifficultyLevel(),
            timeTaken
        );

        // 7. Build comprehensive response
        SubmitPracticeAnswerResponse response = SubmitPracticeAnswerResponse.builder()
            // Question info
            .questionId(questionId)

            // Correctness
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
            .updatedAccuracy(progress.getAccuracyRate() != null
                ? java.math.BigDecimal.valueOf(progress.getAccuracyRate()).setScale(2, java.math.RoundingMode.HALF_UP)
                : java.math.BigDecimal.ZERO)
            .totalAttempts(progress.getQuestionsAttempted())
            .correctAttempts(progress.getCorrectAnswers())
            .masteryLevel(progress.getMasteryLevel().name())

            .build();

        log.info("✅ Practice answer processed: userId={}, questionId={}, isCorrect={}, accuracy={}",
            userId, questionId, isCorrect, progress.getAccuracyRate());

        return response;
    }

    /**
     * Update user's category progress
     *
     * Creates new progress record if doesn't exist,
     * otherwise updates existing with new attempt.
     *
     * @param userId User ID
     * @param category Category
     * @param isCorrect Whether answer was correct
     * @param difficulty Question difficulty level (not currently tracked, reserved for future)
     * @param timeTakenSeconds Time taken to answer (not currently tracked, reserved for future)
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
            .orElseGet(() -> {
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
            });

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
}
