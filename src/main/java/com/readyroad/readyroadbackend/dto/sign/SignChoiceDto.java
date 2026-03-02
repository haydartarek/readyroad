package com.readyroad.readyroadbackend.dto.sign;

import com.readyroad.readyroadbackend.domain.entity.SignChoice;

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
        return new SignChoiceDto(
                c.getId(),
                c.getDisplayOrder() != null ? c.getDisplayOrder() : 0,
                c.getTextNl(),
                c.getTextEn(),
                c.getTextFr(),
                c.getTextAr()
        );
    }
}
