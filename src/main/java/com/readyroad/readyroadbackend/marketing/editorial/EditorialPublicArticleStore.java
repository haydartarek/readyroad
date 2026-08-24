package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    List<SummaryRow> summaries(String language) {
        return jdbc.query("""
                WITH latest_publications AS (
                    SELECT DISTINCT ON (publication.article_id, publication.language)
                           publication.article_id,
                           publication.language,
                           publication.published_slug,
                           publication.image_asset_id,
                           version.title,
                           version.summary,
                           publication.published_at,
                           publication.id
                    FROM article_publications publication
                    JOIN article_versions version ON version.id = publication.article_version_id
                    JOIN articles article ON article.id = publication.article_id
                    WHERE publication.status = 'PUBLISHED'
                      AND version.status = 'PUBLISHED'
                      AND article.lifecycle_state IN ('PUBLISHED', 'UPDATE_RECOMMENDED')
                    ORDER BY publication.article_id, publication.language,
                             publication.published_at DESC, publication.id DESC
                )
                SELECT current_publication.article_id,
                       current_publication.language,
                       current_publication.published_slug,
                       current_publication.title,
                       current_publication.summary,
                       current_publication.published_at,
                       current_publication.image_asset_id,
                       jsonb_object_agg(
                           localized_publication.language,
                           localized_publication.published_slug
                           ORDER BY localized_publication.language
                       ) AS alternate_slugs
                FROM latest_publications current_publication
                JOIN latest_publications localized_publication
                  ON localized_publication.article_id = current_publication.article_id
                WHERE current_publication.language = ?
                GROUP BY current_publication.article_id,
                         current_publication.language,
                         current_publication.published_slug,
                         current_publication.title,
                         current_publication.summary,
                         current_publication.published_at,
                         current_publication.image_asset_id
                ORDER BY current_publication.published_at DESC,
                         current_publication.article_id DESC
                """, (result, rowNumber) -> new SummaryRow(
                result.getLong("article_id"),
                result.getString("language"),
                result.getString("published_slug"),
                result.getString("title"),
                result.getString("summary"),
                result.getObject("published_at", OffsetDateTime.class).toInstant(),
                nullableLong(result, "image_asset_id"),
                readAlternateSlugs(result)), language);
    }

    List<SummaryRow> related(
            String language,
            String targetPath,
            int limit) {
        return jdbc.query("""
                WITH latest_publications AS (
                    SELECT DISTINCT ON (publication.article_id, publication.language)
                           publication.article_id,
                           publication.language,
                           publication.published_slug,
                           version.title,
                           version.summary,
                           version.metadata,
                           publication.image_asset_id,
                           publication.published_at,
                           publication.id
                    FROM article_publications publication
                    JOIN article_versions version ON version.id = publication.article_version_id
                    JOIN articles article ON article.id = publication.article_id
                    WHERE publication.status = 'PUBLISHED'
                      AND version.status = 'PUBLISHED'
                      AND article.lifecycle_state IN ('PUBLISHED', 'UPDATE_RECOMMENDED')
                    ORDER BY publication.article_id, publication.language,
                             publication.published_at DESC, publication.id DESC
                )
                SELECT current_publication.article_id,
                       current_publication.language,
                       current_publication.published_slug,
                       current_publication.title,
                       current_publication.summary,
                       current_publication.published_at,
                       current_publication.image_asset_id,
                       jsonb_object_agg(
                           localized_publication.language,
                           localized_publication.published_slug
                           ORDER BY localized_publication.language
                       ) AS alternate_slugs
                FROM latest_publications current_publication
                JOIN latest_publications localized_publication
                  ON localized_publication.article_id = current_publication.article_id
                WHERE current_publication.language = ?
                  AND EXISTS (
                      SELECT 1
                      FROM jsonb_array_elements(
                          CASE
                              WHEN jsonb_typeof(current_publication.metadata -> 'internalLinks') = 'array'
                                  THEN current_publication.metadata -> 'internalLinks'
                              ELSE '[]'::jsonb
                          END
                      ) link
                      WHERE link ->> 'targetPath' = ?
                  )
                GROUP BY current_publication.article_id,
                         current_publication.language,
                         current_publication.published_slug,
                         current_publication.title,
                         current_publication.summary,
                         current_publication.published_at,
                         current_publication.image_asset_id
                ORDER BY current_publication.published_at DESC,
                         current_publication.article_id DESC
                LIMIT ?
                """, (result, rowNumber) -> new SummaryRow(
                result.getLong("article_id"),
                result.getString("language"),
                result.getString("published_slug"),
                result.getString("title"),
                result.getString("summary"),
                result.getObject("published_at", OffsetDateTime.class).toInstant(),
                nullableLong(result, "image_asset_id"),
                readAlternateSlugs(result)), language, targetPath, limit);
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
                       publication.image_asset_id,
                       publication.language,
                       publication.published_slug,
                       version.title,
                       version.summary,
                       version.body,
                       version.metadata ->> 'metaTitle' AS meta_title,
                       version.metadata ->> 'metaDescription' AS meta_description,
                       version.metadata AS metadata,
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
                nullableLong(result, "image_asset_id"),
                result.getString("language"),
                result.getString("published_slug"),
                result.getString("title"),
                result.getString("summary"),
                result.getString("body"),
                result.getString("meta_title"),
                result.getString("meta_description"),
                readJson(result.getString("metadata")),
                result.getObject("published_at", OffsetDateTime.class).toInstant());
    }

    private Map<String, String> readAlternateSlugs(ResultSet result) throws SQLException {
        try {
            LinkedHashMap<String, String> slugs = objectMapper.readValue(
                    result.getString("alternate_slugs"),
                    new TypeReference<>() {});
            return Map.copyOf(slugs);
        } catch (JsonProcessingException error) {
            throw new SQLException("Invalid localized article route map", error);
        }
    }

    private JsonNode readJson(String value) throws SQLException {
        try {
            return objectMapper.readTree(value);
        } catch (JsonProcessingException error) {
            throw new SQLException("Invalid published article metadata", error);
        }
    }

    record ArticleRow(
            long articleId,
            Long imageAssetId,
            String language,
            String slug,
            String title,
            String summary,
            String body,
            String metaTitle,
            String metaDescription,
            JsonNode metadata,
            java.time.Instant publishedAt) {}

    record SummaryRow(
            long articleId,
            String language,
            String slug,
            String title,
            String summary,
            java.time.Instant publishedAt,
            Long imageAssetId,
            Map<String, String> alternateSlugs) {}

    private static Long nullableLong(ResultSet result, String column) throws SQLException {
        long value = result.getLong(column);
        return result.wasNull() ? null : value;
    }

    private record RouteSlug(String language, String slug) {}
}
