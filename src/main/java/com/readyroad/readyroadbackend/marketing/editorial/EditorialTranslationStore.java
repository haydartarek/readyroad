package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialTranslationStore {

    private static final Set<String> REQUIRED_LANGUAGES = Set.of("AR", "NL", "FR", "EN");

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    TranslationContext context(long articleId) {
        return jdbc.query("""
                SELECT article.id,
                       article.lifecycle_state,
                       article.canonical_language,
                       version.id AS source_version_id,
                       version.version_number AS source_version_number,
                       version.title,
                       version.slug,
                       version.summary,
                       version.body,
                       version.metadata,
                       version.generation_metadata
                FROM articles article
                JOIN article_versions version
                  ON version.article_id = article.id
                 AND version.language = article.canonical_language
                 AND version.is_current
                WHERE article.id = ?
                """, this::mapContext, articleId).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "A current canonical article version is required for translation"));
    }

    TranslationContext lockContext(long articleId) {
        Integer locked = jdbc.query("""
                SELECT id
                FROM articles
                WHERE id = ?
                FOR UPDATE
                """, (result, rowNumber) -> result.getInt("id"), articleId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown article: " + articleId));

        if (locked <= 0) {
            throw new IllegalArgumentException("Unknown article: " + articleId);
        }

        return context(articleId);
    }

    Set<String> currentLanguages(long articleId) {
        return jdbc.queryForList("""
                SELECT language
                FROM article_versions
                WHERE article_id = ?
                  AND is_current
                """, String.class, articleId).stream()
                .collect(Collectors.toUnmodifiableSet());
    }

    boolean hasAllRequiredCurrentLanguages(long articleId) {
        return currentLanguages(articleId).containsAll(REQUIRED_LANGUAGES);
    }

    private TranslationContext mapContext(ResultSet result, int rowNumber) throws SQLException {
        return new TranslationContext(
                result.getLong("id"),
                EditorialArticleState.valueOf(result.getString("lifecycle_state")),
                result.getString("canonical_language"),
                result.getLong("source_version_id"),
                result.getInt("source_version_number"),
                result.getString("title"),
                result.getString("slug"),
                result.getString("summary"),
                result.getString("body"),
                json(result.getString("metadata")),
                json(result.getString("generation_metadata")));
    }

    private JsonNode json(String value) {
        if (value == null || value.isBlank()) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(value);
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Stored article version JSON is invalid", error);
        }
    }

    record TranslationContext(
            long articleId,
            EditorialArticleState state,
            String canonicalLanguage,
            long sourceVersionId,
            int sourceVersionNumber,
            String title,
            String slug,
            String summary,
            String body,
            JsonNode metadata,
            JsonNode generationMetadata) {}
}