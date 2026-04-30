package com.readyroad.readyroadbackend.dto.sign;

import com.readyroad.readyroadbackend.domain.entity.SignChoice;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;
import com.readyroad.readyroadbackend.service.RoadSignReferenceTextResolver;
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
        return from(c, questionType, null);
    }

    public static SignChoiceDto from(SignChoice c, SignQuestionType questionType, RoadSignReferenceTextResolver resolver) {
        return new SignChoiceDto(
                c.getId(),
                c.getDisplayOrder() != null ? c.getDisplayOrder() : 0,
                resolve(resolver, Language.NL, questionType, c.getTextNl()),
                resolve(resolver, Language.EN, questionType, c.getTextEn()),
                resolve(resolver, Language.FR, questionType, c.getTextFr()),
                resolve(resolver, Language.AR, questionType, c.getTextAr())
        );
    }

    private static String resolve(
            RoadSignReferenceTextResolver resolver,
            Language language,
            SignQuestionType questionType,
        String value) {
        String sanitized = SignQuestionTextSanitizer.sanitizeChoice(questionType, language.name(), value);
        if (resolver == null) {
            return sanitized;
        }
        return switch (language) {
            case NL -> resolver.resolveNl(sanitized);
            case EN -> resolver.resolveEn(sanitized);
            case FR -> resolver.resolveFr(sanitized);
            case AR -> resolver.resolveAr(sanitized);
        };
    }

    private enum Language {
        NL,
        EN,
        FR,
        AR
    }
}
