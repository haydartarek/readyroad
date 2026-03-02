package com.readyroad.readyroadbackend.dto.sign;

import com.readyroad.readyroadbackend.domain.entity.SignChoice;
import com.readyroad.readyroadbackend.domain.entity.SignPracticeAnswer;
import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession;
import com.readyroad.readyroadbackend.domain.entity.SignQuestion;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Full results of a completed (or in-progress) practice session.
 */
public record SignPracticeResultDto(
        Long          sessionId,
        String        signCode,
        String        nameNl,
        String        nameEn,
        String        nameFr,
        String        nameAr,
        String        status,
        int           totalQuestions,
        int           correctAnswers,
        int           wrongAnswers,
        double        scorePercentage,
        /** A session is considered passed when ≥ 80% correct. */
        boolean       passed,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        List<QuestionResultItem> questionResults
) {

    // ── Nested DTO ────────────────────────────────────────────────────────────

    public record QuestionResultItem(
            Long   questionId,
            String questionRef,
            String difficulty,
            String questionNl,
            String questionEn,
            String questionFr,
            String questionAr,
            boolean isCorrect,

            Long   selectedChoiceId,
            String selectedTextNl,
            String selectedTextEn,
            String selectedTextFr,
            String selectedTextAr,

            Long   correctChoiceId,
            String correctTextNl,
            String correctTextEn,
            String correctTextFr,
            String correctTextAr,

            String explanationNl,
            String explanationEn,
            String explanationFr,
            String explanationAr
    ) {
        public static QuestionResultItem from(SignPracticeAnswer a) {
            SignQuestion q      = a.getQuestion();
            SignChoice   picked = a.getChoice();

            // Find the correct choice (the one with isCorrect=true)
            SignChoice correct = q.getChoices().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsCorrect()))
                    .findFirst()
                    .orElse(picked); // fallback (should never happen)

            return new QuestionResultItem(
                    q.getId(),
                    q.getQuestionRef(),
                    q.getDifficulty().name(),
                    q.getQuestionNl(), q.getQuestionEn(),
                    q.getQuestionFr(), q.getQuestionAr(),
                    Boolean.TRUE.equals(a.getIsCorrect()),

                    picked.getId(),
                    picked.getTextNl(), picked.getTextEn(),
                    picked.getTextFr(), picked.getTextAr(),

                    correct.getId(),
                    correct.getTextNl(), correct.getTextEn(),
                    correct.getTextFr(), correct.getTextAr(),

                    q.getExplanationNl(), q.getExplanationEn(),
                    q.getExplanationFr(), q.getExplanationAr()
            );
        }
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    public static SignPracticeResultDto from(SignPracticeSession s,
                                             List<SignPracticeAnswer> answers) {
        int correct = s.getCorrectCount();
        int total   = answers.size();
        double pct  = total == 0 ? 0.0 : (correct * 100.0 / total);

        return new SignPracticeResultDto(
                s.getId(),
                s.getSignCode(),
                s.getSign().getNameNl(),
                s.getSign().getNameEn(),
                s.getSign().getNameFr(),
                s.getSign().getNameAr(),
                s.getStatus().name(),
                s.getTotalQuestions(),
                correct,
                total - correct,
                Math.round(pct * 100.0) / 100.0,
                pct >= 80.0,
                s.getStartedAt(),
                s.getCompletedAt(),
                answers.stream().map(QuestionResultItem::from).toList()
        );
    }
}
