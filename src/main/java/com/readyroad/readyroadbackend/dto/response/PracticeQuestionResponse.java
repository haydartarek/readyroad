package com.readyroad.readyroadbackend.dto.response;

import java.util.List;

public record PracticeQuestionResponse(
        Long id,
        Long lessonId,
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
        Integer displayOrder
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
