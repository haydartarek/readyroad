package com.readyroad.readyroadbackend.dto.sign;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Returned after a stateless exam submission.
 * Reveals correctness for every question (post-exam feedback).
 */
public record SignExamResultDto(
        Long resultId,
        String signCode,
        String routeCode,
        String signImagePath,
        String nameNl,
        String nameEn,
        String nameFr,
        String nameAr,
        int    examNumber,
        LocalDateTime completedAt,

        // ── Score summary ──────────────────────────────────────────────────
        int    totalLinked,
        int    answeredCount,
        int    unansweredCount,
        int    correctAnswers,
        int    wrongAnswers,
        double scorePercentage,

    /** Minimum correct answers needed to pass (configured exam passing score). */
        int     passingThreshold,
        boolean passed,
        /** "PASSED" or "FAILED" */
        String  resultStatus,

        // ── Per-question breakdown (revealed after exam) ───────────────────
        List<ExamQuestionResult> questionResults
) {

    // ── Nested DTO ────────────────────────────────────────────────────────────

    public record ExamQuestionResult(
            Long   questionId,
            String questionRef,
            String difficulty,
            String questionNl,
            String questionEn,
            String questionFr,
            String questionAr,

            /** True if the user included this question in the submission. */
            boolean answered,
            /** null when not answered. */
            Boolean isCorrect,

            Long   selectedChoiceId,

            Long   correctChoiceId,
            String correctTextNl,
            String correctTextEn,
            String correctTextFr,
            String correctTextAr,

            String explanationNl,
            String explanationEn,
            String explanationFr,
            String explanationAr
    ) {}
}
