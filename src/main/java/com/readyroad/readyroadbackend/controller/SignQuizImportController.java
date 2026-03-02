package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.RoadSignDetailDto;
import com.readyroad.readyroadbackend.dto.RoadSignSummaryDto;
import com.readyroad.readyroadbackend.dto.SignImportResultDto;
import com.readyroad.readyroadbackend.domain.entity.SignImportRun;
import com.readyroad.readyroadbackend.service.SignQuizImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Admin REST endpoints for the Sign Quiz System.
 *
 * <ul>
 *   <li>{@code POST  /api/admin/sign-quiz/import}        — run the full importer</li>
 *   <li>{@code GET   /api/admin/sign-quiz/import/last}   — last import run record</li>
 *   <li>{@code GET   /api/admin/sign-quiz/signs}         — list all active signs</li>
 *   <li>{@code GET   /api/admin/sign-quiz/signs/{code}}  — full sign detail with questions</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/sign-quiz")
@RequiredArgsConstructor
@Tag(name = "Sign Quiz Admin", description = "Sign Quiz System — import management and data inspection")
public class SignQuizImportController {

    private final SignQuizImportService importService;

    // ── POST /api/admin/sign-quiz/import ────────────────────────────────────

    /**
     * Triggers the full Sign Quiz import from the {@code signs_import/} directory.
     * Each sign directory is processed independently; errors in one sign do not
     * block the rest. Returns a summary report that is also persisted to the DB.
     */
    @PostMapping("/import")
    @Operation(
        summary     = "Run Sign Quiz import",
        description = "Reads every sign directory under signs_import/, validates JSON files "
                    + "(9-step validation), and upserts road_signs, sign_questions, sign_choices, "
                    + "sign_exams, and sign_exam_questions. Returns the import report."
    )
    public ResponseEntity<SignImportResultDto> runImport(Principal principal) {
        String performer = (principal != null) ? principal.getName() : "ADMIN";
        log.info("Sign Quiz import triggered by [{}]", performer);

        SignImportRun run    = importService.runImport(performer);
        SignImportResultDto result = SignImportResultDto.from(run);

        // Use 200 OK for SUCCESS/PARTIAL, 207 Multi-Status when there are errors but some succeeded,
        // 500 Internal Server Error when everything failed.
        if ("FAILED".equals(result.status())) {
            return ResponseEntity.internalServerError().body(result);
        }
        return ResponseEntity.ok(result);
    }

    // ── GET /api/admin/sign-quiz/import/last ────────────────────────────────

    /**
     * Returns the most recent {@code sign_import_runs} record, or 404 if none exists.
     */
    @GetMapping("/import/last")
    @Operation(
        summary     = "Last import run",
        description = "Fetches the most recent sign_import_runs record from the database."
    )
    public ResponseEntity<SignImportResultDto> getLastImport() {
        return importService.getLastImportRun()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ── GET /api/admin/sign-quiz/signs ──────────────────────────────────────

    /**
     * Returns a lightweight summary list of all active road signs,
     * ordered by sign code ascending.
     */
    @GetMapping("/signs")
    @Operation(
        summary     = "List all active signs",
        description = "Returns RoadSignSummaryDto for every active road sign (id, code, "
                    + "category, imagePath, seriousViolation, multilingual name)."
    )
    public ResponseEntity<List<RoadSignSummaryDto>> listSigns() {
        List<RoadSignSummaryDto> signs = importService.getAllActiveSigns();
        return ResponseEntity.ok(signs);
    }

    // ── GET /api/admin/sign-quiz/signs/{code} ───────────────────────────────

    /**
     * Returns the full detail of a road sign — all multilingual fields plus
     * every linked question with its answer choices.
     *
     * @param code the sign code, e.g. {@code A1}, {@code B19}, {@code C3}
     */
    @GetMapping("/signs/{code}")
    @Operation(
        summary     = "Get sign detail",
        description = "Returns the complete RoadSignDetailDto for the given sign code, "
                    + "including all questions and their answer choices."
    )
    public ResponseEntity<RoadSignDetailDto> getSign(
            @Parameter(description = "Sign code, e.g. A1, B19, C3", example = "A1")
            @PathVariable String code) {

        return importService.getSignDetailByCode(code.toUpperCase())
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Sign not found: {}", code);
                    return ResponseEntity.notFound().build();
                });
    }

    // ── GET /api/admin/sign-quiz/stats ──────────────────────────────────────

    /**
     * Quick statistics — total signs, total questions (derived from the sign list).
     * Useful for dashboard overview without a dedicated analytics table.
     */
    @GetMapping("/stats")
    @Operation(
        summary     = "Sign Quiz statistics",
        description = "Returns a simple count of imported active signs."
    )
    public ResponseEntity<Map<String, Object>> stats() {
        List<RoadSignSummaryDto> signs = importService.getAllActiveSigns();
        Map<String, Object> body = Map.of(
                "totalSigns",       signs.size(),
                "byCategory",       buildCategoryBreakdown(signs)
        );
        return ResponseEntity.ok(body);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private Map<String, Long> buildCategoryBreakdown(List<RoadSignSummaryDto> signs) {
        java.util.LinkedHashMap<String, Long> map = new java.util.LinkedHashMap<>();
        signs.stream()
             .collect(java.util.stream.Collectors.groupingBy(
                     s -> s.category().name(),
                     java.util.stream.Collectors.counting()))
             .entrySet()
             .stream()
             .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
             .forEach(e -> map.put(e.getKey(), e.getValue()));
        return map;
    }
}
