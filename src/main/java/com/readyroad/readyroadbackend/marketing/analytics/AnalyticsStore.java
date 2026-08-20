package com.readyroad.readyroadbackend.marketing.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class AnalyticsStore {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;
    private final OrganicDiscoveryClassifier classifier;

    @Transactional
    public void saveGa4(
            AnalyticsModels.Ga4Data data,
            LocalDate start,
            LocalDate end,
            Long taskId,
            List<String> partialFailures) {
        saveMetricRows("DAILY", data.totals(), taskId, partialFailures, data.quotaState());
        saveMetricRows("LANGUAGE", data.languages(), taskId, partialFailures, data.quotaState());
        saveMetricRows("DEVICE", data.devices(), taskId, partialFailures, data.quotaState());
        if (data.totals().isEmpty()) {
            saveAnalyticsSnapshot(
                    "GA4", "DAILY", start, end, Map.of(), Map.of(), 0,
                    partialFailures.isEmpty() ? "COMPLETE" : "PARTIAL", partialFailures,
                    data.quotaState(), "GA4:DAILY:" + start + ":" + end, taskId);
        }
    }

    @Transactional
    public void saveSearchConsole(
            AnalyticsModels.SearchConsoleData data,
            String siteUrl,
            Long taskId,
            List<String> partialFailures) {
        Map<LocalDate, Long> snapshotIds = new LinkedHashMap<>();
        for (AnalyticsModels.SearchRow total : data.propertyTotals()) {
            Long id = jdbc.queryForObject("""
                    INSERT INTO seo_snapshots (
                        site_url, period_start, period_end, clicks, impressions, ctr,
                        average_position, source_record_count, task_id, source_kind
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, 1, ?, 'LIVE_API')
                    ON CONFLICT (site_url, period_start, period_end, source_kind) DO UPDATE SET
                        clicks = EXCLUDED.clicks,
                        impressions = EXCLUDED.impressions,
                        ctr = EXCLUDED.ctr,
                        average_position = EXCLUDED.average_position,
                        source_record_count = EXCLUDED.source_record_count,
                        task_id = EXCLUDED.task_id,
                        created_at = CURRENT_TIMESTAMP
                    RETURNING id
                    """, Long.class,
                    siteUrl, total.date(), total.date(), total.clicks(), total.impressions(),
                    total.ctr(), total.position(), taskId);
            snapshotIds.put(total.date(), id);
            saveAnalyticsSnapshot(
                    "SEARCH_CONSOLE", "PROPERTY_TOTAL", total.date(), total.date(),
                    Map.of("siteUrl", siteUrl), searchMetrics(total), 1,
                    partialFailures.isEmpty() ? "COMPLETE" : "PARTIAL", partialFailures,
                    data.quotaState(), "GSC:PROPERTY:" + siteUrl + ":" + total.date(), taskId);
        }
        snapshotIds.values().forEach(id -> {
            jdbc.update("DELETE FROM seo_query_snapshots WHERE seo_snapshot_id = ?", id);
            jdbc.update("DELETE FROM seo_page_snapshots WHERE seo_snapshot_id = ?", id);
        });
        for (AnalyticsModels.SearchRow row : data.queries()) {
            Long snapshotId = snapshotIds.get(row.date());
            if (snapshotId != null) {
                saveQuery(snapshotId, row);
            }
        }
        for (AnalyticsModels.SearchRow row : data.pages()) {
            Long snapshotId = snapshotIds.get(row.date());
            if (snapshotId != null) {
                savePage(snapshotId, row);
            }
        }
    }

    @Transactional
    public void saveRijVia(LocalDate start, LocalDate end, Long taskId, List<String> partialFailures) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT day::date AS snapshot_date,
                       (SELECT COUNT(*) FROM users WHERE created_at::date = day::date) AS registrations,
                       (SELECT COUNT(*) FROM exam_simulations
                           WHERE completed_at IS NOT NULL AND completed_at::date = day::date) AS exams,
                       (SELECT COUNT(*) FROM quiz_attempts WHERE created_at::date = day::date) AS quiz_attempts,
                       (SELECT COUNT(*) FROM sign_practice_sessions WHERE started_at::date = day::date) AS sign_practice
                FROM generate_series(?::date, ?::date, interval '1 day') day
                ORDER BY day
                """, start, end);
        for (Map<String, Object> row : rows) {
            LocalDate date = ((java.sql.Date) row.get("snapshot_date")).toLocalDate();
            Map<String, Object> metrics = new LinkedHashMap<>();
            metrics.put("registrations", number(row.get("registrations")));
            metrics.put("exams", number(row.get("exams")));
            metrics.put("quizAttempts", number(row.get("quiz_attempts")));
            metrics.put("signPracticeSessions", number(row.get("sign_practice")));
            saveAnalyticsSnapshot(
                    "RIJVIA", "DAILY", date, date, Map.of(), metrics, 1,
                    partialFailures.isEmpty() ? "COMPLETE" : "PARTIAL", partialFailures,
                    Map.of(), "RIJVIA:DAILY:" + date, taskId);
        }
    }

    @Transactional(readOnly = true)
    public List<AnalyticsModels.QueryAggregate> aggregateQueries(LocalDate start, LocalDate end) {
        String sql = """
                SELECT query_snapshot.query, query_snapshot.page, query_snapshot.language,
                       query_snapshot.brand_classification, query_snapshot.long_tail,
                       query_snapshot.search_intent,
                       SUM(query_snapshot.clicks)::double precision AS clicks,
                       SUM(query_snapshot.impressions)::double precision AS impressions,
                       CASE WHEN SUM(query_snapshot.impressions) = 0 THEN 0
                            ELSE SUM(query_snapshot.clicks) / SUM(query_snapshot.impressions) END::double precision AS ctr,
                       CASE WHEN SUM(query_snapshot.impressions) = 0 THEN 0
                            ELSE SUM(query_snapshot.average_position * query_snapshot.impressions)
                                 / SUM(query_snapshot.impressions) END::double precision
                            AS average_position
                FROM seo_query_snapshots query_snapshot
                JOIN seo_snapshots source_snapshot
                  ON source_snapshot.id = query_snapshot.seo_snapshot_id
                 AND source_snapshot.source_kind = 'LIVE_API'
                WHERE query_snapshot.snapshot_date BETWEEN ? AND ?
                GROUP BY query_snapshot.query, query_snapshot.page, query_snapshot.language,
                         query_snapshot.brand_classification, query_snapshot.long_tail,
                         query_snapshot.search_intent
                """;
        return jdbc.query(sql, (result, rowNumber) -> new AnalyticsModels.QueryAggregate(
                result.getString("query"), result.getString("page"), result.getString("language"),
                result.getString("brand_classification"), result.getBoolean("long_tail"),
                result.getString("search_intent"), result.getDouble("clicks"),
                result.getDouble("impressions"), result.getDouble("ctr"),
                result.getDouble("average_position"), null, null, null, null,
                distinctPages(result.getString("query"), start, end)), start, end);
    }

    @Transactional(readOnly = true)
    public Map<String, AnalyticsModels.QueryAggregate> aggregateQueryMap(LocalDate start, LocalDate end) {
        Map<String, AnalyticsModels.QueryAggregate> result = new LinkedHashMap<>();
        aggregateQueries(start, end).forEach(row -> result.put(queryKey(row.query(), row.page(), row.language()), row));
        return result;
    }

    @Transactional(readOnly = true)
    public String currentOpportunityState(String key) {
        List<String> states = jdbc.query(
                "SELECT state FROM seo_opportunities WHERE opportunity_key = ?",
                (result, rowNumber) -> result.getString(1), key);
        return states.isEmpty() ? null : states.getFirst();
    }

    @Transactional
    public void saveOpportunity(
            AnalyticsModels.QueryAggregate aggregate,
            AnalyticsModels.OpportunityDecision decision) {
        String key = opportunityKey(aggregate.query(), aggregate.page(), aggregate.language());
        jdbc.update("""
                INSERT INTO seo_opportunities (
                    opportunity_key, query, page, language, state, previous_state,
                    brand_classification, long_tail, search_intent, relevance, cannibalization,
                    impressions, clicks, ctr, average_position, trend, evidence,
                    first_seen_at, last_seen_at, updated_at
                ) VALUES (?, ?, ?, ?, ?, NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb,
                          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                ON CONFLICT (opportunity_key) DO UPDATE SET
                    previous_state = seo_opportunities.state,
                    state = EXCLUDED.state,
                    brand_classification = EXCLUDED.brand_classification,
                    long_tail = EXCLUDED.long_tail,
                    search_intent = EXCLUDED.search_intent,
                    relevance = EXCLUDED.relevance,
                    cannibalization = EXCLUDED.cannibalization,
                    impressions = EXCLUDED.impressions,
                    clicks = EXCLUDED.clicks,
                    ctr = EXCLUDED.ctr,
                    average_position = EXCLUDED.average_position,
                    trend = EXCLUDED.trend,
                    evidence = EXCLUDED.evidence,
                    last_seen_at = CURRENT_TIMESTAMP,
                    updated_at = CURRENT_TIMESTAMP
                """,
                key, aggregate.query(), aggregate.page(), aggregate.language(), decision.state().name(),
                aggregate.brandClassification(), aggregate.longTail(), aggregate.searchIntent(),
                decision.relevance(), decision.cannibalization(), aggregate.impressions(), aggregate.clicks(),
                aggregate.ctr(), aggregate.averagePosition(), decision.trend().name(), json(decision.evidence()));
    }

    @Transactional
    public void saveContentGap(AnalyticsModels.QueryAggregate aggregate, Map<String, Object> evidence) {
        String key = hash("gap|" + aggregate.query() + "|" + aggregate.language());
        jdbc.update("""
                INSERT INTO seo_content_gaps (
                    gap_key, query, language, search_intent, status, evidence,
                    first_seen_at, last_seen_at, updated_at
                ) VALUES (?, ?, ?, ?, 'DISCOVERED', ?::jsonb,
                          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                ON CONFLICT (gap_key) DO UPDATE SET
                    evidence = EXCLUDED.evidence,
                    last_seen_at = CURRENT_TIMESTAMP,
                    updated_at = CURRENT_TIMESTAMP
                """, key, aggregate.query(), aggregate.language(), aggregate.searchIntent(), json(evidence));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> opportunities(int limit) {
        return jdbc.queryForList("""
                SELECT id, query, page, language, state, previous_state, brand_classification,
                       long_tail, search_intent, relevance, cannibalization, impressions,
                       clicks, ctr, average_position, trend, evidence, first_seen_at, last_seen_at
                FROM seo_opportunities
                ORDER BY CASE state
                    WHEN 'DECLINING' THEN 0 WHEN 'OPPORTUNITY' THEN 1 WHEN 'EMERGING' THEN 2
                    WHEN 'ESTABLISHED' THEN 3 ELSE 4 END,
                    impressions DESC, updated_at DESC
                LIMIT ?
                """, Math.max(1, Math.min(limit, 200)));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> contentGaps(int limit) {
        return jdbc.queryForList("""
                SELECT id, query, language, search_intent, status, evidence, first_seen_at, last_seen_at
                FROM seo_content_gaps
                ORDER BY last_seen_at DESC
                LIMIT ?
                """, Math.max(1, Math.min(limit, 200)));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> sourceStatus() {
        return jdbc.queryForList("""
                SELECT source, MAX(period_end) AS latest_period_end, MAX(created_at) AS last_synced_at,
                       COUNT(*) AS snapshot_count,
                       COUNT(*) FILTER (WHERE status = 'PARTIAL') AS partial_snapshot_count
                FROM analytics_snapshots
                GROUP BY source
                ORDER BY source
                """);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> latestFullSyncTask() {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT status, created_at, updated_at, error_code
                FROM agent_tasks
                WHERE agent_type = 'ANALYTICS' AND task_type = 'ANALYTICS_FULL_SYNC'
                ORDER BY created_at DESC
                LIMIT 1
                """);
        return rows.isEmpty() ? Map.of() : java.util.Collections.unmodifiableMap(
                new LinkedHashMap<>(rows.getFirst()));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> reports(int limit) {
        return jdbc.queryForList("""
                SELECT id, snapshot_type, period_start, period_end, dimensions, metrics, created_at
                FROM analytics_snapshots
                WHERE snapshot_type IN ('WEEKLY_REPORT', 'MONTHLY_REPORT')
                ORDER BY period_end DESC, created_at DESC
                LIMIT ?
                """, Math.max(1, Math.min(limit, 100)));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> languagePerformance(LocalDate start, LocalDate end) {
        return jdbc.queryForList("""
                SELECT page_snapshot.language,
                       SUM(page_snapshot.clicks) AS clicks,
                       SUM(page_snapshot.impressions) AS impressions,
                       CASE WHEN SUM(page_snapshot.impressions) = 0 THEN 0
                            ELSE SUM(page_snapshot.clicks) / SUM(page_snapshot.impressions) END AS ctr,
                       CASE WHEN SUM(page_snapshot.impressions) = 0 THEN 0
                            ELSE SUM(page_snapshot.average_position * page_snapshot.impressions)
                                 / SUM(page_snapshot.impressions) END AS average_position
                FROM seo_page_snapshots page_snapshot
                JOIN seo_snapshots source_snapshot
                  ON source_snapshot.id = page_snapshot.seo_snapshot_id
                 AND source_snapshot.source_kind = 'LIVE_API'
                WHERE page_snapshot.snapshot_date BETWEEN ? AND ?
                GROUP BY page_snapshot.language
                ORDER BY impressions DESC
                """, start, end);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> devicePerformance(LocalDate start, LocalDate end) {
        return jdbc.queryForList("""
                SELECT page_snapshot.device,
                       SUM(page_snapshot.clicks) AS clicks,
                       SUM(page_snapshot.impressions) AS impressions,
                       CASE WHEN SUM(page_snapshot.impressions) = 0 THEN 0
                            ELSE SUM(page_snapshot.clicks) / SUM(page_snapshot.impressions) END AS ctr,
                       CASE WHEN SUM(page_snapshot.impressions) = 0 THEN 0
                            ELSE SUM(page_snapshot.average_position * page_snapshot.impressions)
                                 / SUM(page_snapshot.impressions) END AS average_position
                FROM seo_page_snapshots page_snapshot
                JOIN seo_snapshots source_snapshot
                  ON source_snapshot.id = page_snapshot.seo_snapshot_id
                 AND source_snapshot.source_kind = 'LIVE_API'
                WHERE page_snapshot.snapshot_date BETWEEN ? AND ?
                GROUP BY page_snapshot.device
                ORDER BY impressions DESC
                """, start, end);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> queryClassificationSummary(LocalDate start, LocalDate end) {
        return jdbc.queryForList("""
                SELECT query_snapshot.brand_classification, query_snapshot.long_tail,
                       query_snapshot.search_intent,
                       COUNT(DISTINCT query_snapshot.query) AS query_count,
                       SUM(query_snapshot.clicks) AS clicks,
                       SUM(query_snapshot.impressions) AS impressions
                FROM seo_query_snapshots query_snapshot
                JOIN seo_snapshots source_snapshot
                  ON source_snapshot.id = query_snapshot.seo_snapshot_id
                 AND source_snapshot.source_kind = 'LIVE_API'
                WHERE query_snapshot.snapshot_date BETWEEN ? AND ?
                GROUP BY query_snapshot.brand_classification, query_snapshot.long_tail,
                         query_snapshot.search_intent
                ORDER BY impressions DESC
                """, start, end);
    }

    @Transactional(readOnly = true)
    public LocalDate latestSearchConsoleDate() {
        return jdbc.queryForObject(
                "SELECT MAX(period_end) FROM seo_snapshots WHERE source_kind = 'LIVE_API'",
                LocalDate.class);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> reportMetrics(LocalDate start, LocalDate end) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> ga4 = jdbc.queryForList("""
                SELECT COALESCE(SUM((metrics->>'activeUsers')::numeric), 0) AS active_users,
                       COALESCE(SUM((metrics->>'sessions')::numeric), 0) AS sessions,
                       COALESCE(SUM((metrics->>'screenPageViews')::numeric), 0) AS page_views,
                       COALESCE(SUM((metrics->>'keyEvents')::numeric), 0) AS key_events
                FROM analytics_snapshots
                WHERE source = 'GA4' AND snapshot_type = 'DAILY'
                  AND period_start BETWEEN ? AND ?
                """, start, end);
        if (!ga4.isEmpty()) {
            result.putAll(ga4.getFirst());
        }
        List<Map<String, Object>> seo = jdbc.queryForList("""
                SELECT COALESCE(SUM(clicks), 0) AS organic_clicks,
                       COALESCE(SUM(impressions), 0) AS organic_impressions,
                       CASE WHEN SUM(impressions) = 0 THEN 0
                            ELSE SUM(clicks) / SUM(impressions) END AS organic_ctr
                FROM seo_snapshots
                WHERE source_kind = 'LIVE_API' AND period_start BETWEEN ? AND ?
                """, start, end);
        if (!seo.isEmpty()) {
            result.putAll(seo.getFirst());
        }
        List<Map<String, Object>> readyRoad = jdbc.queryForList("""
                SELECT COALESCE(SUM((metrics->>'registrations')::numeric), 0) AS registrations,
                       COALESCE(SUM((metrics->>'exams')::numeric), 0) AS exams,
                       COALESCE(SUM((metrics->>'quizAttempts')::numeric), 0) AS quiz_attempts,
                       COALESCE(SUM((metrics->>'signPracticeSessions')::numeric), 0) AS sign_practice_sessions
                FROM analytics_snapshots
                WHERE source IN ('READYROAD', 'RIJVIA') AND snapshot_type = 'DAILY'
                  AND period_start BETWEEN ? AND ?
                """, start, end);
        if (!readyRoad.isEmpty()) {
            result.putAll(readyRoad.getFirst());
        }
        return Map.copyOf(result);
    }

    @Transactional
    public void saveReport(
            String type,
            LocalDate start,
            LocalDate end,
            LocalDate comparisonStart,
            LocalDate comparisonEnd,
            Map<String, Object> current,
            Map<String, Object> previous,
            Long taskId) {
        saveAnalyticsSnapshot(
                "RIJVIA", type, start, end,
                Map.of("comparisonStart", comparisonStart, "comparisonEnd", comparisonEnd),
                Map.of("current", current, "previous", previous), 1, "COMPLETE",
                List.of(), Map.of(), type + ":" + start + ":" + end, taskId);
    }

    @Transactional
    public int deleteRawBefore(LocalDate cutoff) {
        int analytics = jdbc.update("""
                DELETE FROM analytics_snapshots
                WHERE snapshot_type IN ('DAILY', 'LANGUAGE', 'DEVICE', 'PROPERTY_TOTAL')
                  AND period_end < ?
                """, cutoff);
        int search = jdbc.update(
                "DELETE FROM seo_snapshots WHERE source_kind = 'LIVE_API' AND period_end < ?",
                cutoff);
        return analytics + search;
    }

    private void saveMetricRows(
            String type,
            List<AnalyticsModels.MetricRow> rows,
            Long taskId,
            List<String> partialFailures,
            Map<String, Object> quota) {
        for (AnalyticsModels.MetricRow row : rows) {
            String key = "GA4:" + type + ":" + row.date() + ":" + hash(row.dimensions().toString());
            saveAnalyticsSnapshot(
                    "GA4", type, row.date(), row.date(), row.dimensions(), row.metrics(), 1,
                    partialFailures.isEmpty() ? "COMPLETE" : "PARTIAL", partialFailures, quota, key, taskId);
        }
    }

    private void saveAnalyticsSnapshot(
            String source,
            String type,
            LocalDate start,
            LocalDate end,
            Map<String, ?> dimensions,
            Map<String, ?> metrics,
            int recordCount,
            String status,
            List<String> partialFailures,
            Map<String, ?> quota,
            String key,
            Long taskId) {
        jdbc.update("""
                INSERT INTO analytics_snapshots (
                    source, snapshot_type, period_start, period_end, dimensions, metrics,
                    source_record_count, status, partial_failures, quota_state,
                    source_snapshot_key, task_id
                ) VALUES (?, ?, ?, ?, ?::jsonb, ?::jsonb, ?, ?, ?::jsonb, ?::jsonb, ?, ?)
                ON CONFLICT (source_snapshot_key) DO UPDATE SET
                    dimensions = EXCLUDED.dimensions,
                    metrics = EXCLUDED.metrics,
                    source_record_count = EXCLUDED.source_record_count,
                    status = EXCLUDED.status,
                    partial_failures = EXCLUDED.partial_failures,
                    quota_state = EXCLUDED.quota_state,
                    task_id = EXCLUDED.task_id,
                    created_at = CURRENT_TIMESTAMP
                """, source, type, start, end, json(dimensions), json(metrics), recordCount, status,
                json(partialFailures), json(quota), key, taskId);
    }

    private void saveQuery(Long snapshotId, AnalyticsModels.SearchRow row) {
        String language = classifier.language(row.page(), row.query());
        jdbc.update("""
                INSERT INTO seo_query_snapshots (
                    seo_snapshot_id, snapshot_date, query, page, language,
                    brand_classification, long_tail, search_intent,
                    clicks, impressions, ctr, average_position
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, snapshotId, row.date(), row.query(), row.page(), language,
                classifier.brand(row.query()).name(), classifier.longTail(row.query()),
                classifier.intent(row.query()).name(), row.clicks(), row.impressions(), row.ctr(), row.position());
    }

    private void savePage(Long snapshotId, AnalyticsModels.SearchRow row) {
        jdbc.update("""
                INSERT INTO seo_page_snapshots (
                    seo_snapshot_id, snapshot_date, page, language, device,
                    clicks, impressions, ctr, average_position
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, snapshotId, row.date(), row.page(), classifier.language(row.page(), ""), row.device(),
                row.clicks(), row.impressions(), row.ctr(), row.position());
    }

    private int distinctPages(String query, LocalDate start, LocalDate end) {
        Integer result = jdbc.queryForObject("""
                SELECT COUNT(DISTINCT page)
                FROM seo_query_snapshots query_snapshot
                JOIN seo_snapshots source_snapshot
                  ON source_snapshot.id = query_snapshot.seo_snapshot_id
                 AND source_snapshot.source_kind = 'LIVE_API'
                WHERE query_snapshot.query = ?
                  AND query_snapshot.snapshot_date BETWEEN ? AND ?
                  AND query_snapshot.page <> ''
                """, Integer.class, query, start, end);
        return result == null ? 0 : result;
    }

    private static Map<String, Object> searchMetrics(AnalyticsModels.SearchRow row) {
        return Map.of(
                "clicks", row.clicks(),
                "impressions", row.impressions(),
                "ctr", row.ctr(),
                "averagePosition", row.position());
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Analytics snapshot could not be serialized", error);
        }
    }

    private static long number(Object value) {
        return value instanceof Number number ? number.longValue() : 0;
    }

    public static String opportunityKey(String query, String page, String language) {
        return hash("opportunity|" + query + "|" + page + "|" + language);
    }

    public static String queryKey(String query, String page, String language) {
        return query + "\u0000" + page + "\u0000" + language;
    }

    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("SHA-256 is unavailable", error);
        }
    }
}
