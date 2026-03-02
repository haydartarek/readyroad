package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.SignImportRun;

import java.time.LocalDateTime;

/**
 * DTO returned by the Sign Quiz Import endpoint.
 */
public record SignImportResultDto(
        Long   id,
        String performedBy,
        String status,
        int    signsProcessed,
        int    signsCreated,
        int    signsUpdated,
        int    signsSkipped,
        int    questionsCreated,
        int    questionsUpdated,
        int    examsCreated,
        int    errorsCount,
        String errorSummary,
        Long   durationMs,
        LocalDateTime createdAt
) {
    public static SignImportResultDto from(SignImportRun run) {
        return new SignImportResultDto(
                run.getId(),
                run.getPerformedBy(),
                run.getStatus(),
                run.getSignsProcessed(),
                run.getSignsCreated(),
                run.getSignsUpdated(),
                run.getSignsSkipped(),
                run.getQuestionsCreated(),
                run.getQuestionsUpdated(),
                run.getExamsCreated(),
                run.getErrorsCount(),
                run.getErrorSummary(),
                run.getDurationMs(),
                run.getCreatedAt()
        );
    }
}
