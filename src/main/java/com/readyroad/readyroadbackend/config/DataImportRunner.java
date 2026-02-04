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
 * Set readyroad.data-import.enabled=true in application.yml to enable auto-import.
 */
@Component
public class DataImportRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataImportRunner.class);

    private final DataImportService dataImportService;

    @Value("${readyroad.data-import.enabled:false}")
    private boolean importEnabled;

    @Value("${readyroad.data-import.path:data}")
    private String dataPath;

    public DataImportRunner(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!importEnabled) {
            log.info("Data import is disabled. Set readyroad.data-import.enabled=true to enable.");
            return;
        }

        File dataDir = new File(dataPath);
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            log.warn("Data directory not found: {}", dataDir.getAbsolutePath());
            return;
        }

        // Check that required files exist
        String[] requiredFiles = {"signs.json", "lessons_content.json", "category_descriptions.json"};
        for (String fileName : requiredFiles) {
            File f = new File(dataDir, fileName);
            if (!f.exists()) {
                log.warn("Required file not found: {}. Skipping import.", f.getAbsolutePath());
                return;
            }
        }

        log.info("Starting automatic data import from: {}", dataDir.getAbsolutePath());
        try {
            dataImportService.importAllData(dataDir.getAbsolutePath());
            log.info("Automatic data import completed successfully!");
        } catch (Exception e) {
            log.error("Automatic data import failed: {}", e.getMessage(), e);
        }
    }
}
