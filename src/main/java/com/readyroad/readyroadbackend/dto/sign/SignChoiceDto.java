package com.readyroad.readyroadbackend.dto.sign;

import com.readyroad.readyroadbackend.domain.entity.SignChoice;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;
import com.readyroad.readyroadbackend.util.SignQuestionTextSanitizer;

/**
 * A single answer choice — intentionally omits {@code isCorrect}
 * so the client cannot determine the answer before submitting.
 */
public record SignChoiceDto(
        Long   id,
        int    displayOrder,
        String textNl,
        String textEn,
        String textFr,
        String textAr
) {
    public static SignChoiceDto from(SignChoice c) {
        return from(c, null);
    }

    public static SignChoiceDto from(SignChoice c, SignQuestionType questionType) {
        return new SignChoiceDto(
                c.getId(),
                c.getDisplayOrder() != null ? c.getDisplayOrder() : 0,
                SignQuestionTextSanitizer.sanitizeChoice(questionType, c.getTextNl()),
                SignQuestionTextSanitizer.sanitizeChoice(questionType, c.getTextEn()),
                SignQuestionTextSanitizer.sanitizeChoice(questionType, c.getTextFr()),
                SignQuestionTextSanitizer.sanitizeChoice(questionType, c.getTextAr())
        );
    }
}
