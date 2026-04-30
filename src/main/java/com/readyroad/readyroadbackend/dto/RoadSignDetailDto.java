package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.entity.SignChoice;
import com.readyroad.readyroadbackend.domain.entity.SignQuestion;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.enums.SignDifficulty;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;
import com.readyroad.readyroadbackend.service.RoadSignReferenceTextResolver;
import com.readyroad.readyroadbackend.util.DrivingTextSanitizer;
import com.readyroad.readyroadbackend.util.SignQuestionTextSanitizer;

import java.util.List;

/**
 * Full detail of a road sign — includes all multilingual fields,
 * plus all linked questions with their answer choices.
 *
 * <p>Must be populated inside a JPA transaction so that lazy collections
 * (questions → choices) are initialized before the session closes.</p>
 */
public record RoadSignDetailDto(
        Long              id,
        String            signCode,
        SignCategory      category,
        String            imagePath,
        boolean           seriousViolation,

        // Multilingual names
        String            nameNl,
        String            nameEn,
        String            nameFr,
        String            nameAr,

        // Multilingual descriptions
        String            descriptionNl,
        String            descriptionEn,
        String            descriptionFr,
        String            descriptionAr,

        boolean           isActive,
        List<QuestionDto> questions
) {

    // ── Nested: Question ────────────────────────────────────────────────────

    public record QuestionDto(
            Long             id,
            String           questionRef,
            SignQuestionType questionType,
            SignDifficulty   difficulty,
            boolean          isCritical,
            boolean          showSign,

            // Multilingual question text
            String           questionNl,
            String           questionEn,
            String           questionFr,
            String           questionAr,

            // Multilingual explanation
            String           explanationNl,
            String           explanationEn,
            String           explanationFr,
            String           explanationAr,

            List<ChoiceDto>  choices
    ) {

        // ── Nested: Choice ──────────────────────────────────────────────────

        public record ChoiceDto(
                Long    id,
                int     displayOrder,
                boolean isCorrect,
                String  textNl,
                String  textEn,
                String  textFr,
                String  textAr
        ) {
            public static ChoiceDto from(SignChoice c) {
                return from(c, null);
            }

            public static ChoiceDto from(SignChoice c, RoadSignReferenceTextResolver resolver) {
                return new ChoiceDto(
                        c.getId(),
                        c.getDisplayOrder() != null ? c.getDisplayOrder() : 0,
                        Boolean.TRUE.equals(c.getIsCorrect()),
                        resolveChoice(resolver, c.getQuestion() != null ? c.getQuestion().getQuestionType() : null, c.getTextNl(), "NL"),
                        resolveChoice(resolver, c.getQuestion() != null ? c.getQuestion().getQuestionType() : null, c.getTextEn(), "EN"),
                        resolveChoice(resolver, c.getQuestion() != null ? c.getQuestion().getQuestionType() : null, c.getTextFr(), "FR"),
                        resolveChoice(resolver, c.getQuestion() != null ? c.getQuestion().getQuestionType() : null, c.getTextAr(), "AR")
                );
            }

            static String resolve(RoadSignReferenceTextResolver resolver, String value, String language) {
                String sanitized = DrivingTextSanitizer.sanitize(language, value);
                if (resolver == null) {
                    return sanitized;
                }

                return switch (language) {
                    case "NL" -> resolver.resolveNl(sanitized);
                    case "EN" -> resolver.resolveEn(sanitized);
                    case "FR" -> resolver.resolveFr(sanitized);
                    case "AR" -> resolver.resolveAr(sanitized);
                    default -> sanitized;
                };
            }

            static String resolveChoice(
                    RoadSignReferenceTextResolver resolver,
                    SignQuestionType questionType,
                    String value,
                    String language) {
                String sanitized = questionType == null
                        ? DrivingTextSanitizer.sanitize(language, value)
                        : SignQuestionTextSanitizer.sanitizeChoice(questionType, language, value);
                if (resolver == null) {
                    return sanitized;
                }

                return switch (language) {
                    case "NL" -> resolver.resolveNl(sanitized);
                    case "EN" -> resolver.resolveEn(sanitized);
                    case "FR" -> resolver.resolveFr(sanitized);
                    case "AR" -> resolver.resolveAr(sanitized);
                    default -> sanitized;
                };
            }
        }

        public static QuestionDto from(SignQuestion q) {
            return from(q, null);
        }

        public static QuestionDto from(SignQuestion q, RoadSignReferenceTextResolver resolver) {
            return new QuestionDto(
                    q.getId(),
                    q.getQuestionRef(),
                    q.getQuestionType(),
                    q.getDifficulty(),
                    Boolean.TRUE.equals(q.getIsCritical()),
                    Boolean.TRUE.equals(q.getShowSign()),
                    resolveQuestion(resolver, q.getQuestionType(), q.getQuestionNl(), "NL"),
                    resolveQuestion(resolver, q.getQuestionType(), q.getQuestionEn(), "EN"),
                    resolveQuestion(resolver, q.getQuestionType(), q.getQuestionFr(), "FR"),
                    resolveQuestion(resolver, q.getQuestionType(), q.getQuestionAr(), "AR"),
                    resolveExplanation(resolver, q.getQuestionType(), q.getExplanationNl(), "NL"),
                    resolveExplanation(resolver, q.getQuestionType(), q.getExplanationEn(), "EN"),
                    resolveExplanation(resolver, q.getQuestionType(), q.getExplanationFr(), "FR"),
                    resolveExplanation(resolver, q.getQuestionType(), q.getExplanationAr(), "AR"),
                    q.getDeliverableChoices().stream().map(choice -> ChoiceDto.from(choice, resolver)).toList()
            );
        }

        private static String resolveQuestion(
                RoadSignReferenceTextResolver resolver,
                SignQuestionType questionType,
                String value,
                String language) {
            String sanitized = SignQuestionTextSanitizer.sanitizeQuestion(questionType, language, value);
            return ChoiceDto.resolve(resolver, sanitized, language);
        }

        private static String resolveExplanation(
                RoadSignReferenceTextResolver resolver,
                SignQuestionType questionType,
                String value,
                String language) {
            String sanitized = SignQuestionTextSanitizer.sanitizeExplanation(questionType, language, value);
            return ChoiceDto.resolve(resolver, sanitized, language);
        }
    }

    // ── Factory ─────────────────────────────────────────────────────────────

    /**
     * Map from JPA entity to DTO.
     * Must be called while a JPA transaction is active so lazy collections load.
     */
    public static RoadSignDetailDto from(RoadSign s) {
        return from(s, null);
    }

    public static RoadSignDetailDto from(RoadSign s, RoadSignReferenceTextResolver resolver) {
        return new RoadSignDetailDto(
                s.getId(),
                s.getSignCode(),
                s.getCategory(),
                s.getImagePath(),
                Boolean.TRUE.equals(s.getSeriousViolation()),
                s.getNameNl(),
                s.getNameEn(),
                s.getNameFr(),
                s.getNameAr(),
                s.getDescriptionNl(),
                s.getDescriptionEn(),
                s.getDescriptionFr(),
                s.getDescriptionAr(),
                Boolean.TRUE.equals(s.getIsActive()),
                s.getQuestions().stream()
                        .filter(q -> Boolean.TRUE.equals(q.getIsActive()))
                        .map(q -> QuestionDto.from(q, resolver))
                        .toList()
        );
    }
}
