package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.ImportHistory;
import com.readyroad.readyroadbackend.domain.repository.ImportHistoryRepository;
import com.readyroad.readyroadbackend.dto.ImportReport;
import com.readyroad.readyroadbackend.service.DataImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/admin/import")
@RequiredArgsConstructor
@Tag(name = "Data Import", description = "Upload-based data import with preview and execute")
public class DataImportController {

    private static final Set<String> ALLOWED_TYPES = Set.of("signs", "lessons", "categories", "quiz_questions");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    private final DataImportService dataImportService;
    private final ImportHistoryRepository importHistoryRepository;

    // ── Preview (dry run) ────────────────────────────────────────

    @PostMapping("/{type}/preview")
    @Operation(summary = "Preview import (dry run) — no DB changes")
    public ResponseEntity<?> preview(
            @PathVariable String type,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        ResponseEntity<?> validation = validateRequest(type, file);
        if (validation != null)
            return validation;

        byte[] content;
        try {
            content = file.getBytes();
        } catch (Exception e) {
            return badRequest("Cannot read uploaded file");
        }

        ImportReport report = dispatch(type, content, true);

        saveHistory(principal, type, file.getOriginalFilename(), content, report);
        return ResponseEntity.ok(report);
    }

    // ── Execute (real import) ────────────────────────────────────

    @PostMapping("/{type}/execute")
    @Operation(summary = "Execute import — applies changes to the database")
    public ResponseEntity<?> execute(
            @PathVariable String type,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        ResponseEntity<?> validation = validateRequest(type, file);
        if (validation != null)
            return validation;

        byte[] content;
        try {
            content = file.getBytes();
        } catch (Exception e) {
            return badRequest("Cannot read uploaded file");
        }

        ImportReport report = dispatch(type, content, false);

        saveHistory(principal, type, file.getOriginalFilename(), content, report);
        return ResponseEntity.ok(report);
    }

    // ── History ──────────────────────────────────────────────────

    @GetMapping("/history")
    @Operation(summary = "List recent import history")
    public ResponseEntity<List<ImportHistory>> getHistory() {
        return ResponseEntity.ok(importHistoryRepository.findTop20ByOrderByPerformedAtDesc());
    }

    @GetMapping("/history/{id}")
    @Operation(summary = "Get single import history record")
    public ResponseEntity<?> getHistoryDetail(@PathVariable Long id) {
        return importHistoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── Internal helpers ─────────────────────────────────────────

    private ImportReport dispatch(String type, byte[] content, boolean dryRun) {
        return switch (type) {
            case "signs" -> dataImportService.importSignsFromUpload(content, dryRun);
            case "lessons" -> dataImportService.importLessonsFromUpload(content, dryRun);
            case "categories" -> dataImportService.importCategoriesFromUpload(content, dryRun);
            case "quiz_questions" -> dataImportService.importQuizQuestionsFromUpload(content, dryRun);
            default -> new ImportReport(type, dryRun ? "PREVIEW" : "IMPORT", dryRun, 0, 0, 0, 0, List.of(),
                    List.of("Unknown type: " + type));
        };
    }

    private ResponseEntity<?> validateRequest(String type, MultipartFile file) {
        if (!ALLOWED_TYPES.contains(type)) {
            return badRequest("Invalid import type. Allowed: " + ALLOWED_TYPES);
        }
        if (file == null || file.isEmpty()) {
            return badRequest("File is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return badRequest("File exceeds maximum size of 10 MB");
        }
        String originalName = file.getOriginalFilename();
        if (originalName != null && !originalName.toLowerCase().endsWith(".json")) {
            return badRequest("Only JSON files are allowed");
        }
        return null; // valid
    }

    private void saveHistory(Principal principal, String type, String fileName,
            byte[] content, ImportReport report) {
        try {
            ImportHistory h = new ImportHistory();
            h.setPerformedBy(principal != null ? principal.getName() : "unknown");
            h.setPerformedAt(LocalDateTime.now());
            h.setImportType(type);
            h.setFileName(fileName != null ? fileName : "upload.json");
            h.setFileChecksum(dataImportService.checksumOf(content));
            h.setDryRun(report.dryRun());
            h.setCreatedCount(report.created());
            h.setUpdatedCount(report.updated());
            h.setSkippedCount(report.skipped());
            h.setStatus(report.errors().isEmpty() ? "SUCCESS" : "FAILED");
            h.setErrorSummary(report.errors().isEmpty() ? null : String.join("; ", report.errors()));
            h.setWarningSummary(report.warnings().isEmpty() ? null : String.join("; ", report.warnings()));
            importHistoryRepository.save(h);
        } catch (Exception e) {
            log.error("Failed to save import history: {}", e.getMessage());
        }
    }

    private ResponseEntity<Map<String, String>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }
}
