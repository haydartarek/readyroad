package com.readyroad.readyroadbackend.dto.sign;

import com.readyroad.readyroadbackend.domain.entity.SignChoice;
import com.readyroad.readyroadbackend.domain.entity.SignPracticeAnswer;
import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession;
import com.readyroad.readyroadbackend.domain.entity.SignQuestion;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;
import com.readyroad.readyroadbackend.service.RoadSignReferenceTextResolver;
import com.readyroad.readyroadbackend.util.SignQuestionTextSanitizer;

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
            return from(a, null);
        }

        public static QuestionResultItem from(SignPracticeAnswer a, RoadSignReferenceTextResolver resolver) {
            SignQuestion q      = a.getQuestion();
            SignChoice   picked = a.getChoice();
            SignQuestionType questionType = q.getQuestionType();

            // Find the correct choice (the one with isCorrect=true)
            SignChoice correct = q.getDeliverableChoices().stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsCorrect()))
                    .findFirst()
                    .orElse(picked); // fallback (should never happen)

            return new QuestionResultItem(
                    q.getId(),
                    q.getQuestionRef(),
                    q.getDifficulty().name(),
                    resolveSanitizedQuestion(resolver, Language.NL, questionType, q.getQuestionNl()),
                    resolveSanitizedQuestion(resolver, Language.EN, questionType, q.getQuestionEn()),
                    resolveSanitizedQuestion(resolver, Language.FR, questionType, q.getQuestionFr()),
                    resolveSanitizedQuestion(resolver, Language.AR, questionType, q.getQuestionAr()),
                    Boolean.TRUE.equals(a.getIsCorrect()),

                    picked.getId(),
                    resolveSanitizedChoice(resolver, Language.NL, questionType, picked.getTextNl()),
                    resolveSanitizedChoice(resolver, Language.EN, questionType, picked.getTextEn()),
                    resolveSanitizedChoice(resolver, Language.FR, questionType, picked.getTextFr()),
                    resolveSanitizedChoice(resolver, Language.AR, questionType, picked.getTextAr()),

                    correct.getId(),
                    resolveSanitizedChoice(resolver, Language.NL, questionType, correct.getTextNl()),
                    resolveSanitizedChoice(resolver, Language.EN, questionType, correct.getTextEn()),
                    resolveSanitizedChoice(resolver, Language.FR, questionType, correct.getTextFr()),
                    resolveSanitizedChoice(resolver, Language.AR, questionType, correct.getTextAr()),

                    resolveSanitizedExplanation(resolver, Language.NL, questionType, q.getExplanationNl()),
                    resolveSanitizedExplanation(resolver, Language.EN, questionType, q.getExplanationEn()),
                    resolveSanitizedExplanation(resolver, Language.FR, questionType, q.getExplanationFr()),
                    resolveSanitizedExplanation(resolver, Language.AR, questionType, q.getExplanationAr())
            );
        }

        private static String resolve(
                RoadSignReferenceTextResolver resolver,
                Language language,
                String value) {
            if (resolver == null) {
                return value;
            }
            return switch (language) {
                case NL -> resolver.resolveNl(value);
                case EN -> resolver.resolveEn(value);
                case FR -> resolver.resolveFr(value);
                case AR -> resolver.resolveAr(value);
            };
        }

        private static String resolveSanitizedChoice(
                RoadSignReferenceTextResolver resolver,
                Language language,
                SignQuestionType questionType,
                String value) {
            String sanitized = SignQuestionTextSanitizer.sanitizeChoice(questionType, language.name(), value);
            return resolve(resolver, language, sanitized);
        }

        private static String resolveSanitizedQuestion(
                RoadSignReferenceTextResolver resolver,
                Language language,
                SignQuestionType questionType,
                String value) {
            String sanitized = SignQuestionTextSanitizer.sanitizeQuestion(questionType, language.name(), value);
            return resolve(resolver, language, sanitized);
        }

        private static String resolveSanitizedExplanation(
                RoadSignReferenceTextResolver resolver,
                Language language,
                SignQuestionType questionType,
                String value) {
            String sanitized = SignQuestionTextSanitizer.sanitizeExplanation(questionType, language.name(), value);
            return resolve(resolver, language, sanitized);
        }

        private enum Language {
            NL,
            EN,
            FR,
            AR
        }
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    public static SignPracticeResultDto from(SignPracticeSession s,
                                             List<SignPracticeAnswer> answers) {
        return from(s, answers, null);
    }

    public static SignPracticeResultDto from(
            SignPracticeSession s,
            List<SignPracticeAnswer> answers,
            RoadSignReferenceTextResolver resolver) {
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
                answers.stream().map(answer -> QuestionResultItem.from(answer, resolver)).toList()
        );
    }
}
