package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for submitting Quiz answers
 * FIX #10: Adding attemptId to the DTO to improve API design
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionDTO {
    private Long attemptId; // Attempt identifier (for Smart Quiz)
    private Map<Long, Long> answers; // questionId -> selectedOptionId
}
