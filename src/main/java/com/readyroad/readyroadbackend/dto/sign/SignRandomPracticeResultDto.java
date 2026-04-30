package com.readyroad.readyroadbackend.dto.sign;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Result of a persisted mixed-sign random practice session.
 */
public record SignRandomPracticeResultDto(
        Long sessionId,
        String status,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        int    totalQuestions,
        int    answeredCount,
        int    correctAnswers,
        int    wrongAnswers,
        int    unanswered,
        double scorePercentage,
        boolean passed,
        int    passingScore,
        List<QuestionResult> questions
) {

    public record QuestionResult(
            Long   questionId,
            String questionNl,
            String questionEn,
            String questionFr,
            String questionAr,
            Long   selectedChoiceId,
            Long   correctChoiceId,
            String correctChoiceNl,
            String correctChoiceEn,
            String correctChoiceFr,
            String correctChoiceAr,
            boolean isCorrect,
            boolean wasTimeout,
            String explanationNl,
            String explanationEn,
            String explanationFr,
            String explanationAr,
            String signCode,
            String signImagePath,
            String difficulty
    ) {}
}
