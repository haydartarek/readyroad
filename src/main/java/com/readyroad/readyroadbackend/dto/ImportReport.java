package com.readyroad.readyroadbackend.dto;

import java.util.List;

/**
 * Stable JSON report returned by preview (dry-run) and execute import
 * endpoints.
 * <p>
 * Contract fields per spec:
 * type, mode (PREVIEW|IMPORT), recordsTotal, created, updated, skipped,
 * warnings[], errors[].
 * <p>
 * {@code dryRun} is kept for backward-compatibility with existing consumers.
 */
public record ImportReport(
        String type,
        String mode,
        boolean dryRun,
        int recordsTotal,
        int created,
        int updated,
        int skipped,
        List<String> warnings,
        List<String> errors) {
    /**
     * Builder helper — accumulates counts and messages during import processing.
     */
    public static class Builder {
        private final String type;
        private final boolean dryRun;
        private int created;
        private int updated;
        private int skipped;
        private final java.util.ArrayList<String> warnings = new java.util.ArrayList<>();
        private final java.util.ArrayList<String> errors = new java.util.ArrayList<>();

        public Builder(String type, boolean dryRun) {
            this.type = type;
            this.dryRun = dryRun;
        }

        public Builder incCreated() {
            created++;
            return this;
        }

        public Builder incUpdated() {
            updated++;
            return this;
        }

        public Builder incSkipped() {
            skipped++;
            return this;
        }

        public Builder warn(String msg) {
            warnings.add(msg);
            return this;
        }

        public Builder error(String msg) {
            errors.add(msg);
            return this;
        }

        public ImportReport build() {
            String mode = dryRun ? "PREVIEW" : "IMPORT";
            int recordsTotal = created + updated + skipped;
            return new ImportReport(type, mode, dryRun, recordsTotal,
                    created, updated, skipped,
                    List.copyOf(warnings), List.copyOf(errors));
        }
    }
}
