package com.readyroad.readyroadbackend.dto;

import java.util.List;

/**
 * DTO for exam question (without correct answers!)
 */
public record ExamQuestionDTO(
    Long id,
    String questionAr,
    String questionEn,
    String questionNl,
    String questionFr,
    String questionType,
    String difficultyLevel,
    String contentImageUrl,
    List<ExamOptionDTO> options
) {}
