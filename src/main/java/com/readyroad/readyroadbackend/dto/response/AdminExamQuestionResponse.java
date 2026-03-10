package com.readyroad.readyroadbackend.dto.response;

import java.time.LocalDateTime;

/**
 * Admin-facing exam question response with all details including all 4 options.
 */
public record AdminExamQuestionResponse(
                Long id,
                String categoryCode,
                String categoryNameEn,
                String categoryNameAr,
                String categoryNameNl,
                String categoryNameFr,
                String questionEn,
                String questionAr,
                String questionNl,
                String questionFr,
                String option1En,
                String option1Ar,
                String option1Nl,
                String option1Fr,
                String option2En,
                String option2Ar,
                String option2Nl,
                String option2Fr,
                String option3En,
                String option3Ar,
                String option3Nl,
                String option3Fr,
                String option4En,
                String option4Ar,
                String option4Nl,
                String option4Fr,
                Integer correctAnswer,
                String explanationEn,
                String explanationAr,
                String explanationNl,
                String explanationFr,
                String imageUrl,
                String difficulty,
                Boolean isImportant,
                Boolean isActive,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
