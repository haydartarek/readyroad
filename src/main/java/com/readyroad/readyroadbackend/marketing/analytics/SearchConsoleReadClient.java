package com.readyroad.readyroadbackend.marketing.analytics;

import java.time.LocalDate;

public interface SearchConsoleReadClient {
    AnalyticsModels.SearchConsoleData fetch(LocalDate startDate, LocalDate endDate);
}
