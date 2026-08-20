package com.readyroad.readyroadbackend.dto;

import java.time.Instant;
import java.util.List;

public record TheoryTimeoutAnalysisResponse(
        long totalTimeouts,
        List<TheoryTimeoutItem> items) {

    public record TheoryTimeoutItem(
            Long examId,
            Long questionId,
            String questionTextEn,
            String questionTextNl,
            String questionTextFr,
            String questionTextAr,
            String categoryCode,
            String categoryNameEn,
            String categoryNameNl,
            String categoryNameFr,
            String categoryNameAr,
            String difficulty,
            Instant timedOutAt,
            String reviewPath) {
    }
}
