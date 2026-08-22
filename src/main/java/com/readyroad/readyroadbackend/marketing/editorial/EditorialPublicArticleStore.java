package com.readyroad.readyroadbackend.marketing.editorial;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialPublicArticleStore {

    private final JdbcTemplate jdbc;

    List<EditorialPublicArticleDtos.Summary> summaries(String language) {
        return jdbc.query("""
                SELECT language, published_slug, title, summary, published_at
                FROM (
                    SELECT DISTINCT ON (publication.article_id)
                           publication.article_id,
                           publication.language,
                           publication.published_slug,
                           version.title,
                           version.summary,
                           publication.published_at,
                           publication.id
                    FROM article_publications publication
                    JOIN article_versions version ON version.id = publication.article_version_id
                    JOIN articles article ON article.id = publication.article_id
                    WHERE publication.language = ?
                      AND publication.status = 'PUBLISHED'
                      AND version.status = 'PUBLISHED'
                      AND article.lifecycle_state IN ('PUBLISHED', 'UPDATE_RECOMMENDED')
                    ORDER BY publication.article_id, publication.published_at DESC, publication.id DESC
                ) published
                ORDER BY published_at DESC, article_id DESC
                """, (result, rowNumber) -> new EditorialPublicArticleDtos.Summary(
                result.getString("language"),
                result.getString("published_slug"),
                result.getString("title"),
                result.getString("summary"),
                result.getObject("published_at", OffsetDateTime.class).toInstant()), language);
    }

    Optional<ArticleRow> exact(String language, String slug) {
        return article("""
                WHERE publication.language = ?
                  AND lower(publication.published_slug) = lower(?)
                """, language, slug);
    }

    Optional<ArticleRow> byArticle(long articleId, String language) {
        return article("""
                WHERE publication.article_id = ?
                  AND publication.language = ?
                """, articleId, language);
    }

    List<Long> articleIdsForSlug(String slug) {
        return jdbc.queryForList("""
                SELECT DISTINCT publication.article_id
                FROM article_publications publication
                JOIN article_versions version ON version.id = publication.article_version_id
                JOIN articles article ON article.id = publication.article_id
                WHERE lower(publication.published_slug) = lower(?)
                  AND publication.status = 'PUBLISHED'
                  AND version.status = 'PUBLISHED'
                  AND article.lifecycle_state IN ('PUBLISHED', 'UPDATE_RECOMMENDED')
                ORDER BY publication.article_id
                """, Long.class, slug);
    }

    Map<String, String> alternateSlugs(long articleId) {
        Map<String, String> slugs = new LinkedHashMap<>();
        List<RouteSlug> rows = jdbc.query("""
                SELECT language, published_slug
                FROM (
                    SELECT DISTINCT ON (publication.language)
                           publication.language,
                           publication.published_slug,
                           publication.published_at,
                           publication.id
                    FROM article_publications publication
                    JOIN article_versions version ON version.id = publication.article_version_id
                    JOIN articles article ON article.id = publication.article_id
                    WHERE publication.article_id = ?
                      AND publication.status = 'PUBLISHED'
                      AND version.status = 'PUBLISHED'
                      AND article.lifecycle_state IN ('PUBLISHED', 'UPDATE_RECOMMENDED')
                    ORDER BY publication.language, publication.published_at DESC, publication.id DESC
                ) localized
                ORDER BY CASE language WHEN 'AR' THEN 1 WHEN 'NL' THEN 2 WHEN 'FR' THEN 3 ELSE 4 END
                """, (result, rowNumber) -> new RouteSlug(
                result.getString("language"),
                result.getString("published_slug")), articleId);
        rows.forEach(route -> slugs.put(route.language(), route.slug()));
        return Map.copyOf(slugs);
    }

    private Optional<ArticleRow> article(String predicate, Object... parameters) {
        String sql = """
                SELECT publication.article_id,
                       publication.language,
                       publication.published_slug,
                       version.title,
                       version.summary,
                       version.body,
                       version.metadata ->> 'metaTitle' AS meta_title,
                       version.metadata ->> 'metaDescription' AS meta_description,
                       publication.published_at
                FROM article_publications publication
                JOIN article_versions version ON version.id = publication.article_version_id
                JOIN articles article ON article.id = publication.article_id
                """ + predicate + """
                  AND publication.status = 'PUBLISHED'
                  AND version.status = 'PUBLISHED'
                  AND article.lifecycle_state IN ('PUBLISHED', 'UPDATE_RECOMMENDED')
                ORDER BY publication.published_at DESC, publication.id DESC
                LIMIT 1
                """;
        return jdbc.query(sql, this::articleRow, parameters).stream().findFirst();
    }

    private ArticleRow articleRow(ResultSet result, int rowNumber) throws SQLException {
        return new ArticleRow(
                result.getLong("article_id"),
                result.getString("language"),
                result.getString("published_slug"),
                result.getString("title"),
                result.getString("summary"),
                result.getString("body"),
                result.getString("meta_title"),
                result.getString("meta_description"),
                result.getObject("published_at", OffsetDateTime.class).toInstant());
    }

    record ArticleRow(
            long articleId,
            String language,
            String slug,
            String title,
            String summary,
            String body,
            String metaTitle,
            String metaDescription,
            java.time.Instant publishedAt) {}

    private record RouteSlug(String language, String slug) {}
}
