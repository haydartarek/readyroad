package com.readyroad.readyroadbackend.dto.response;

import java.util.List;

/**
 * Result of a lesson import operation (preview or execute).
 */
public record LessonImportResult(
        boolean dryRun,
        int created,
        int updated,
        int skipped,
        int totalInFile,
        List<String> errors,
        List<LessonImportItem> items) {

    public record LessonImportItem(
            String lessonCode,
            String titleEn,
            String action, // "CREATED" | "UPDATED" | "SKIPPED" | "ERROR"
            String detail) {
    }
}
