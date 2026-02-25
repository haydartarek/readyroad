package com.readyroad.readyroadbackend.dto;

import java.util.List;

/**
 * Represents a single sign entry from the canonical signs.json file.
 * Used only for the admin import pipeline.
 */
public record SignImportEntry(
        String code,
        String longDescriptionEn,
        String longDescriptionNl,
        String longDescriptionFr,
        String longDescriptionAr) {

    /**
     * Wrapper for the full import request payload.
     */
    public record ImportRequest(
            List<SignImportEntry> signs,
            boolean dryRun) {
    }

    /**
     * Result for a single sign in the import.
     */
    public record ImportItemResult(
            String signCode,
            String status, // "updated", "created", "skipped", "error"
            String message) {
    }

    /**
     * Summary result of the entire import operation.
     */
    public record ImportResult(
            int total,
            int updated,
            int skipped,
            int errors,
            boolean dryRun,
            List<ImportItemResult> details) {
    }
}
