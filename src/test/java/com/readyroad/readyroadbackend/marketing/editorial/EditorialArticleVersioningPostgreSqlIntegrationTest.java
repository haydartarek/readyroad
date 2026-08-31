package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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
class EditorialArticleVersioningPostgreSqlIntegrationTest {

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
                () -> "dmVyc2lvbmluZy1pbnRlZ3JhdGlvbi10ZXN0LWtleS1ub3QtZm9yLXByb2R1Y3Rpb24=");
        registry.add("readyroad.admin.default-password", () -> "Versioning-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired EditorialArticleVersionService service;
    @Autowired ObjectMapper objectMapper;

    @BeforeEach
    void resetVersioningData() {
        new JdbcTemplate(dataSource).execute("""
                TRUNCATE article_refresh_recommendations, article_performance_snapshots,
                         article_publications, article_image_localizations,
                         article_image_variants, article_image_assets, article_versions, articles
                RESTART IDENTITY
                """);
    }

    @Test
    void appendsVersionsWithoutOverwritingHistoricalContent() {
        long articleId = insertArticle(1, "version-history");

        var first = service.append(request(articleId, "EN", "First title", "First body"), "editor-one");
        var second = service.append(request(articleId, "EN", "Second title", "Second body"), "editor-two");

        assertThat(first.versionNumber()).isEqualTo(1);
        assertThat(second.versionNumber()).isEqualTo(2);
        assertThat(service.current(articleId, "EN")).contains(second);
        assertThat(service.history(articleId, "EN"))
                .extracting(EditorialArticleVersionDtos.Version::versionNumber)
                .containsExactly(2, 1);
        assertThat(service.history(articleId, "EN"))
                .extracting(EditorialArticleVersionDtos.Version::body)
                .containsExactly("Second body", "First body");
        assertThat(currentCount(articleId)).isOne();
    }

    @Test
    void keepsIndependentCurrentVersionsForAllSupportedLanguages() {
        long articleId = insertArticle(2, "localized-history");

        for (String language : List.of("AR", "NL", "FR", "EN")) {
            service.append(request(articleId, language, language + " title", language + " body"), "editor");
        }
        var arabicRevision = service.append(
                request(articleId, "AR", "عنوان محدث", "محتوى محدث"), "editor");

        assertThat(arabicRevision.versionNumber()).isEqualTo(2);
        assertThat(currentCount(articleId)).isEqualTo(4);
        assertThat(service.history(articleId, "AR"))
                .extracting(EditorialArticleVersionDtos.Version::versionNumber)
                .containsExactly(2, 1);
        for (String language : List.of("NL", "FR", "EN")) {
            assertThat(service.current(articleId, language)).get()
                    .extracting(EditorialArticleVersionDtos.Version::versionNumber)
                    .isEqualTo(1);
        }
    }

    @Test
    void serializesConcurrentAppendsIntoDeterministicVersionNumbers() throws Exception {
        long articleId = insertArticle(3, "concurrent-history");
        service.append(request(articleId, "EN", "Initial", "Initial body"), "editor");
        CountDownLatch start = new CountDownLatch(1);

        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> {
                start.await();
                return service.append(request(articleId, "EN", "Concurrent A", "Body A"), "editor-a");
            });
            var second = executor.submit(() -> {
                start.await();
                return service.append(request(articleId, "EN", "Concurrent B", "Body B"), "editor-b");
            });
            start.countDown();

            assertThat(List.of(first.get().versionNumber(), second.get().versionNumber()))
                    .containsExactlyInAnyOrder(2, 3);
        }

        assertThat(service.history(articleId, "EN"))
                .extracting(EditorialArticleVersionDtos.Version::versionNumber)
                .containsExactly(3, 2, 1);
        assertThat(currentCount(articleId)).isOne();
    }

    @Test
    void databaseRejectsHistoricalContentMutationAndDeletion() {
        long articleId = insertArticle(4, "immutable-history");
        var version = service.append(request(articleId, "EN", "Immutable", "Original body"), "editor");
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        assertThatThrownBy(() -> jdbc.update(
                "UPDATE article_versions SET body = 'Rewritten' WHERE id = ?", version.id()))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update(
                "DELETE FROM article_versions WHERE id = ?", version.id()))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThat(service.current(articleId, "EN")).get()
                .extracting(EditorialArticleVersionDtos.Version::body)
                .isEqualTo("Original body");
    }

    private EditorialArticleVersionDtos.AppendRequest request(
            long articleId,
            String language,
            String title,
            String body) {
        return new EditorialArticleVersionDtos.AppendRequest(
                articleId, language, title, "versioning-" + language.toLowerCase(),
                "Summary", body, objectMapper.createObjectNode(),
                objectMapper.createObjectNode(), "DRAFT");
    }

    private long insertArticle(int backlogOrder, String canonicalKey) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Long topicId = jdbc.queryForObject(
                "SELECT id FROM article_topics WHERE official_backlog_order = ?",
                Long.class,
                backlogOrder);
        return jdbc.queryForObject("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language
                ) VALUES (?, ?, 'IDEA', 'EN')
                RETURNING id
                """, Long.class, topicId, canonicalKey);
    }

    private int currentCount(long articleId) {
        return new JdbcTemplate(dataSource).queryForObject("""
                SELECT count(*) FROM article_versions
                WHERE article_id = ? AND is_current
                """, Integer.class, articleId);
    }
}
