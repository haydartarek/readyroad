package com.readyroad.readyroadbackend.marketing.analytics;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.audit.ExecutionLogService;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.editorial.EditorialPriorityTaskService;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnalyticsSyncServiceTest {

    private final GoogleAnalyticsReadClient ga4 = mock(GoogleAnalyticsReadClient.class);
    private final SearchConsoleReadClient search = mock(SearchConsoleReadClient.class);
    private final AnalyticsStore store = mock(AnalyticsStore.class);
    private final AnalyticsSettingsService settings = mock(AnalyticsSettingsService.class);
    private final AnalyticsScheduleActivator schedules = mock(AnalyticsScheduleActivator.class);
    private final OrganicOpportunityService opportunities = mock(OrganicOpportunityService.class);
    private final EditorialPriorityTaskService editorialPriorities = mock(EditorialPriorityTaskService.class);
    private final ExecutionLogService logs = mock(ExecutionLogService.class);
    private final MarketingAuditService audit = mock(MarketingAuditService.class);
    private final MarketingProperties properties = new MarketingProperties();
    private final AnalyticsSyncService service = new AnalyticsSyncService(
            ga4, search, store, settings, schedules, opportunities, editorialPriorities,
            logs, audit, properties);

    @BeforeEach
    void setUp() {
        when(settings.current()).thenReturn(AnalyticsSettings.defaults());
        when(audit.detail(any(), any())).thenReturn(new ObjectMapper().createObjectNode());
        when(store.latestSearchConsoleDate()).thenReturn(LocalDate.now().minusDays(1));
    }

    @Test
    void oneFailedGoogleSourceProducesAPartialSyncAndKeepsHealthyData() {
        when(ga4.fetch(any(), any())).thenThrow(
                new MarketingTaskExecutionException("HTTP_503", "GA4 unavailable"));
        when(search.fetch(any(), any())).thenReturn(new AnalyticsModels.SearchConsoleData(
                List.of(), List.of(), List.of(), Map.of()));

        service.synchronize(42L, true);

        verify(store).saveReadyRoad(any(), any(), eq(42L), eq(List.of("GA4:HTTP_503")));
        verify(store).saveSearchConsole(any(), any(), eq(42L), eq(List.of("GA4:HTTP_503")));
        verify(editorialPriorities).enqueueAfterAnalytics(eq(42L), any());
        verify(schedules).activateAfterSuccessfulSync(any());
    }

    @Test
    void bothGoogleSourcesFailWithRetryableSourceFailure() {
        when(ga4.fetch(any(), any())).thenThrow(
                new MarketingTaskExecutionException("HTTP_503", "GA4 unavailable"));
        when(search.fetch(any(), any())).thenThrow(
                new MarketingTaskExecutionException("HTTP_503", "Search unavailable"));

        assertThatThrownBy(() -> service.synchronize(43L, false))
                .isInstanceOf(MarketingTaskExecutionException.class)
                .extracting(error -> ((MarketingTaskExecutionException) error).errorCode())
                .isEqualTo("HTTP_503");

        verify(store).saveReadyRoad(any(), any(), eq(43L), anyList());
        verifyNoInteractions(editorialPriorities);
    }
}
