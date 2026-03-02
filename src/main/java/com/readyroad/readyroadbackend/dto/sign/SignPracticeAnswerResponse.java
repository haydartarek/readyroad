package com.readyroad.readyroadbackend.dto.sign;

/**
 * Immediate feedback returned after submitting one practice answer.
 *
 * <p>Contains the correct answer text and explanation so the user
 * can learn from mistakes immediately (unlike exam mode).</p>
 */
public record SignPracticeAnswerResponse(

        Long    questionId,
        boolean isCorrect,

        // ── Selected choice (what the user picked) ─────────────────────────
        Long   selectedChoiceId,
        String selectedTextNl,
        String selectedTextEn,
        String selectedTextFr,
        String selectedTextAr,

        // ── Correct choice (revealed immediately in practice) ───────────────
        Long   correctChoiceId,
        String correctTextNl,
        String correctTextEn,
        String correctTextFr,
        String correctTextAr,

        // ── Explanation ────────────────────────────────────────────────────
        String explanationNl,
        String explanationEn,
        String explanationFr,
        String explanationAr,

        // ── Session progress ───────────────────────────────────────────────
        int     questionsAnswered,
        int     totalQuestions,
        /** True when all questions in the session have been answered. */
        boolean sessionCompleted,

        // ── User's overall accuracy for this sign (from user_weak_areas) ───
        double  signAccuracyPercentage,
        int     signTotalAttempts
) {}
