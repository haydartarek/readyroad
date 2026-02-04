package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.service.DataImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/import")
@Tag(name = "Data Import", description = "Import data from JSON files into the database")
public class DataImportController {

    private static final Logger log = LoggerFactory.getLogger(DataImportController.class);

    private final DataImportService dataImportService;

    public DataImportController(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    @PostMapping
    @Operation(summary = "Import all data from JSON files",
               description = "Imports categories, signs, and lessons from JSON files in the data directory")
    public ResponseEntity<Map<String, String>> importData(
            @RequestParam(defaultValue = "data") String dataDir) {

        File dir = new File(dataDir);
        if (!dir.exists() || !dir.isDirectory()) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", "Data directory not found: " + dir.getAbsolutePath()));
        }

        try {
            log.info("Manual data import triggered for directory: {}", dir.getAbsolutePath());
            dataImportService.importAllData(dir.getAbsolutePath());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Data import completed successfully from: " + dir.getAbsolutePath()));
        } catch (Exception e) {
            log.error("Manual data import failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    Map.of("status", "error", "message", "Import failed: " + e.getMessage()));
        }
    }
}
