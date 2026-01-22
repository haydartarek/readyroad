package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO لتقديم إجابات Quiz
 * ✅ FIX #10: إضافة attemptId للـ DTO لتحسين API design
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionDTO {
    private Long attemptId; // معرّف المحاولة (للـ Smart Quiz)
    private Map<Long, Long> answers; // questionId -> selectedOptionId
}
