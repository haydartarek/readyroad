package com.readyroad.readyroadbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for exam start response
 * Prevents circular references and lazy loading issues
 */
public record ExamStartResponseDTO(
    Long examId,
    int totalQuestions,
    int timeLimitMinutes,
    String status,
    LocalDateTime startedAt,
    LocalDateTime expiresAt,
    List<ExamQuestionDTO> questions
) {}
