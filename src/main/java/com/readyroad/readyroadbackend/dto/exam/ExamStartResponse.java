package com.readyroad.readyroadbackend.dto.exam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for starting/resuming an exam simulation.
 * Uses Instant for UTC-aware timestamps.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamStartResponse {

    private Long examId;

    /**
     * Total persisted questions in the complete Belgian theory exam.
     * Remains 50 even when a free learner can currently see only 10.
     */
    private Integer totalQuestions;

    private Double timeLimitMinutes;

    private Integer timeLimitSeconds;

    private String status;

    private Instant startedAt;

    private Instant expiresAt;

    /**
     * Questions currently allowed to be exposed to this learner.
     *
     * PREVIEW:
     * only orders 1..10.
     *
     * FULL:
     * all 50 questions.
     */
    private List<ExamQuestionDTO> questions;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExamAccessMode accessMode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExamAccessState accessState;

    /**
     * Current free preview limit.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer freeQuestionLimit;

    /**
     * First question order the frontend should continue with.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer resumeQuestionOrder;

    /**
     * Questions already finalized by an answer or timeout.
     * Used to safely hydrate frontend state after refresh/payment.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Long> finalizedQuestionIds;
}