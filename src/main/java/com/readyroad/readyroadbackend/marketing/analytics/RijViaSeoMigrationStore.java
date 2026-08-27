package com.readyroad.readyroadbackend.marketing.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
public class RijViaSeoMigrationStore {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;
    private final MarketingAuditService auditService;
    private final MarketingProperties marketingProperties;

    @Transactional(readOnly = true)
    public Long importIdByHash(String sha256) {
        List<Long> ids = jdbc.query(
                "SELECT id FROM search_console_import_snapshots WHERE file_sha256 = ?",
                (result, rowNumber) -> result.getLong(1), sha256);
        return ids.isEmpty() ? null : ids.getFirst();
    }

    @Transactional
    public ImportResult save(
            SearchConsoleWorkbookParser.ParsedWorkbook workbook,
            RijViaSeoOpportunityEngine.Analysis analysis,
            String actor) {
        Long existing = importIdByHash(workbook.sha256());
        if (existing != null) {
            auditService.recordEntityEvent(
                    "SEARCH_CONSOLE_LOCAL_IMPORT_REUSED", actor, "SEARCH_CONSOLE_IMPORT",
                    String.valueOf(existing), null, "search-console-import-" + workbook.sha256(),
                    objectMapper.createObjectNode().put("fileSha256", workbook.sha256()));
            return new ImportResult(existing, false, workspace(existing, true));
        }

        Map<String, Integer> sheetCounts = sheetCounts(workbook);
        Map<String, Object> summary = summary(workbook, sheetCounts);
        Map<String, Object> propertyTotals = map(analysis.report().get("propertyTotals"));
        String snapshotStatus = workbook.warnings().isEmpty() ? "COMPLETE" : "COMPLETE_WITH_WARNINGS";

        Long analyticsSnapshotId = jdbc.queryForObject("""
                INSERT INTO analytics_snapshots (
                    source, snapshot_type, period_start, period_end, dimensions, metrics,
                    source_record_count, status, partial_failures, quota_state,
                    source_snapshot_key, task_id
                ) VALUES (
                    'SEARCH_CONSOLE', 'LOCAL_EXCEL_IMPORT', ?, ?, ?::jsonb, ?::jsonb,
                    ?, ?, ?::jsonb, '{}'::jsonb, ?, NULL
                )
                RETURNING id
                """, Long.class,
                workbook.periodStart(), workbook.periodEnd(),
                json(Map.of("sourceFileName", workbook.sourceFileName(), "sheetCounts", sheetCounts)),
                json(Map.of(
                        "propertyTotals", propertyTotals,
                        "dimensionTotals", analysis.report().get("dimensionTotals"))),
                totalRows(sheetCounts), workbook.warnings().isEmpty() ? "COMPLETE" : "PARTIAL",
                json(workbook.warnings()), "GSC:LOCAL_EXCEL:" + workbook.sha256());

        Long seoSnapshotId = jdbc.queryForObject("""
                INSERT INTO seo_snapshots (
                    site_url, period_start, period_end, clicks, impressions, ctr,
                    average_position, source_record_count, task_id, source_kind
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL, 'LOCAL_EXCEL')
                RETURNING id
                """, Long.class,
                marketingProperties.getAnalytics().getSearchConsoleSiteUrl(),
                workbook.periodStart(), workbook.periodEnd(),
                number(propertyTotals.get("clicks")), number(propertyTotals.get("impressions")),
                number(propertyTotals.get("ctr")), number(propertyTotals.get("averagePosition")),
                workbook.chart().size());

        Long importId = jdbc.queryForObject("""
                INSERT INTO search_console_import_snapshots (
                    source_file_name, file_sha256, file_size_bytes, period_start, period_end,
                    seo_snapshot_id, analytics_snapshot_id, sheet_counts, summary, report,
                    warnings, ignored_row_count, status, imported_by
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?::jsonb, ?::jsonb, ?, ?, ?)
                RETURNING id
                """, Long.class,
                workbook.sourceFileName(), workbook.sha256(), workbook.fileSizeBytes(),
                workbook.periodStart(), workbook.periodEnd(), seoSnapshotId, analyticsSnapshotId,
                json(sheetCounts), json(summary), json(analysis.report()), json(workbook.warnings()),
                workbook.ignoredRowCount(), snapshotStatus, actor);

        for (RijViaSeoOpportunityEngine.AnalyzedRow row : analysis.queryRows()) {
            insertQuery(seoSnapshotId, importId, workbook.periodEnd(), row);
            insertOpportunity(importId, workbook.sha256(), row, false);
        }
        for (RijViaSeoOpportunityEngine.AnalyzedRow row : analysis.pageRows()) {
            insertPage(seoSnapshotId, importId, workbook.periodEnd(), row);
            insertOpportunity(importId, workbook.sha256(), row, true);
        }
        for (RijViaSeoOpportunityEngine.DraftBrief brief : analysis.draftBriefs()) {
            insertBrief(importId, brief);
        }
        reprioritizeOfficialBacklog(importId, analysis);

        auditService.recordEntityEvent(
                "SEARCH_CONSOLE_LOCAL_IMPORT_COMPLETED", actor, "SEARCH_CONSOLE_IMPORT",
                String.valueOf(importId), null, "search-console-import-" + workbook.sha256(),
                objectMapper.createObjectNode()
                        .put("fileSha256", workbook.sha256())
                        .put("queryRows", workbook.queries().size())
                        .put("pageRows", workbook.pages().size())
                        .put("productionMutation", false));

        return new ImportResult(importId, true, workspace(importId, true));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> latestWorkspace(boolean importEnabled) {
        List<Long> ids = jdbc.query(
                "SELECT id FROM search_console_import_snapshots ORDER BY imported_at DESC, id DESC LIMIT 1",
                (result, rowNumber) -> result.getLong(1));
        return ids.isEmpty() ? emptyWorkspace(importEnabled) : workspace(ids.getFirst(), importEnabled);
    }

    private Map<String, Object> workspace(Long importId, boolean importEnabled) {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Object> publishingSafety = setting("STRATEGY", "publishing.safety");
        Map<String, Object> migration = setting("STRATEGY", "seo.migration");
        Map<String, Object> social = socialWorkspace(publishingSafety);
        result.put("localImportEnabled", importEnabled);
        result.put("publishingEnabled", booleanValue(publishingSafety, "contentPublishing"));
        result.put("canonicalActivation", stringValue(migration, "activationStatus", "PENDING_RELEASE"));
        result.put("targetDomain", "rijvia.be");

        Map<String, Object> imported = jdbc.queryForObject("""
                SELECT id, source_file_name, file_sha256, file_size_bytes, period_start, period_end,
                       sheet_counts::text, summary::text, report::text, warnings::text,
                       ignored_row_count, status, imported_by, imported_at
                FROM search_console_import_snapshots
                WHERE id = ?
                """, (row, rowNumber) -> {
                    Map<String, Object> value = new LinkedHashMap<>();
                    value.put("id", row.getLong("id"));
                    value.put("sourceFileName", row.getString("source_file_name"));
                    value.put("fileSha256", row.getString("file_sha256"));
                    value.put("fileSizeBytes", row.getLong("file_size_bytes"));
                    value.put("periodStart", row.getObject("period_start", LocalDate.class));
                    value.put("periodEnd", row.getObject("period_end", LocalDate.class));
                    value.put("sheetCounts", jsonMap(row.getString("sheet_counts")));
                    value.put("summary", jsonMap(row.getString("summary")));
                    value.put("report", jsonMap(row.getString("report")));
                    value.put("warnings", jsonList(row.getString("warnings")));
                    value.put("ignoredRowCount", row.getInt("ignored_row_count"));
                    value.put("status", row.getString("status"));
                    value.put("importedBy", row.getString("imported_by"));
                    value.put("importedAt", row.getObject("imported_at"));
                    return value;
                }, importId);
        result.put("latestImport", imported);

        Map<String, Object> report = map(imported.get("report"));
        result.put("opportunities", jdbc.queryForList("""
                SELECT id, query, page, language, state, priority, brand_classification,
                       classifications, recommended_action_category, confidence_level,
                       clicks, impressions, ctr, average_position, evidence
                FROM seo_opportunities
                WHERE source_import_id = ?
                ORDER BY CASE priority WHEN 'P0' THEN 0 WHEN 'P1' THEN 1 WHEN 'P2' THEN 2 ELSE 3 END,
                         impressions DESC, id
                LIMIT 250
                """, importId));
        result.put("migrationReadiness", Map.of(
                "urlMappings", valueOrEmpty(report, "urlMappings"),
                "blockedMappings", valueOrEmpty(report, "blockedMappings"),
                "technicalSeo", valueOrEmpty(report, "technicalSeo")));
        result.put("internalLinks", valueOrEmpty(report, "internalLinkOpportunities"));
        result.put("contentBacklog", Map.of(
                "draftBriefs", draftBriefs(importId),
                "officialTopics", officialBacklog()));
        result.put("strategy", strategy());
        result.put("authority", valueOrEmpty(report, "authority"));
        result.put("social", social);
        result.put("ownerDecisionsRequired", ownerDecisions(true, social));
        return Map.copyOf(result);
    }

    private Map<String, Object> emptyWorkspace(boolean importEnabled) {
        Map<String, Object> publishingSafety = setting("STRATEGY", "publishing.safety");
        Map<String, Object> migration = setting("STRATEGY", "seo.migration");
        Map<String, Object> social = socialWorkspace(publishingSafety);
        return Map.ofEntries(
                Map.entry("localImportEnabled", importEnabled),
                Map.entry("publishingEnabled", booleanValue(publishingSafety, "contentPublishing")),
                Map.entry("canonicalActivation", stringValue(migration, "activationStatus", "PENDING_RELEASE")),
                Map.entry("targetDomain", "rijvia.be"),
                Map.entry("latestImport", Map.of()),
                Map.entry("opportunities", List.of()),
                Map.entry("migrationReadiness", Map.of()),
                Map.entry("internalLinks", List.of()),
                Map.entry("contentBacklog", Map.of("draftBriefs", List.of(), "officialTopics", officialBacklog())),
                Map.entry("strategy", strategy()),
                Map.entry("authority", Map.of("mode", "FREE_OR_EARNED_ONLY", "outreach", "DISABLED")),
                Map.entry("social", social),
                Map.entry("ownerDecisionsRequired", ownerDecisions(false, social)));
    }

    private Map<String, Object> socialWorkspace(Map<String, Object> publishingSafety) {
        Map<String, Object> configured = setting("STRATEGY", "social.official_accounts");
        boolean ownerConfirmed = booleanValue(configured, "ownerConfirmed");
        boolean publishingEnabled = booleanValue(publishingSafety, "socialPublishing");
        Map<String, Object> result = new LinkedHashMap<>(configured);
        result.put("officialHandlesConfigured", ownerConfirmed);
        result.put("draftOnly", !publishingEnabled);
        result.put("publishing", publishingEnabled ? "ENABLED" : "BLOCKED_PROVIDER_API_OAUTH");
        result.put("ownerDecisionRequired", !ownerConfirmed);
        return Map.copyOf(result);
    }

    private List<String> ownerDecisions(boolean imported, Map<String, Object> social) {
        List<String> decisions = new ArrayList<>();
        if (!imported) {
            decisions.add("Upload the local Search Console XLSX export to create an evidence snapshot.");
        }
        if (!booleanValue(social, "officialHandlesConfigured")) {
            decisions.add("Confirm official RijVia social handles before publishing.");
        }
        return List.copyOf(decisions);
    }

    private Map<String, Object> setting(String agentType, String settingKey) {
        List<String> values = jdbc.query(
                "SELECT setting_value::text FROM agent_settings WHERE agent_type = ? AND setting_key = ?",
                (result, rowNumber) -> result.getString(1), agentType, settingKey);
        return values.isEmpty() ? Map.of() : jsonMap(values.getFirst());
    }

    private static boolean booleanValue(Map<String, Object> values, String key) {
        return Boolean.TRUE.equals(values.get(key));
    }

    private static String stringValue(Map<String, Object> values, String key, String fallback) {
        Object value = values.get(key);
        return value instanceof String text && !text.isBlank() ? text : fallback;
    }

    private void insertQuery(
            Long seoSnapshotId,
            Long importId,
            LocalDate snapshotDate,
            RijViaSeoOpportunityEngine.AnalyzedRow row) {
        jdbc.update("""
                INSERT INTO seo_query_snapshots (
                    seo_snapshot_id, snapshot_date, query, page, language,
                    brand_classification, long_tail, search_intent,
                    clicks, impressions, ctr, average_position,
                    source_import_id, classifications, recommended_action_category, confidence_level
                ) VALUES (?, ?, ?, '', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?)
                """,
                seoSnapshotId, snapshotDate, row.metric().dimension(), row.language(),
                row.brandClassification(), row.classifications().contains("LONG_TAIL"),
                searchIntent(row.classifications()), row.metric().clicks(), row.metric().impressions(),
                row.metric().ctr(), row.metric().position(), importId, json(row.classifications()),
                row.recommendedActionCategory(), row.confidenceLevel());
    }

    private void insertPage(
            Long seoSnapshotId,
            Long importId,
            LocalDate snapshotDate,
            RijViaSeoOpportunityEngine.AnalyzedRow row) {
        jdbc.update("""
                INSERT INTO seo_page_snapshots (
                    seo_snapshot_id, snapshot_date, page, language, device,
                    clicks, impressions, ctr, average_position,
                    source_import_id, recommended_action_category, confidence_level
                ) VALUES (?, ?, ?, ?, 'UNKNOWN', ?, ?, ?, ?, ?, ?, ?)
                """,
                seoSnapshotId, snapshotDate, row.metric().dimension(), row.language(),
                row.metric().clicks(), row.metric().impressions(), row.metric().ctr(),
                row.metric().position(), importId, row.recommendedActionCategory(), row.confidenceLevel());
    }

    private Long insertOpportunity(
            Long importId,
            String fileHash,
            RijViaSeoOpportunityEngine.AnalyzedRow row,
            boolean page) {
        String dimensionType = page ? "PAGE" : "QUERY";
        String key = sha256("LOCAL_EXCEL|" + fileHash + "|" + dimensionType + "|" + row.metric().dimension());
        Map<String, Object> evidence = new LinkedHashMap<>();
        evidence.put("source", "LOCAL_SEARCH_CONSOLE_EXCEL");
        evidence.put("score", row.score());
        evidence.put("trend", "INSUFFICIENT_DATA");
        evidence.put("cannibalization", "NOT_ASSESSABLE_FROM_DIMENSION_EXPORT");
        evidence.put("dimensionType", dimensionType);
        return jdbc.queryForObject("""
                INSERT INTO seo_opportunities (
                    opportunity_key, query, page, language, state, previous_state,
                    brand_classification, long_tail, search_intent, relevance, cannibalization,
                    impressions, clicks, ctr, average_position, trend, evidence,
                    first_seen_at, last_seen_at, updated_at, source_import_id, priority,
                    recommended_action_category, confidence_level, classifications
                ) VALUES (?, ?, ?, ?, ?, NULL, ?, ?, ?, ?, FALSE, ?, ?, ?, ?,
                          'INSUFFICIENT_DATA', ?::jsonb, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
                          CURRENT_TIMESTAMP, ?, ?, ?, ?, ?::jsonb)
                RETURNING id
                """, Long.class,
                key, page ? "" : row.metric().dimension(), page ? row.metric().dimension() : "",
                row.language(), row.state(), row.brandClassification(),
                row.classifications().contains("LONG_TAIL"), searchIntent(row.classifications()),
                row.relevant(), row.metric().impressions(), row.metric().clicks(), row.metric().ctr(),
                row.metric().position(), json(evidence), importId, row.priority(),
                row.recommendedActionCategory(), row.confidenceLevel(), json(row.classifications()));
    }

    private void insertBrief(Long importId, RijViaSeoOpportunityEngine.DraftBrief brief) {
        jdbc.update("""
                INSERT INTO marketing_draft_briefs (
                    source_import_id, brief_key, language, working_title, purpose,
                    target_queries, supporting_pages, content_pillar_key, icp_key,
                    conversion_goal_key, status, evidence
                ) VALUES (?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?, ?, NULL,
                          'WAITING_OWNER_REVIEW', ?::jsonb)
                ON CONFLICT (source_import_id, brief_key) DO NOTHING
                """,
                importId, brief.key(), brief.language(), brief.workingTitle(), brief.purpose(),
                json(brief.targetQueries()), json(brief.supportingPages()), brief.contentPillarKey(),
                brief.icpKey(), json(brief.evidence()));
    }

    private void reprioritizeOfficialBacklog(
            Long importId,
            RijViaSeoOpportunityEngine.Analysis analysis) {
        Map<String, String> clusterPriorities = new LinkedHashMap<>();
        registerPriority(clusterPriorities, "TRAFFIC_SIGNS", analysis, "TRAFFIC_SIGN_QUERY", "TRAFFIC_SIGN_PAGE");
        registerPriority(clusterPriorities, "THEORY_EXAM", analysis, "THEORY_EXAM_QUERY", "LANDING_PAGE", "FAQ_PAGE");
        registerPriority(clusterPriorities, "PRACTICAL_EXAM", analysis, "PRACTICAL_EXAM_QUERY");
        registerPriority(clusterPriorities, "BELGIAN_LICENSE", analysis, "LOCAL_BELGIUM_QUERY");
        for (Map.Entry<String, String> entry : clusterPriorities.entrySet()) {
            jdbc.update("""
                    UPDATE article_topics
                    SET article_priority = ?,
                        priority_reason = ?,
                        updated_at = CURRENT_TIMESTAMP
                    WHERE cluster_key = ?
                      AND source_type = 'OFFICIAL_STRATEGIC_BACKLOG'
                    """,
                    entry.getValue(),
                    "Re-ranked from local Search Console import #" + importId
                            + "; no publication action was created.",
                    entry.getKey());
        }
    }

    private static void registerPriority(
            Map<String, String> target,
            String cluster,
            RijViaSeoOpportunityEngine.Analysis analysis,
            String... classifications) {
        List<RijViaSeoOpportunityEngine.AnalyzedRow> all = new ArrayList<>();
        all.addAll(analysis.queryRows());
        all.addAll(analysis.pageRows());
        all.stream()
                .filter(row -> row.classifications().stream().anyMatch(value -> List.of(classifications).contains(value)))
                .min((left, right) -> Integer.compare(priorityOrder(left.priority()), priorityOrder(right.priority())))
                .ifPresent(row -> target.put(cluster, row.priority()));
    }

    private List<Map<String, Object>> draftBriefs(Long importId) {
        return jdbc.queryForList("""
                SELECT id, brief_key, language, working_title, purpose, target_queries,
                       supporting_pages, content_pillar_key, icp_key, conversion_goal_key,
                       status, evidence, created_at
                FROM marketing_draft_briefs
                WHERE source_import_id = ?
                ORDER BY language, id
                """, importId);
    }

    private List<Map<String, Object>> officialBacklog() {
        return jdbc.queryForList("""
                SELECT topic_key, official_backlog_order, cluster_key, working_title,
                       title_language, status, article_priority, priority_reason
                FROM article_topics
                WHERE source_type = 'OFFICIAL_STRATEGIC_BACKLOG'
                ORDER BY official_backlog_order
                """);
    }

    private Map<String, Object> strategy() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("positioning", jdbc.queryForList("""
                SELECT id, statement, brand_identity, brand_voice, approved_by, version
                FROM marketing_positioning WHERE active = TRUE ORDER BY id LIMIT 1
                """));
        result.put("usps", jdbc.queryForList("""
                SELECT id, title, description, evidence_type, evidence_reference,
                       active, approved_by FROM marketing_usp WHERE active = TRUE ORDER BY priority DESC, id
                """));
        result.put("icps", jdbc.queryForList("""
                SELECT id, name, language, country, primary_goal, active, approved_by
                FROM marketing_icp WHERE active = TRUE ORDER BY id
                """));
        result.put("contentPillars", jdbc.queryForList("""
                SELECT id, pillar_key, name, priority, active, approved_by
                FROM marketing_content_pillars WHERE active = TRUE ORDER BY priority DESC, id
                """));
        result.put("settings", jdbc.queryForList("""
                SELECT setting_key, setting_value, updated_by, updated_at
                FROM agent_settings
                WHERE agent_type = 'STRATEGY'
                  AND setting_key IN (
                    'brand.identity', 'seo.migration', 'seo.language.ar', 'seo.language.nl',
                    'seo.language.fr', 'seo.language.en', 'publishing.safety', 'backlink.policy')
                ORDER BY setting_key
                """));
        return Map.copyOf(result);
    }

