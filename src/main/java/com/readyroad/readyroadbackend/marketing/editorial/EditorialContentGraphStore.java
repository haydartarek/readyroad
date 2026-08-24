package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialContentGraphStore {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    List<VersionRow> currentVersions() {
        return jdbc.query("""
                SELECT article.id AS article_id,
                       topic.pillar,
                       article.lifecycle_state,
                       version.language,
                       version.title,
                       version.metadata,
                       publication.published_slug
                FROM articles article
                JOIN article_topics topic ON topic.id = article.article_topic_id
                JOIN article_versions version
                  ON version.article_id = article.id
                 AND version.is_current
                LEFT JOIN LATERAL (
                    SELECT candidate.published_slug
                    FROM article_publications candidate
                    JOIN article_versions published_version
                      ON published_version.id = candidate.article_version_id
                    WHERE candidate.article_id = article.id
                      AND candidate.language = version.language
                      AND candidate.status = 'PUBLISHED'
                      AND published_version.status = 'PUBLISHED'
                    ORDER BY candidate.published_at DESC, candidate.id DESC
                    LIMIT 1
                ) publication ON TRUE
                ORDER BY article.id, version.language
                """, this::versionRow);
    }

    private VersionRow versionRow(ResultSet result, int rowNumber) throws SQLException {
        return new VersionRow(
                result.getLong("article_id"),
                result.getBoolean("pillar"),
                result.getString("lifecycle_state"),
                result.getString("language"),
                result.getString("title"),
                readJson(result.getString("metadata")),
                result.getString("published_slug"));
    }

    private JsonNode readJson(String value) throws SQLException {
        try {
            return value == null ? objectMapper.createObjectNode() : objectMapper.readTree(value);
        } catch (JsonProcessingException error) {
            throw new SQLException("Invalid editorial content graph metadata", error);
        }
    }

    record VersionRow(
            long articleId,
            boolean pillar,
            String lifecycleState,
            String language,
            String title,
            JsonNode metadata,
            String publishedSlug) {}
}
