package com.readyroad.readyroadbackend.dto;

/**
 * DTO for exam option (without isCorrect field for security!)
 */
public record ExamOptionDTO(
    Long id,
    String optionTextAr,
    String optionTextEn,
    String optionTextNl,
    String optionTextFr,
    int displayOrder
) {}
