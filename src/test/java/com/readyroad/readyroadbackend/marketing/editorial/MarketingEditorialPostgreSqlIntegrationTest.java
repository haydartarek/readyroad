package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.sql.DataSource;
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
}
