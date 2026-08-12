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
            "seo_page_snapshots", "seo_opportunities", "seo_content_gaps");

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
    @Autowired AgentScheduleRepository scheduleRepository;

    private JdbcTemplate jdbc;

    @BeforeEach
    void clean() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.update("TRUNCATE seo_content_gaps, seo_opportunities, seo_page_snapshots, "
                + "seo_query_snapshots, seo_snapshots, analytics_snapshots RESTART IDENTITY CASCADE");
    }

    @Test
    void migrationCreatesExactlyTheSixAnalyticsTablesAndDisabledSchedules() {
        List<String> tables = jdbc.queryForList("""
                SELECT table_name FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name IN (
                    'analytics_snapshots', 'seo_snapshots', 'seo_query_snapshots',
                    'seo_page_snapshots', 'seo_opportunities', 'seo_content_gaps')
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
    void storesReadyRoadDailyMetricsWithoutUsingProductionData() {
        LocalDate date = LocalDate.of(2026, 8, 10);

        store.saveReadyRoad(date, date, null, List.of());

        assertThat(jdbc.queryForObject("""
                SELECT metrics->>'registrations' FROM analytics_snapshots
                WHERE source = 'READYROAD' AND period_start = '2026-08-10'
                """, String.class)).isEqualTo("0");
    }
}
