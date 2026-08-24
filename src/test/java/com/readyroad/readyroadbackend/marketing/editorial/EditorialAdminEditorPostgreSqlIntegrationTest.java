package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("postgresql")
@Testcontainers
class EditorialAdminEditorPostgreSqlIntegrationTest {

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
                () -> "ZWRpdG9yaWFsLWVkaXRvci10ZXN0LWtleS1ub3QtZm9yLXByb2R1Y3Rpb24=");
        registry.add("readyroad.admin.default-password", () -> "Editor-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired EditorialEditorService service;

    private JdbcTemplate jdbc;

    @BeforeEach
    void resetEditorData() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("""
                TRUNCATE article_refresh_recommendations, article_performance_snapshots,
                         article_publications, article_image_licenses, article_image_localizations,
                         article_image_variants, article_image_assets, article_versions, articles
                RESTART IDENTITY
                """);
        jdbc.update("DELETE FROM audit_logs WHERE event_type = 'EDITORIAL_ARTICLE_DRAFT_SAVED'");
    }

    @Test
    void exposesTheExistingEditorialBacklogWithoutCreatingArticles() {
        var workspace = service.workspace();

        assertThat(workspace.languages()).containsExactly("AR", "NL", "FR", "EN");
        assertThat(workspace.topics()).hasSizeGreaterThanOrEqualTo(40);
        assertThat(workspace.topics().getFirst().topicKey()).isEqualTo("OFFICIAL-001");
        assertThat(workspace.topics().getFirst().articleId()).isNull();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM articles", Integer.class)).isZero();
    }

    @Test
    void createsAnArticleAndAnImmutableDraftVersionOnFirstSave() {
        var saved = service.save(1, "AR", request("العنوان", "article-ar", "ملخص", "المحتوى", null), "admin");

        assertThat(saved.created()).isTrue();
        assertThat(saved.articleCreated()).isTrue();
        assertThat(saved.lifecycleState()).isEqualTo("PLANNED");
        assertThat(saved.version().versionNumber()).isOne();
        assertThat(saved.version().language()).isEqualTo("AR");
        assertThat(saved.version().metaTitle()).isEqualTo("SEO العنوان");
        assertThat(saved.version().metaDescription()).isEqualTo("SEO ملخص");
        assertThat(saved.version().createdBy()).isEqualTo("admin");
        assertThat(service.versions(saved.articleId(), "AR")).hasSize(1);
        assertThat(auditCount(saved.articleId())).isOne();
    }

    @Test
    void persistsValidatedInternalLinksInsideTheImmutableVersionMetadata() {
        var saved = service.save(1, "EN", new EditorialEditorDtos.SaveRequest(
                "Title",
                "linked-article",
                "Summary",
                "Body",
                "SEO Title",
                "SEO Summary",
                List.of(new EditorialInternalLinkDtos.Input("/exam", "Start the theory exam")),
                null), "admin");

        assertThat(saved.version().internalLinks()).containsExactly(
                new EditorialInternalLinkDtos.Link("EXAM", "/exam", "Start the theory exam"));
        assertThat(jdbc.queryForObject("""
                SELECT metadata -> 'internalLinks' -> 0 ->> 'targetPath'
                FROM article_versions WHERE id = ?
                """, String.class, saved.version().id())).isEqualTo("/exam");
    }

    @Test
    void derivesTheContentGraphAndReportsOnlyDisconnectedArticleVersionsAsOrphans() {
        service.save(1, "EN", new EditorialEditorDtos.SaveRequest(
                "Connected",
                "connected",
                "Summary",
                "Body",
                "Connected | RijVia",
                "Connected description",
                List.of(new EditorialInternalLinkDtos.Input("/exam", "Start the theory exam")),
                null), "admin");
        service.save(2, "EN", request(
                "Disconnected", "disconnected", "Summary", "Body", null), "admin");

        var graph = service.workspace().contentGraph();

        assertThat(graph.articleNodeCount()).isEqualTo(2);
        assertThat(graph.assetNodeCount()).isOne();
        assertThat(graph.edgeCount()).isOne();
        assertThat(graph.orphanArticleCount()).isOne();
        assertThat(graph.orphanArticles())
                .extracting(EditorialContentGraphDtos.OrphanArticle::title)
                .containsExactly("Disconnected");
        assertThat(graph.edges().getFirst().targetPath()).isEqualTo("/exam");
    }

    @Test
    void treatsAnIdenticalRepeatedSaveAsIdempotent() {
        var request = request("Title", "article-en", "Summary", "Body", null);
        var first = service.save(1, "EN", request, "admin");
        var repeated = service.save(1, "EN", request, "admin");

        assertThat(first.created()).isTrue();
        assertThat(repeated.created()).isFalse();
        assertThat(repeated.version().id()).isEqualTo(first.version().id());
        assertThat(service.versions(first.articleId(), "EN")).hasSize(1);
        assertThat(auditCount(first.articleId())).isOne();
    }

    @Test
    void appendsChangedContentAndRejectsAStaleEditor() {
        var first = service.save(1, "EN", request("First", "first", "Summary", "First body", null), "admin");
        var second = service.save(1, "EN", request("Second", "second", "Summary", "Second body", 1), "admin");

        assertThat(second.version().versionNumber()).isEqualTo(2);
        assertThat(service.versions(first.articleId(), "EN"))
                .extracting(EditorialEditorDtos.Version::versionNumber)
                .containsExactly(2, 1);
        assertThatThrownBy(() -> service.save(
                1, "EN", request("Stale", "stale", "Summary", "Stale body", 1), "admin"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");
        assertThat(service.versions(first.articleId(), "EN")).hasSize(2);
        assertThat(auditCount(first.articleId())).isEqualTo(2);
    }

    @Test
    void serializesConcurrentRepeatedSavesWithoutDuplicateVersions() throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        var request = request("Concurrent", "concurrent", "Summary", "Body", null);

        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> {
                start.await();
                return service.save(2, "EN", request, "admin-a");
            });
            var second = executor.submit(() -> {
                start.await();
                return service.save(2, "EN", request, "admin-b");
            });
            start.countDown();

            assertThat(List.of(first.get().created(), second.get().created()))
                    .containsExactlyInAnyOrder(true, false);
        }

        long articleId = service.workspace().topics().stream()
                .filter(topic -> topic.topicId() == 2)
                .findFirst()
                .orElseThrow()
                .articleId();
        assertThat(service.versions(articleId, "EN")).hasSize(1);
        assertThat(auditCount(articleId)).isOne();
    }

    private EditorialEditorDtos.SaveRequest request(
            String title,
            String slug,
            String summary,
            String body,
            Integer expectedCurrentVersion) {
        return new EditorialEditorDtos.SaveRequest(
                title,
                slug,
                summary,
                body,
                "SEO " + title,
                "SEO " + summary,
                expectedCurrentVersion);
    }

    private int auditCount(long articleId) {
        return jdbc.queryForObject("""
                SELECT count(*) FROM audit_logs
                WHERE event_type = 'EDITORIAL_ARTICLE_DRAFT_SAVED'
                  AND entity_type = 'EDITORIAL_ARTICLE'
                  AND entity_id = ?
                """, Integer.class, String.valueOf(articleId));
    }
}
