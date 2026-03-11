package com.readyroad.readyroadbackend.dto.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for starting exam simulation - Story A1
 * Uses Instant for UTC-aware timestamps
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamStartResponse {

    private Long examId;

    private Integer totalQuestions;

    private Integer timeLimitMinutes;

    private String status;

    /**
     * Exam start time in UTC (ISO-8601 format)
     * Example: "2026-02-05T19:30:00Z"
     */
    private Instant startedAt;

    /**
     * Exam expiration time in UTC (ISO-8601 format)
     * Example: "2026-02-05T20:00:00Z"
     */
    private Instant expiresAt;

    private List<ExamQuestionDTO> questions;
}
