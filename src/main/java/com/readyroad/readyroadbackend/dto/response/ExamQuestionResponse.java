package com.readyroad.readyroadbackend.dto.response;

import java.util.List;

public record ExamQuestionResponse(
        Long id,
        Long categoryId,
        String categoryCode,
        String questionAr,
        String questionEn,
        String questionNl,
        String questionFr,
        List<OptionResponse> options,
        Integer correctAnswer,
        String explanationAr,
        String explanationEn,
        String explanationNl,
        String explanationFr,
        String imageUrl,
        String difficulty,
        Boolean isImportant
) {
    public record OptionResponse(
            Integer number,
            String textAr,
            String textEn,
            String textNl,
            String textFr
    ) {
    }
}
