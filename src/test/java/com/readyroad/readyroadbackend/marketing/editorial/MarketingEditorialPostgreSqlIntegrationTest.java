package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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

@SpringBootTest
@ActiveProfiles("postgresql")
@Testcontainers
class MarketingEditorialPostgreSqlIntegrationTest {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add("readyroad.marketing.enabled", () -> "false");
        registry.add("jwt.secret-key",
                () -> "ZWRpdG9yaWFsLWludGVncmF0aW9uLXRlc3Qta2V5LW5vdC1mb3ItcHJvZHVjdGlvbg==");
        registry.add("readyroad.admin.default-password", () -> "Editorial-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired EditorialBacklogService service;
    @Autowired EditorialPriorityService priorityService;
    @Autowired EditorialPrioritySettingsService prioritySettingsService;
    @Autowired EditorialPriorityTaskService priorityTaskService;
    @Autowired ObjectMapper objectMapper;

    @BeforeEach
    void resetPriorityTestData() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.update("DELETE FROM article_priorities");
        jdbc.update("""
                UPDATE article_topics
                SET article_priority = NULL, priority_reason = NULL,
                    source_opportunity_id = NULL, content_pillar_id = NULL,
                    funnel_stage_id = NULL, conversion_goal_id = NULL,
                    supporting_pages = '[]'::jsonb, internal_link_targets = '[]'::jsonb
                """);
        jdbc.update("DELETE FROM seo_content_gaps WHERE gap_key = 'editorial-priority-gap'");
        jdbc.update("DELETE FROM seo_opportunities WHERE opportunity_key = 'editorial-priority-test'");
        jdbc.update("DELETE FROM audit_logs WHERE event_type = 'EDITORIAL_PRIORITIES_RECALCULATED'");
        jdbc.update("DELETE FROM agent_tasks WHERE agent_type = 'EDITORIAL'");
        jdbc.update("DELETE FROM marketing_conversion_goals WHERE goal_key = 'EDITORIAL_PRIORITY_TEST'");
    }

