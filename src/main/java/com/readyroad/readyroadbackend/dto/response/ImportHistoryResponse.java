package com.readyroad.readyroadbackend.dto.response;

import java.time.LocalDateTime;

public record ImportHistoryResponse(
        Long id,
        String performedBy,
        LocalDateTime performedAt,
        String importType,
        String fileName,
        Boolean dryRun,
        Integer createdCount,
        Integer updatedCount,
        Integer skippedCount,
        String status,
        String errorSummary,
        String warningSummary) {
}