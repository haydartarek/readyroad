package com.readyroad.readyroadbackend.marketing.analytics;

import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "readyroad.marketing.enabled", havingValue = "true")
@RequiredArgsConstructor
public class AnalyticsInitialSyncBootstrap implements ApplicationRunner {

    private static final ZoneId OPERATIONS_ZONE = ZoneId.of("Europe/Brussels");

    private final GoogleServiceAccountCredentials credentials;
    private final AnalyticsStore store;
    private final AnalyticsAdminService adminService;

    @Override
    public void run(ApplicationArguments args) {
        if (credentials.isConfigured() && store.latestSearchConsoleDate() == null) {
            adminService.requestSync(
                    "analytics-initial-backfill-" + LocalDate.now(OPERATIONS_ZONE),
                    "ANALYTICS_BOOTSTRAP");
        }
    }
}
