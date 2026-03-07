package com.readyroad.readyroadbackend.dto.sign;

import java.util.List;

/**
 * Result of a random-sign-practice check (stateless, no DB session).
 * Returned by POST /api/sign-quiz/random-practice/check
 */
public record SignRandomPracticeResultDto(
        int    totalQuestions,
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
