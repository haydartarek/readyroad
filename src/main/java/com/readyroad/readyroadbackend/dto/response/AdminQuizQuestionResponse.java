package com.readyroad.readyroadbackend.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin-facing quiz question response with all details.
 */
public record AdminQuizQuestionResponse(
                Long id,
                String categoryCode,
                String categoryNameEn,
                String difficultyLevel,
                String questionType,
                String questionEn,
                String questionAr,
                String questionNl,
                String questionFr,
                String contentImageUrl,
                Boolean isActive,
                int optionsCount,
                List<OptionResponse> options,
                Boolean isReferenced,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {

        public record OptionResponse(
                        Long id,
                        String textEn,
                        String textAr,
                        String textNl,
                        String textFr,
                        Boolean isCorrect,
                        Integer displayOrder) {
        }
}
