package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialArticleVersionStore {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    void lockArticle(long articleId) {
        List<Long> ids = jdbc.query(
                "SELECT id FROM articles WHERE id = ? FOR UPDATE",
                (result, rowNumber) -> result.getLong("id"),
                articleId);
        if (ids.isEmpty()) {
            throw new IllegalArgumentException("Unknown article: " + articleId);
        }
    }

    EditorialArticleVersionDtos.Version append(
            EditorialArticleVersionDtos.AppendRequest request,
            String language,
            JsonNode metadata,
            JsonNode generationMetadata,
            String actor) {
        Integer versionNumber = jdbc.queryForObject("""
                SELECT COALESCE(max(version_number), 0) + 1
                FROM article_versions
                WHERE article_id = ? AND language = ?
                """, Integer.class, request.articleId(), language);
        jdbc.update("""
                UPDATE article_versions
                SET is_current = FALSE
                WHERE article_id = ? AND language = ? AND is_current
                """, request.articleId(), language);
        Long id = jdbc.queryForObject("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, slug, summary, body,
                    metadata, generation_metadata, status, is_current, created_by
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?, TRUE, ?)
                RETURNING id
                """, Long.class,
                request.articleId(), versionNumber, language, request.title().trim(),
                blankToNull(request.slug()), blankToNull(request.summary()), request.body(),
                metadata.toString(), generationMetadata.toString(), request.status().trim(), actor);
        return require(id);
    }

    List<EditorialArticleVersionDtos.Version> history(long articleId, String language) {
        return jdbc.query("""
                SELECT id, article_id, version_number, language, title, slug, summary, body,
                       metadata, generation_metadata, status, is_current, created_at, created_by
                FROM article_versions
                WHERE article_id = ? AND language = ?
                ORDER BY version_number DESC
                """, this::version, articleId, language);
    }

    Optional<EditorialArticleVersionDtos.Version> current(long articleId, String language) {
        return jdbc.query("""
                SELECT id, article_id, version_number, language, title, slug, summary, body,
                       metadata, generation_metadata, status, is_current, created_at, created_by
                FROM article_versions
                WHERE article_id = ? AND language = ? AND is_current
                """, this::version, articleId, language).stream().findFirst();
    }

    private EditorialArticleVersionDtos.Version require(long id) {
        return jdbc.query("""
                SELECT id, article_id, version_number, language, title, slug, summary, body,
                       metadata, generation_metadata, status, is_current, created_at, created_by
                FROM article_versions
                WHERE id = ?
                """, this::version, id).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Article version was not persisted: " + id));
    }

    private EditorialArticleVersionDtos.Version version(ResultSet result, int rowNumber) throws SQLException {
        OffsetDateTime createdAt = result.getObject("created_at", OffsetDateTime.class);
        return new EditorialArticleVersionDtos.Version(
                result.getLong("id"), result.getLong("article_id"),
                result.getInt("version_number"), result.getString("language"),
                result.getString("title"), result.getString("slug"), result.getString("summary"),
                result.getString("body"), json(result.getString("metadata")),
                json(result.getString("generation_metadata")), result.getString("status"),
                result.getBoolean("is_current"), createdAt.toInstant(), result.getString("created_by"));
    }

    private JsonNode json(String value) {
        try {
            return objectMapper.readTree(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored article version JSON is invalid", exception);
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
