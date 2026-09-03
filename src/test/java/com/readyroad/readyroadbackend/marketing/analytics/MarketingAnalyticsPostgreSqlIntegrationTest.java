package com.readyroad.readyroadbackend.marketing.analytics;

import static org.assertj.core.api.Assertions.assertThat;

import com.readyroad.readyroadbackend.marketing.domain.AgentSchedule;
import com.readyroad.readyroadbackend.marketing.repository.AgentScheduleRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("postgresql")
@Testcontainers
class MarketingAnalyticsPostgreSqlIntegrationTest {

    private static final Set<String> TABLES = Set.of(
            "analytics_snapshots", "seo_snapshots", "seo_query_snapshots",
            "seo_page_snapshots", "seo_opportunities", "seo_content_gaps",
            "search_console_import_snapshots", "marketing_draft_briefs");

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add("readyroad.marketing.enabled", () -> "false");
        registry.add("jwt.secret-key",
                () -> "YW5hbHl0aWNzLXRlc3Qtand0LXNlY3JldC1ub3QtZm9yLXByb2R1Y3Rpb24=");
        registry.add("readyroad.admin.default-password", () -> "Analytics-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired AnalyticsStore store;
    @Autowired RijViaSeoMigrationStore migrationStore;
    @Autowired RijViaSeoOpportunityEngine opportunityEngine;
    @Autowired AgentScheduleRepository scheduleRepository;

    private JdbcTemplate jdbc;

    @BeforeEach
    void clean() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.update("TRUNCATE marketing_draft_briefs, seo_content_gaps, seo_opportunities, "
                + "seo_page_snapshots, seo_query_snapshots, search_console_import_snapshots, "
                + "seo_snapshots, analytics_snapshots RESTART IDENTITY CASCADE");
    }

    @Test
    void migrationCreatesTheAnalyticsAndLocalEvidenceTablesWithDisabledSchedules() {
        List<String> tables = jdbc.queryForList("""
                SELECT table_name FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name IN (
                    'analytics_snapshots', 'seo_snapshots', 'seo_query_snapshots',
                    'seo_page_snapshots', 'seo_opportunities', 'seo_content_gaps',
                    'search_console_import_snapshots', 'marketing_draft_briefs')
                """, String.class);
        assertThat(new HashSet<>(tables)).containsExactlyInAnyOrderElementsOf(TABLES);
        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM agent_settings WHERE agent_type = 'ANALYTICS'", Integer.class)).isEqualTo(4);
        assertThat(jdbc.queryForObject("""
                SELECT character_maximum_length FROM information_schema.columns
                WHERE table_schema = 'public'
                  AND table_name = 'seo_opportunities'
                  AND column_name = 'trend'
                """, Integer.class)).isGreaterThanOrEqualTo("INSUFFICIENT_DATA".length());
        List<AgentSchedule> schedules = scheduleRepository.findByAgentTypeOrderByScheduleKeyAsc("ANALYTICS");
        assertThat(schedules).hasSize(3).allMatch(schedule -> !schedule.isEnabled());
        assertThat(schedules.stream()
                .filter(schedule -> "analytics-full-sync".equals(schedule.getScheduleKey()))
                .findFirst().orElseThrow().getIntervalDays()).isEqualTo((short) 3);
    }

    @Test
    void storesPropertyTotalsIndependentlyFromQueryAndPageRows() {
        LocalDate date = LocalDate.of(2026, 8, 10);
        var data = new AnalyticsModels.SearchConsoleData(
                List.of(new AnalyticsModels.SearchRow(date, "", "", "UNKNOWN", 10, 100, 0.10, 8)),
                List.of(new AnalyticsModels.SearchRow(
                        date, "belgian driving theory questions", "https://readyroad.be/lessons",
                        "UNKNOWN", 7, 80, 0.0875, 9)),
                List.of(new AnalyticsModels.SearchRow(
                        date, "", "https://readyroad.be/lessons", "MOBILE", 6, 70, 0.0857, 9.5)),
                Map.of());

        store.saveSearchConsole(data, "sc-domain:readyroad.be", null, List.of());

        assertThat(jdbc.queryForObject("SELECT clicks FROM seo_snapshots", Double.class)).isEqualTo(10);
        assertThat(jdbc.queryForObject("SELECT clicks FROM seo_query_snapshots", Double.class)).isEqualTo(7);
        assertThat(jdbc.queryForObject("SELECT device FROM seo_page_snapshots", String.class)).isEqualTo("MOBILE");
        assertThat(jdbc.queryForObject(
                "SELECT brand_classification FROM seo_query_snapshots", String.class)).isEqualTo("NON_BRAND");
    }