    private static Map<String, Integer> sheetCounts(SearchConsoleWorkbookParser.ParsedWorkbook workbook) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put(SearchConsoleWorkbookParser.QUERIES, workbook.queries().size());
        counts.put(SearchConsoleWorkbookParser.PAGES, workbook.pages().size());
        counts.put(SearchConsoleWorkbookParser.COUNTRIES, workbook.countries().size());
        counts.put(SearchConsoleWorkbookParser.DEVICES, workbook.devices().size());
        counts.put(SearchConsoleWorkbookParser.SEARCH_APPEARANCE, workbook.searchAppearance().size());
        counts.put(SearchConsoleWorkbookParser.CHART, workbook.chart().size());
        counts.put(SearchConsoleWorkbookParser.FILTERS, workbook.filters().size());
        return Map.copyOf(counts);
    }

    private static Map<String, Object> summary(
            SearchConsoleWorkbookParser.ParsedWorkbook workbook,
            Map<String, Integer> sheetCounts) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("periodStart", workbook.periodStart());
        summary.put("periodEnd", workbook.periodEnd());
        summary.put("filters", workbook.filters());
        summary.put("sheetCounts", sheetCounts);
        summary.put("ignoredRowCount", workbook.ignoredRowCount());
        summary.put("anonymizedQueryWarning", true);
        summary.put("totalsPolicy", "PROPERTY_TOTALS_FROM_DAILY_CHART_ONLY");
        return Map.copyOf(summary);
    }

    private static int totalRows(Map<String, Integer> counts) {
        return counts.entrySet().stream()
                .filter(entry -> !SearchConsoleWorkbookParser.FILTERS.equals(entry.getKey()))
                .mapToInt(Map.Entry::getValue).sum();
    }

    private static String searchIntent(List<String> classifications) {
        for (String value : List.of("INFORMATIONAL", "NAVIGATIONAL", "TRANSACTIONAL")) {
            if (classifications.contains(value)) return value;
        }
        return "UNKNOWN";
    }

    private static int priorityOrder(String priority) {
        return switch (priority) {
            case "P0" -> 0;
            case "P1" -> 1;
            case "P2" -> 2;
            default -> 3;
        };
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Marketing workspace data could not be serialized", error);
        }
    }

    private Map<String, Object> jsonMap(String value) {
        try {
            return objectMapper.readValue(value, new TypeReference<>() {});
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Stored marketing workspace JSON is invalid", error);
        }
    }

    private List<Object> jsonList(String value) {
        try {
            return objectMapper.readValue(value, new TypeReference<>() {});
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Stored marketing workspace JSON is invalid", error);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> map(Object value) {
        return value instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }

    private static Object valueOrEmpty(Map<String, Object> map, String key) {
        return map.getOrDefault(key, List.of());
    }

    private static double number(Object value) {
        return value instanceof Number number ? number.doubleValue() : 0;
    }

    private static String sha256(String value) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("SHA-256 is unavailable", error);
        }
    }

    public record ImportResult(Long importId, boolean created, Map<String, Object> workspace) {}
}