    @Test
    void seedsTheExactOfficialBacklogWithoutInventingFutureStrategyDecisions() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_topics", Integer.class)).isEqualTo(40);
        assertThat(jdbc.queryForObject(
                "SELECT count(DISTINCT official_backlog_order) FROM article_topics", Integer.class))
                .isEqualTo(40);
        assertThat(jdbc.queryForObject(
                "SELECT min(official_backlog_order) FROM article_topics", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject(
                "SELECT max(official_backlog_order) FROM article_topics", Integer.class)).isEqualTo(40);
        assertThat(jdbc.queryForList(
                "SELECT official_backlog_order FROM article_topics WHERE pillar ORDER BY official_backlog_order",
                Integer.class)).containsExactly(1, 9, 15, 21, 28, 35);
        assertThat(jdbc.queryForList("""
                SELECT count(*) FROM article_topics
                GROUP BY cluster_order
                ORDER BY cluster_order
                """, Integer.class)).containsExactly(8, 6, 6, 7, 7, 6);
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM article_topics
                WHERE article_priority IS NOT NULL
                   OR priority_reason IS NOT NULL
                   OR source_opportunity_id IS NOT NULL
                   OR content_pillar_id IS NOT NULL
                   OR funnel_stage_id IS NOT NULL
                   OR conversion_goal_id IS NOT NULL
                   OR target_queries <> '[]'::jsonb
                   OR supporting_pages <> '[]'::jsonb
                   OR internal_link_targets <> '[]'::jsonb
                """, Integer.class)).isZero();
    }

    @Test
    void exposesTheBacklogInOfficialOrderWithAccurateSummaryCounts() {
        EditorialDtos.Backlog response = service.backlog();

        assertThat(response.total()).isEqualTo(40);
        assertThat(response.pillars()).isEqualTo(6);
        assertThat(response.unresolvedStrategyContext()).isEqualTo(40);
        assertThat(response.topics()).hasSize(40);
        assertThat(response.topics()).extracting(EditorialDtos.Topic::officialOrder)
                .containsExactlyElementsOf(java.util.stream.IntStream.rangeClosed(1, 40).boxed().toList());
        assertThat(response.topics()).extracting(EditorialDtos.Topic::title)
                .doesNotHaveDuplicates()
                .allSatisfy(title -> assertThat(title).isNotBlank());
        assertThat(response.topics()).extracting(EditorialDtos.Topic::primaryLanguage)
                .containsOnlyNulls();
    }

    @Test
    void seedsTheOwnerApprovedConfigAndRejectsInvalidWeightTotals() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        var config = prioritySettingsService.current();

        assertThat(config.weights().values().stream()
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add))
                .isEqualByComparingTo("100");
        assertThat(config.p0()).isEqualByComparingTo("80");
        assertThat(config.p1()).isEqualByComparingTo("60");
        assertThat(config.p2()).isEqualByComparingTo("40");
        assertThat(config.p3()).isZero();
        assertThat(config.missingSearchConsolePercent()).isEqualByComparingTo("50");
        assertThat(jdbc.queryForObject("""
                SELECT interval_days FROM agent_schedules
                WHERE agent_type = 'ANALYTICS' AND schedule_key = 'analytics-full-sync'
                """, Integer.class)).isEqualTo(3);
        assertThat(jdbc.queryForObject("""
                SELECT zone_id FROM agent_schedules
                WHERE agent_type = 'ANALYTICS' AND schedule_key = 'analytics-full-sync'
                """, String.class)).isEqualTo("Europe/Brussels");
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM agent_definitions WHERE agent_type = 'EDITORIAL' AND enabled",
                Integer.class)).isOne();
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM audit_logs
                WHERE event_type = 'EDITORIAL_PRIORITY_SETTINGS_SEEDED'
                """, Integer.class)).isOne();

        var invalid = prioritySettingsService.raw().deepCopy();
        ((com.fasterxml.jackson.databind.node.ObjectNode) invalid.path("weights"))
                .put("searchDemand", 21);
        assertThatThrownBy(() -> prioritySettingsService.parse(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("total 100");
    }

    @Test
    void recalculationIsIdempotentAndMissingEvidenceKeepsTheOfficialOrderDeterministic() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        assertThat(priorityService.recalculate(null, "TEST", "integration-test")).isEqualTo(40);
        assertThat(priorityService.recalculate(null, "TEST", "integration-test")).isEqualTo(40);

        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_priorities", Integer.class))
                .isEqualTo(40);
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_topics WHERE article_priority = 'P3'",
                Integer.class)).isEqualTo(40);
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_priorities WHERE final_score = 10.000",
                Integer.class)).isEqualTo(40);
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM article_priorities
                WHERE evidence_states->>'searchConsoleOpportunity' = 'MISSING'
                """, Integer.class)).isEqualTo(40);
        assertThat(priorityService.priorities())
                .extracting(EditorialDtos.Priority::officialOrder)
                .containsExactlyElementsOf(java.util.stream.IntStream.rangeClosed(1, 40).boxed().toList());
    }

    @Test
    void realLinkedEvidenceRaisesPriorityWithoutFabricatingUnavailableFactors() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Long pillarId = jdbc.queryForObject(
                "SELECT id FROM marketing_content_pillars WHERE active ORDER BY priority DESC, id LIMIT 1",
                Long.class);
        Long funnelId = jdbc.queryForObject(
                "SELECT id FROM marketing_funnel_stages WHERE active ORDER BY sequence_number, id LIMIT 1",
                Long.class);
        Long conversionId = jdbc.queryForObject("""
                INSERT INTO marketing_conversion_goals (
                    goal_key, name, primary_cta, funnel_stage_id, active, approved_by
                ) VALUES (
                    'EDITORIAL_PRIORITY_TEST', 'Editorial priority test goal',
                    'Start practice', ?, TRUE, 'INTEGRATION_TEST'
                ) RETURNING id
                """, Long.class, funnelId);
        Long opportunityId = jdbc.queryForObject("""
                INSERT INTO seo_opportunities (
                    opportunity_key, query, page, language, state, previous_state,
                    brand_classification, long_tail, search_intent, relevance,
                    cannibalization, impressions, clicks, ctr, average_position,
                    trend, evidence, first_seen_at, last_seen_at
                ) VALUES (
                    'editorial-priority-test', 'امتحان السياقة النظري',
                    'https://readyroad.be/ar', 'AR', 'OPPORTUNITY', 'EMERGING',
                    'NON_BRAND', TRUE, 'INFORMATIONAL', TRUE,
                    FALSE, 50, 5, 0.10, 8,
                    'IMPROVING', '{"hasHistoricalComparison":true}'::jsonb,
                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                ) RETURNING id
                """, Long.class);
        jdbc.update("""
                INSERT INTO seo_content_gaps (
                    gap_key, query, language, search_intent, status,
                    evidence, first_seen_at, last_seen_at
                ) VALUES (
                    'editorial-priority-gap', 'امتحان السياقة النظري', 'AR',
                    'INFORMATIONAL', 'DISCOVERED', '{}'::jsonb,
                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                )
                """);
        jdbc.update("""
                UPDATE article_topics
                SET source_opportunity_id = ?, content_pillar_id = ?, funnel_stage_id = ?,
                    conversion_goal_id = ?, supporting_pages = '["/ar/lessons"]'::jsonb,
                    internal_link_targets = '["/ar/exam"]'::jsonb
                WHERE official_backlog_order = 1
                """, opportunityId, pillarId, funnelId, conversionId);

        priorityService.recalculate(null, "TEST_EVIDENCE", "integration-test");

        EditorialDtos.Priority first = priorityService.priorities().getFirst();
        assertThat(first.officialOrder()).isEqualTo(1);
        assertThat(first.finalScore()).isEqualByComparingTo("90.000");
        assertThat(first.priority()).isEqualTo("P0");
        assertThat(first.searchConsoleScore()).isEqualByComparingTo("100");
        assertThat(first.searchDemandScore()).isEqualByComparingTo("100");
        assertThat(first.businessRelevanceScore()).isEqualByComparingTo("100");
        assertThat(first.evidenceStates()).contains(
                "\"multilingualOpportunity\": \"MISSING\"",
                "\"contentFreshnessNeed\": \"MISSING\"");
    }

    @Test
    void priorityTaskCreationUsesTheUnifiedIdempotencyScope() {
        var first = priorityTaskService.enqueue("MANUAL", "same-trigger", "integration-test");
        var duplicate = priorityTaskService.enqueue("MANUAL", "same-trigger", "integration-test");

        assertThat(first.created()).isTrue();
        assertThat(duplicate.created()).isFalse();
        assertThat(duplicate.task().getId()).isEqualTo(first.task().getId());
    }
}