    @Test
    void storesRijViaDailyMetricsWithoutUsingProductionData() {
        LocalDate date = LocalDate.of(2026, 8, 10);

        store.saveRijVia(date, date, null, List.of());

        assertThat(jdbc.queryForObject("""
                SELECT metrics->>'registrations' FROM analytics_snapshots
                WHERE source = 'RIJVIA' AND period_start = '2026-08-10'
                """, String.class)).isEqualTo("0");
    }

    @Test
    void persistsTheLocalSearchConsoleEvidenceIdempotentlyAndConfiguresRijViaStrategy() {
        var workbook = new SearchConsoleWorkbookParser.ParsedWorkbook(
                "readyroad-search-console.xlsx", "a".repeat(64), 4096,
                LocalDate.of(2026, 8, 18), LocalDate.of(2026, 8, 19),
                List.of(
                        new SearchConsoleWorkbookParser.MetricRow("readyroad", 12, 38, 12d / 38d, 3),
                        new SearchConsoleWorkbookParser.MetricRow(
                                "autoweg autosnelweg verschil belgië", 0, 100, 0, 12)),
                List.of(
                        new SearchConsoleWorkbookParser.MetricRow(
                                "https://readyroad.be/fr", 1, 1158, 0.0009, 5.01),
                        new SearchConsoleWorkbookParser.MetricRow(
                                "https://readyroad.be/nl/traffic-signs/A13", 0, 189, 0, 8.38)),
                List.of(), List.of(), List.of(),
                List.of(
                        new SearchConsoleWorkbookParser.ChartRow(
                                LocalDate.of(2026, 8, 18), 20, 2500, .008, 9),
                        new SearchConsoleWorkbookParser.ChartRow(
                                LocalDate.of(2026, 8, 19), 18, 2367, .0076, 9.4)),
                Map.of("نوع البحث", "الويب"), List.of(), 0);
        var analysis = opportunityEngine.analyze(workbook, "https://rijvia.be");

        var first = migrationStore.save(workbook, analysis, "marketing-admin");
        var repeated = migrationStore.save(workbook, analysis, "marketing-admin");

        assertThat(first.created()).isTrue();
        assertThat(repeated.created()).isFalse();
        assertThat(repeated.importId()).isEqualTo(first.importId());
        assertThat(first.workspace()).containsEntry("publishingEnabled", true);
        assertThat(first.workspace()).containsEntry("canonicalActivation", "RELEASED");
        @SuppressWarnings("unchecked")
        var opportunities = (List<Map<String, Object>>) first.workspace().get("opportunities");
        assertThat(opportunities).allSatisfy(row -> {
            assertThat(row.get("classifications")).isInstanceOf(List.class);
            assertThat(row.get("evidence")).isInstanceOf(Map.class);
        });
        @SuppressWarnings("unchecked")
        var strategy = (Map<String, Object>) first.workspace().get("strategy");
        @SuppressWarnings("unchecked")
        var settings = (List<Map<String, Object>>) strategy.get("settings");
        assertThat(settings).allSatisfy(row -> assertThat(row.get("setting_value")).isInstanceOf(Map.class));
        assertThat(first.workspace().get("ownerDecisionsRequired")).isEqualTo(List.of());
        @SuppressWarnings("unchecked")
        Map<String, Object> social = (Map<String, Object>) first.workspace().get("social");
        assertThat(social)
                .containsEntry("officialHandlesConfigured", true)
                .containsEntry("publishing", "BLOCKED_PROVIDER_API_OAUTH");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM search_console_import_snapshots", Integer.class))
                .isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM seo_query_snapshots", Integer.class))
                .isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM seo_page_snapshots", Integer.class))
                .isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM seo_opportunities", Integer.class))
                .isEqualTo(4);
        assertThat(jdbc.queryForObject("""
                SELECT site_url FROM seo_snapshots WHERE source_kind = 'LOCAL_EXCEL'
                """, String.class)).isEqualTo("sc-domain:rijvia.be");
        assertThat(store.latestSearchConsoleDate()).isNull();
        assertThat(store.aggregateQueries(
                LocalDate.of(2026, 8, 18), LocalDate.of(2026, 8, 19))).isEmpty();
        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM marketing_usp WHERE active = TRUE", Integer.class)).isEqualTo(10);
        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*) FROM marketing_usp
                WHERE title = 'RijVia learning platform' AND active = TRUE
                """, Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("""
                SELECT COUNT(*) FROM marketing_content_pillars
                WHERE pillar_key = 'RIJVIA_EDUCATIONAL_VIDEOS' AND active = TRUE
                """, Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("""
                SELECT setting_value->>'activationStatus' FROM agent_settings
                WHERE agent_type = 'STRATEGY' AND setting_key = 'seo.migration'
                """, String.class)).isEqualTo("RELEASED");
    }
}
