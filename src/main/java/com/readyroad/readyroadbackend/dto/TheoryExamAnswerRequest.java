package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Client payload for a single answer in the Belgian theory exam (practice mode).
 * selectedOptionId == null means the question timed out (no answer given).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TheoryExamAnswerRequest {
    private Long questionId;
    private Long selectedOptionId; // null = timed out / no answer
}
