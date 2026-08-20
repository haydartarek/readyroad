package com.readyroad.readyroadbackend.marketing.analytics;

import com.readyroad.readyroadbackend.marketing.audit.ExecutionLogService;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.domain.ExecutionLogLevel;
import com.readyroad.readyroadbackend.marketing.editorial.EditorialPriorityTaskService;
import com.readyroad.readyroadbackend.marketing.editorial.EditorialOpportunityDiscoveryService;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsSyncService {

    private static final ZoneId OPERATIONS_ZONE = ZoneId.of("Europe/Brussels");

    private final GoogleAnalyticsReadClient ga4Client;
    private final SearchConsoleReadClient searchConsoleClient;
    private final AnalyticsStore store;
    private final AnalyticsSettingsService settingsService;
    private final AnalyticsScheduleActivator scheduleActivator;
    private final OrganicOpportunityService opportunityService;
    private final EditorialPriorityTaskService editorialPriorityTaskService;
    private final EditorialOpportunityDiscoveryService editorialOpportunityDiscoveryService;
    private final ExecutionLogService logService;
    private final MarketingAuditService auditService;
    private final MarketingProperties properties;

    public void synchronize(Long taskId, boolean initial) {
        AnalyticsSettings settings = settingsService.current();
        LocalDate end = LocalDate.now(OPERATIONS_ZONE).minusDays(1);
        LocalDate start = initial
                ? end.minusDays(settings.initialBackfillDays() - 1L)
                : end.minusDays(Math.max(settings.intervalDays() - 1L, 0));
        AnalyticsModels.Ga4Data ga4 = null;
        AnalyticsModels.SearchConsoleData searchConsole = null;
        MarketingTaskExecutionException ga4Failure = null;
        MarketingTaskExecutionException searchFailure = null;
        try {
            ga4 = ga4Client.fetch(start, end);
        } catch (MarketingTaskExecutionException error) {
            ga4Failure = error;
        }
        try {
            searchConsole = searchConsoleClient.fetch(start, end);
        } catch (MarketingTaskExecutionException error) {
            searchFailure = error;
        }

        List<String> partialFailures = new ArrayList<>();
        if (ga4Failure != null) {
            partialFailures.add("GA4:" + ga4Failure.errorCode());
        }
        if (searchFailure != null) {
            partialFailures.add("SEARCH_CONSOLE:" + searchFailure.errorCode());
        }
        store.saveRijVia(start, end, taskId, partialFailures);
        if (ga4 != null) {
            store.saveGa4(ga4, start, end, taskId, partialFailures);
        }
        if (searchConsole != null) {
            store.saveSearchConsole(
                    searchConsole, properties.getAnalytics().getSearchConsoleSiteUrl(), taskId, partialFailures);
            opportunityService.analyze(end);
            warnIfDataIsLate(taskId, end, settings);
            editorialPriorityTaskService.enqueueAfterAnalytics(taskId, end);
            editorialOpportunityDiscoveryService.enqueueCandidates(taskId);
        }
        if (!partialFailures.isEmpty()) {
            logService.record(
                    taskId, null, ExecutionLogLevel.WARN, "ANALYTICS_PARTIAL_SYNC",
                    "Analytics sync completed with one or more unavailable sources",
                    auditService.detail("failedSources", partialFailures.size()));
        }
        if (ga4 == null && searchConsole == null) {
            throw ga4Failure != null ? ga4Failure : searchFailure;
        }

        store.deleteRawBefore(end.minusMonths(24));
        scheduleActivator.activateAfterSuccessfulSync(Instant.now());
        logService.record(
                taskId, null, ExecutionLogLevel.INFO, "ANALYTICS_SYNC_COMPLETED",
                "Analytics sources synchronized",
                auditService.detail("periodStart", start.toString()));
    }

    private void warnIfDataIsLate(Long taskId, LocalDate expectedThrough, AnalyticsSettings settings) {
        LocalDate latest = store.latestSearchConsoleDate();
        if (latest != null && latest.isBefore(expectedThrough.minusDays(settings.noDataDays()))) {
            logService.record(
                    taskId, null, ExecutionLogLevel.WARN, "ANALYTICS_NO_DATA",
                    "Search Console data is older than the configured no-data threshold",
                    auditService.detail("latestDataDate", latest.toString()));
        }
    }
}
