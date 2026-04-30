package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.dto.response.LessonImportResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Keeps the lessons database aligned with the single bundled canonical JSON file.
 */
@Service
@ConditionalOnProperty(name = "app.lessons.sync-on-startup", havingValue = "true", matchIfMissing = true)
public class LessonCanonicalSyncService {

    private static final Logger log = LoggerFactory.getLogger(LessonCanonicalSyncService.class);

    private final LessonImportService lessonImportService;

    public LessonCanonicalSyncService(LessonImportService lessonImportService) {
        this.lessonImportService = lessonImportService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncLessonsFromCanonicalJson() {
        try {
            LessonImportResult result = lessonImportService.importFromClasspath();
            if (result.errors() == null || result.errors().isEmpty()) {
                log.info("Canonical lessons sync completed: {} created, {} updated, {} skipped, {} total",
                        result.created(), result.updated(), result.skipped(), result.totalInFile());
            } else {
                log.warn("Canonical lessons sync finished with validation errors: {}", result.errors());
            }
        } catch (Exception ex) {
            log.error("Canonical lessons sync failed", ex);
        }
    }
}
