package com.readyroad.readyroadbackend.config;

import com.readyroad.readyroadbackend.service.DataImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Runs data import from JSON files at application startup.
 * Only imports if the data directory exists and contains the expected files.
 * Set readyroad.data-import.enabled=true in application.yml to enable
 * auto-import.
 */
@Component
public class DataImportRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataImportRunner.class);

    private final DataImportService dataImportService;

    @Value("${readyroad.data-import.enabled:false}")
    private boolean importEnabled;

    @Value("${readyroad.data-import.path:data}")
    private String dataPath;

    private static final String[] REQUIRED_FILES = {
            "signs.json",
            "category_descriptions.json"
    };

    public DataImportRunner(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!importEnabled) {
            log.info("═══════════════════════════════════════════════════");
            log.info("ℹ️  Data import is DISABLED");
            log.info("Set readyroad.data-import.enabled=true to enable.");
            log.info("═══════════════════════════════════════════════════");
            return;
        }

        File dataDir = new File(dataPath);
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            log.warn("⚠️  Data directory not found: {}", dataDir.getAbsolutePath());
            return;
        }

        log.info("═══════════════════════════════════════════════════");
        log.info("🔍 VERIFYING DATA IMPORT SOURCES");
        log.info("═══════════════════════════════════════════════════");

        // Check that required files exist
        boolean allFilesExist = true;
        for (String fileName : REQUIRED_FILES) {
            File f = new File(dataDir, fileName);
            if (!f.exists()) {
                log.warn("❌ Required file not found: {}", f.getAbsolutePath());
                allFilesExist = false;
            } else {
                log.info("✅ Found: {}", fileName);
            }
        }

        if (!allFilesExist) {
            log.warn("⚠️  Skipping import due to missing required files.");
            return;
        }

        log.info("═══════════════════════════════════════════════════");
        log.info("🚀 Starting automatic data import");
        log.info("📁 Source: {}", dataDir.getAbsolutePath());
        log.info("═══════════════════════════════════════════════════");

        try {
            dataImportService.importAllData(dataDir.getAbsolutePath());
            log.info("═══════════════════════════════════════════════════");
            log.info("✅ Automatic data import completed successfully!");
            log.info("═══════════════════════════════════════════════════");
        } catch (Exception e) {
            log.error("═══════════════════════════════════════════════════");
            log.error("❌ Automatic data import failed: {}", e.getMessage(), e);
            log.error("═══════════════════════════════════════════════════");
        }
    }
}
