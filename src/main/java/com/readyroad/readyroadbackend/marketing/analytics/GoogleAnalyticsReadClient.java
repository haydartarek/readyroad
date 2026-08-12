package com.readyroad.readyroadbackend.marketing.analytics;

import java.time.LocalDate;

public interface GoogleAnalyticsReadClient {
    AnalyticsModels.Ga4Data fetch(LocalDate startDate, LocalDate endDate);
}
