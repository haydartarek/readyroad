package com.readyroad.readyroadbackend.marketing.analytics;

import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsReportService {

    private final AnalyticsStore store;

    public void weekly(Long taskId, LocalDate today) {
        LocalDate end = today.minusDays(1);
        LocalDate start = end.minusDays(6);
        LocalDate previousEnd = start.minusDays(1);
        LocalDate previousStart = previousEnd.minusDays(6);
        store.saveReport(
                "WEEKLY_REPORT", start, end, previousStart, previousEnd,
                store.reportMetrics(start, end), store.reportMetrics(previousStart, previousEnd), taskId);
    }

    public void monthly(Long taskId, LocalDate today) {
        YearMonth current = YearMonth.from(today);
        YearMonth completed = current.minusMonths(1);
        YearMonth previous = completed.minusMonths(1);
        store.saveReport(
                "MONTHLY_REPORT", completed.atDay(1), completed.atEndOfMonth(),
                previous.atDay(1), previous.atEndOfMonth(),
                store.reportMetrics(completed.atDay(1), completed.atEndOfMonth()),
                store.reportMetrics(previous.atDay(1), previous.atEndOfMonth()), taskId);
    }
}
