package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.entity.SignChoice;
import com.readyroad.readyroadbackend.domain.entity.SignQuestion;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.enums.SignDifficulty;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;

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
                return new ChoiceDto(
                        c.getId(),
                        c.getDisplayOrder() != null ? c.getDisplayOrder() : 0,
                        Boolean.TRUE.equals(c.getIsCorrect()),
                        c.getTextNl(),
                        c.getTextEn(),
                        c.getTextFr(),
                        c.getTextAr()
                );
            }
        }

        public static QuestionDto from(SignQuestion q) {
            return new QuestionDto(
                    q.getId(),
                    q.getQuestionRef(),
                    q.getQuestionType(),
                    q.getDifficulty(),
                    Boolean.TRUE.equals(q.getIsCritical()),
                    Boolean.TRUE.equals(q.getShowSign()),
                    q.getQuestionNl(),
                    q.getQuestionEn(),
                    q.getQuestionFr(),
                    q.getQuestionAr(),
                    q.getExplanationNl(),
                    q.getExplanationEn(),
                    q.getExplanationFr(),
                    q.getExplanationAr(),
                    q.getChoices().stream().map(ChoiceDto::from).toList()
            );
        }
    }

    // ── Factory ─────────────────────────────────────────────────────────────

    /**
     * Map from JPA entity to DTO.
     * Must be called while a JPA transaction is active so lazy collections load.
     */
    public static RoadSignDetailDto from(RoadSign s) {
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
                        .map(QuestionDto::from)
                        .toList()
        );
    }
}
