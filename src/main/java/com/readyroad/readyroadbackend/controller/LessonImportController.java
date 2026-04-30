package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.LessonImportResult;
import com.readyroad.readyroadbackend.service.LessonImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Admin-only endpoints for importing lessons from lessons_content.json into the
 * database.
 * <p>
 * Supports:
 * <ul>
 * <li><b>Preview / dry-run</b> – validates JSON and shows what would
 * change</li>
 * <li><b>Execute</b> – performs idempotent upsert into DB</li>
 * <li><b>Import from classpath</b> – uses the bundled lessons_content.json</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/admin/lessons-import")
@Tag(name = "Admin – Lesson Import", description = "Import lessons from JSON into database")
@PreAuthorize("hasRole('ADMIN')")
public class LessonImportController {

    private final LessonImportService importService;

    public LessonImportController(LessonImportService importService) {
        this.importService = importService;
    }

    /**
     * Preview what an import would do without touching the database.
     */
    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Preview lesson import (dry-run, no DB writes)")
    public ResponseEntity<LessonImportResult> preview(
            @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(importService.preview(file.getInputStream()));
    }

    /**
     * Execute the import: upsert all lessons into the DB.
     */
    @PostMapping(value = "/execute", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Execute lesson import (upsert into DB)")
    public ResponseEntity<LessonImportResult> execute(
            @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(importService.execute(file.getInputStream()));
    }

    /**
     * Import from the bundled classpath resource (data/lessons_content.json).
     * Useful for initial seeding or CI pipelines.
     */
    @PostMapping("/execute-bundled")
    @Operation(summary = "Import lessons from bundled classpath JSON")
    public ResponseEntity<LessonImportResult> executeBundled() {
        return ResponseEntity.ok(importService.importFromClasspath());
    }
}
