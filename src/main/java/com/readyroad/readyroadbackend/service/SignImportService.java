package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.dto.SignImportEntry;
import com.readyroad.readyroadbackend.dto.SignImportEntry.ImportItemResult;
import com.readyroad.readyroadbackend.dto.SignImportEntry.ImportResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Admin-only service for importing long descriptions from the canonical
 * signs.json file into the database. Matches signs by code and updates
 * only long_description_* fields. Never modifies names or images.
 */
@Service
@Slf4j
public class SignImportService {

    private final RoadSignRepository roadSignRepository;

    public SignImportService(RoadSignRepository roadSignRepository) {
        this.roadSignRepository = roadSignRepository;
    }

    /**
     * Import long descriptions from a list of sign entries.
     *
     * @param entries the sign entries with long descriptions
     * @param dryRun  if true, validate and preview only — no DB writes
     * @return import result with per-sign details
     */
    @Transactional
    public ImportResult importLongDescriptions(List<SignImportEntry> entries, boolean dryRun) {
        List<ImportItemResult> details = new ArrayList<>();
        int updated = 0;
        int skipped = 0;
        int errors = 0;

        for (SignImportEntry entry : entries) {
            // Validate entry
            if (entry.code() == null || entry.code().isBlank()) {
                details.add(new ImportItemResult(
                        entry.code(), "error", "Missing sign code"));
                errors++;
                continue;
            }

            boolean hasAnyDescription = isNotBlank(entry.longDescriptionEn()) ||
                    isNotBlank(entry.longDescriptionNl()) ||
                    isNotBlank(entry.longDescriptionFr()) ||
                    isNotBlank(entry.longDescriptionAr());

            if (!hasAnyDescription) {
                details.add(new ImportItemResult(
                        entry.code(), "skipped", "No long descriptions provided"));
                skipped++;
                continue;
            }

            // Find sign by code
            Optional<RoadSign> signOpt = roadSignRepository.findFirstBySignCodeOrderByIdAsc(entry.code());
            if (signOpt.isEmpty()) {
                details.add(new ImportItemResult(
                        entry.code(), "error", "Sign not found in database"));
                errors++;
                continue;
            }

            RoadSign sign = signOpt.get();

            // Check if anything actually changed (map longDescription → description)
            boolean changed = false;
            if (isDifferent(sign.getDescriptionEn(), entry.longDescriptionEn()))
                changed = true;
            if (isDifferent(sign.getDescriptionNl(), entry.longDescriptionNl()))
                changed = true;
            if (isDifferent(sign.getDescriptionFr(), entry.longDescriptionFr()))
                changed = true;
            if (isDifferent(sign.getDescriptionAr(), entry.longDescriptionAr()))
                changed = true;

            if (!changed) {
                details.add(new ImportItemResult(
                        entry.code(), "skipped", "No changes detected"));
                skipped++;
                continue;
            }

            if (!dryRun) {
                if (entry.longDescriptionEn() != null)
                    sign.setDescriptionEn(entry.longDescriptionEn());
                if (entry.longDescriptionNl() != null)
                    sign.setDescriptionNl(entry.longDescriptionNl());
                if (entry.longDescriptionFr() != null)
                    sign.setDescriptionFr(entry.longDescriptionFr());
                if (entry.longDescriptionAr() != null)
                    sign.setDescriptionAr(entry.longDescriptionAr());
                roadSignRepository.save(sign);
            }

            details.add(new ImportItemResult(
                    entry.code(), "updated",
                    dryRun ? "Would update long descriptions" : "Long descriptions updated"));
            updated++;
        }

        log.info("Sign import {} complete: total={}, updated={}, skipped={}, errors={}",
                dryRun ? "(dry-run)" : "", entries.size(), updated, skipped, errors);

        return new ImportResult(entries.size(), updated, skipped, errors, dryRun, details);
    }

    /**
     * Validate that entries have correct structure and matching signs exist.
     */
    public ImportResult validateEntries(List<SignImportEntry> entries) {
        return importLongDescriptions(entries, true);
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }

    private static boolean isDifferent(String current, String incoming) {
        if (incoming == null)
            return false; // null incoming = don't touch
        if (current == null)
            return true; // null current + non-null incoming = changed
        return !current.equals(incoming);
    }
}
