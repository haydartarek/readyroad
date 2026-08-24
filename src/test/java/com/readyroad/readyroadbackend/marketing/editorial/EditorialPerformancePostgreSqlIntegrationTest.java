package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import com.readyroad.readyroadbackend.marketing.worker.MarketingTaskDispatcher;
import java.time.LocalDate;
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
class EditorialPerformancePostgreSqlIntegrationTest {

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
                () -> "ZWRpdG9yaWFsLXBlcmZvcm1hbmNlLXRlc3Qta2V5LW5vdC1mb3ItcHJvZA==");
        registry.add("readyroad.admin.default-password", () -> "Performance-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired TaskCreationService taskCreationService;
    @Autowired AgentTaskRepository taskRepository;
    @Autowired EditorialPerformanceTaskService taskService;
    @Autowired MarketingTaskDispatcher dispatcher;
    @Autowired EditorialPerformanceService performanceService;
    @Autowired ObjectMapper objectMapper;

    private JdbcTemplate jdbc;

    @BeforeEach
    void reset() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("""
                TRUNCATE article_refresh_recommendations, article_performance_snapshots,
                         article_publications, article_image_licenses, article_image_localizations,
                         article_image_variants, article_image_assets, article_versions, article_briefs, articles
                RESTART IDENTITY
                """);
        jdbc.update("DELETE FROM seo_page_snapshots");
        jdbc.update("DELETE FROM seo_query_snapshots");
        jdbc.update("DELETE FROM seo_snapshots");
        jdbc.update("DELETE FROM audit_logs WHERE entity_type = 'EDITORIAL_ARTICLE'");
        jdbc.update("DELETE FROM agent_tasks WHERE task_type IN ("
                + "'ANALYTICS_FULL_SYNC', 'ARTICLE_APPROVAL', 'ARTICLE_PUBLISH', "
                + "'ARTICLE_PERFORMANCE_SNAPSHOT', 'ARTICLE_REFRESH_RECOMMENDATION')");
    }

    @Test
    void capturesAllLocalizedRoutesAndCreatesOneIdempotentRefreshRecommendation() {
        long articleId = publishedArticle(1);
        LocalDate completedThrough = LocalDate.of(2026, 8, 23);
        seedSearchConsole("https://readyroad.be/blog/performance-1-EN", completedThrough.minusDays(30),
                10, 100, 0.10, 8);
        seedSearchConsole("https://readyroad.be/blog/performance-1-EN?source=test", completedThrough,
                3, 100, 0.03, 15);
        AgentTask analytics = analyticsTask(1);

        assertThat(taskService.enqueueAfterAnalytics(analytics.getId(), completedThrough)).isOne();
        assertThat(taskService.enqueueAfterAnalytics(analytics.getId(), completedThrough)).isZero();
        AgentTask performance = performanceTask(articleId, completedThrough);
        dispatcher.dispatch(claimed(performance));
        dispatcher.dispatch(claimed(performance));
        AgentTask recommendation = recommendationTask(articleId, completedThrough);
        dispatcher.dispatch(claimed(recommendation));
        dispatcher.dispatch(claimed(recommendation));

        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_performance_snapshots WHERE article_id = ?",
                Integer.class, articleId)).isEqualTo(4);
        assertThat(jdbc.queryForObject("""
                SELECT indexing_state FROM article_performance_snapshots
                WHERE article_id = ? AND language = 'EN'
                """, String.class, articleId)).isEqualTo("DISCOVERED");
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM article_refresh_recommendations
                WHERE article_id = ? AND recommended
                """, Integer.class, articleId)).isOne();
        assertThat(state(articleId)).isEqualTo("UPDATE_RECOMMENDED");
        assertThat(performanceService.overview(articleId).latestSnapshots()).hasSize(4);
        assertThat(performanceService.overview(articleId).latestRecommendation().reasonCodes())
                .contains("EN:POSITION_DECLINE", "EN:CLICKS_DECLINE", "EN:CTR_DECLINE");
    }

    @Test
    void lowVolumeEvidenceNeverChangesPublishedContentState() {
        long articleId = publishedArticle(2);
        LocalDate completedThrough = LocalDate.of(2026, 8, 23);
        seedSearchConsole("https://readyroad.be/ar/blog/performance-2-AR", completedThrough.minusDays(30),
                1, 2, 0.5, 5);
        AgentTask analytics = analyticsTask(2);

        taskService.enqueueAfterAnalytics(analytics.getId(), completedThrough);
        dispatcher.dispatch(claimed(performanceTask(articleId, completedThrough)));
        dispatcher.dispatch(claimed(recommendationTask(articleId, completedThrough)));

        assertThat(state(articleId)).isEqualTo("PUBLISHED");
        assertThat(performanceService.overview(articleId).latestRecommendation().recommended()).isFalse();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_versions WHERE article_id = ?",
                Integer.class, articleId)).isEqualTo(4);
    }

    private long publishedArticle(int order) {
        Long articleId = jdbc.queryForObject("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language
                ) VALUES ((SELECT id FROM article_topics WHERE official_backlog_order = ?), ?, 'PUBLISHED', 'EN')
                RETURNING id
                """, Long.class, order, "performance-" + order);
        AgentTask approval = editorialTask("ARTICLE_APPROVAL", articleId, "approval:" + articleId);
        AgentTask publication = editorialTask("ARTICLE_PUBLISH", articleId, "publication:" + articleId);
        for (String language : List.of("AR", "NL", "FR", "EN")) {
            Long versionId = jdbc.queryForObject("""
                    INSERT INTO article_versions (
                        article_id, version_number, language, title, slug, body,
                        status, is_current, created_by
                    ) VALUES (?, 1, ?, ?, ?, ?, 'PUBLISHED', TRUE, 'test')
                    RETURNING id
                    """, Long.class, articleId, language, language + " title",
                    "performance-" + order + "-" + language, language + " body");
            jdbc.update("""
                    INSERT INTO article_publications (
                        article_id, article_version_id, language, approval_task_id,
                        publication_task_id, status, published_at, published_slug
                    ) VALUES (?, ?, ?, ?, ?, 'PUBLISHED', CURRENT_TIMESTAMP, ?)
                    """, articleId, versionId, language, approval.getId(), publication.getId(),
                    "performance-" + order + "-" + language);
        }
        return articleId;
    }

    private AgentTask editorialTask(String taskType, long articleId, String key) {
        return taskCreationService.create(new CreateMarketingTaskCommand(
                "EDITORIAL", taskType, objectMapper.createObjectNode().put("articleId", articleId),
                TaskPriority.NORMAL, null, "TEST", key, null, null,
                "ARTICLE", String.valueOf(articleId), ApprovalMetadata.standingOwnerAuthorization())).task();
    }

    private AgentTask analyticsTask(int suffix) {
        return taskCreationService.create(new CreateMarketingTaskCommand(
                "ANALYTICS", "ANALYTICS_FULL_SYNC", objectMapper.createObjectNode(),
                TaskPriority.NORMAL, null, "TEST", "analytics-performance:" + suffix,
                null, null, "ANALYTICS", String.valueOf(suffix),
                ApprovalMetadata.standingOwnerAuthorization())).task();
    }

    private void seedSearchConsole(
            String page,
            LocalDate date,
            double clicks,
            double impressions,
            double ctr,
            double position) {
        Long snapshotId = jdbc.queryForObject("""
                INSERT INTO seo_snapshots (
                    site_url, period_start, period_end, clicks, impressions, ctr,
                    average_position, source_record_count, source_kind
                ) VALUES ('sc-domain:readyroad.be', ?, ?, ?, ?, ?, ?, 1, 'LIVE_API')
                RETURNING id
                """, Long.class, date, date, clicks, impressions, ctr, position);
        jdbc.update("""
                INSERT INTO seo_page_snapshots (
                    seo_snapshot_id, snapshot_date, page, language, device,
                    clicks, impressions, ctr, average_position
                ) VALUES (?, ?, ?, 'EN', 'MOBILE', ?, ?, ?, ?)
                """, snapshotId, date, page, clicks, impressions, ctr, position);
    }

    private AgentTask performanceTask(long articleId, LocalDate completedThrough) {
        return taskRepository.findByAgentTypeAndTaskTypeAndIdempotencyKey(
                "EDITORIAL", EditorialPerformanceTaskHandler.PERFORMANCE_SNAPSHOT,
                "article-performance:" + articleId + ":" + completedThrough).orElseThrow();
    }

    private AgentTask recommendationTask(long articleId, LocalDate completedThrough) {
        return taskRepository.findByAgentTypeAndTaskTypeAndIdempotencyKey(
                "EDITORIAL", EditorialPerformanceTaskHandler.REFRESH_RECOMMENDATION,
                "article-refresh:" + articleId + ":" + completedThrough).orElseThrow();
    }

    private String state(long articleId) {
        return jdbc.queryForObject(
                "SELECT lifecycle_state FROM articles WHERE id = ?", String.class, articleId);
    }

    private static ClaimedTask claimed(AgentTask task) {
        return new ClaimedTask(
                task.getId(), task.getAgentType(), task.getTaskType(), task.getPayload(),
                task.getPayloadVersion(), task.getPriority(), 1, task.getCorrelationId());
    }
}
