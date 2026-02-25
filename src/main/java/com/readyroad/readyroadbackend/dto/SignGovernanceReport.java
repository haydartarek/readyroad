package com.readyroad.readyroadbackend.dto;

import java.util.List;

/**
 * DTOs for the canonical source governance audit (signs.json ↔ DB consistency).
 */
public final class SignGovernanceReport {

    private SignGovernanceReport() {
    }

    /**
     * Per-sign audit result.
     */
    public record SignAuditItem(
            String signCode,
            String status,
            List<String> issues) {
    }

    /**
     * Full governance audit result.
     */
    public record AuditResult(
            int totalDbSigns,
            int totalJsonSigns,
            int fullyConsistent,
            int withIssues,
            int orphanInDb,
            int orphanInJson,
            boolean passed,
            List<SignAuditItem> details) {
    }
}
