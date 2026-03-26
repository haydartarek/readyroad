package com.readyroad.readyroadbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.readyroad.readyroadbackend.service.CanonicalRoadSignSyncService;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final CanonicalRoadSignSyncService canonicalRoadSignSyncService;

    public DataInitializer(CanonicalRoadSignSyncService canonicalRoadSignSyncService) {
        this.canonicalRoadSignSyncService = canonicalRoadSignSyncService;
    }

    @Override
    public void run(String... args) {
        int updated = canonicalRoadSignSyncService.syncCanonicalFields();
        log.info("✅ Canonical road-sign sync finished at startup — {} rows updated.", updated);
    }
}
