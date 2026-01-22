package com.readyroad.readyroadbackend.dto.exam;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for starting exam simulation - Story A1
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime startedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime expiresAt;

    private List<ExamQuestionDTO> questions;
}
