package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialPerformanceStore {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    List<Long> publishedArticleIds() {
        return jdbc.queryForList("""
                SELECT DISTINCT article.id
                FROM articles article
                JOIN article_publications publication ON publication.article_id = article.id
                WHERE article.lifecycle_state IN ('PUBLISHED', 'UPDATE_RECOMMENDED')
                  AND publication.status = 'PUBLISHED'
                ORDER BY article.id
                """, Long.class);
    }

    List<PublicationRoute> currentPublicationRoutes(long articleId) {
        return jdbc.query("""
                SELECT DISTINCT ON (publication.language)
                       publication.id, publication.language, publication.published_slug
                FROM article_publications publication
                JOIN article_versions version ON version.id = publication.article_version_id
                WHERE publication.article_id = ?
                  AND publication.status = 'PUBLISHED'
                  AND version.status = 'PUBLISHED'
                ORDER BY publication.language, publication.published_at DESC, publication.id DESC
                """, (result, rowNumber) -> new PublicationRoute(
                        result.getLong("id"),
                        result.getString("language"),
                        path(result.getString("language"), result.getString("published_slug"))),
                articleId);
    }

    Metrics aggregate(String publishedPath, LocalDate start, LocalDate end) {
        return jdbc.query("""
                SELECT COALESCE(SUM(page.clicks), 0)::double precision AS clicks,
                       COALESCE(SUM(page.impressions), 0)::double precision AS impressions,
                       CASE WHEN COALESCE(SUM(page.impressions), 0) = 0 THEN 0
                            ELSE SUM(page.clicks) / SUM(page.impressions) END::double precision AS ctr,
                       CASE WHEN COALESCE(SUM(page.impressions), 0) = 0 THEN 0
                            ELSE SUM(page.average_position * page.impressions)
                                 / SUM(page.impressions) END::double precision AS average_position
                FROM seo_page_snapshots page
                JOIN seo_snapshots source ON source.id = page.seo_snapshot_id
                WHERE source.source_kind = 'LIVE_API'
                  AND page.snapshot_date BETWEEN ? AND ?
                  AND lower(trim(trailing '/' FROM regexp_replace(
                        split_part(split_part(page.page, '?', 1), '#', 1),
                        '^https?://[^/]+', '')))
                      = lower(trim(trailing '/' FROM ?))
                """, (result, rowNumber) -> metrics(result), start, end, publishedPath).getFirst();
    }

    SnapshotRow saveSnapshot(
            long articleId,
            PublicationRoute route,
            LocalDate periodStart,
            LocalDate periodEnd,
            LocalDate previousStart,
            LocalDate previousEnd,
            Metrics current,
            Metrics previous,
            long monitoringTaskId,
            long analyticsTaskId) {
        return jdbc.query("""
                INSERT INTO article_performance_snapshots (
                    article_id, article_publication_id, language, published_path,
                    period_start, period_end, previous_period_start, previous_period_end,
                    clicks, impressions, ctr, average_position,
                    previous_clicks, previous_impressions, previous_ctr, previous_average_position,
                    evidence_state, indexing_state, monitoring_task_id, analytics_task_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (article_publication_id, period_start, period_end) DO UPDATE SET
                    clicks = EXCLUDED.clicks,
                    impressions = EXCLUDED.impressions,
                    ctr = EXCLUDED.ctr,
                    average_position = EXCLUDED.average_position,
                    previous_clicks = EXCLUDED.previous_clicks,
                    previous_impressions = EXCLUDED.previous_impressions,
                    previous_ctr = EXCLUDED.previous_ctr,
                    previous_average_position = EXCLUDED.previous_average_position,
                    evidence_state = EXCLUDED.evidence_state,
                    indexing_state = EXCLUDED.indexing_state,
                    monitoring_task_id = EXCLUDED.monitoring_task_id,
                    analytics_task_id = EXCLUDED.analytics_task_id,
                    created_at = CURRENT_TIMESTAMP
                RETURNING *
                """, (result, rowNumber) -> snapshot(result),
                articleId, route.publicationId(), route.language(), route.path(),
                periodStart, periodEnd, previousStart, previousEnd,
                current.clicks(), current.impressions(), current.ctr(), current.averagePosition(),
                previous.clicks(), previous.impressions(), previous.ctr(), previous.averagePosition(),
                current.impressions() > 0 ? "PRESENT" : "MISSING",
                current.impressions() > 0 ? "DISCOVERED" : "NO_DATA",
                monitoringTaskId, analyticsTaskId).getFirst();
    }

    List<SnapshotRow> snapshotsForTask(long articleId, long monitoringTaskId) {
        return jdbc.query("""
                SELECT * FROM article_performance_snapshots
                WHERE article_id = ? AND monitoring_task_id = ?
                ORDER BY language
                """, (result, rowNumber) -> snapshot(result), articleId, monitoringTaskId);
    }

    List<EditorialPerformanceDtos.Snapshot> latestSnapshots(long articleId) {
        return jdbc.query("""
                SELECT DISTINCT ON (language) *
                FROM article_performance_snapshots
                WHERE article_id = ?
                ORDER BY language, period_end DESC, id DESC
                """, (result, rowNumber) -> dto(snapshot(result)), articleId);
    }

    Optional<EditorialPerformanceDtos.Recommendation> latestRecommendation(long articleId) {
        return jdbc.query("""
                SELECT * FROM article_refresh_recommendations
                WHERE article_id = ?
                ORDER BY created_at DESC, id DESC
                LIMIT 1
                """, (result, rowNumber) -> recommendation(result), articleId).stream().findFirst();
    }

    SaveDecisionResult saveRecommendation(
            long articleId,
            long performanceTaskId,
            long recommendationTaskId,
            LocalDate periodEnd,
            EditorialPerformancePolicy.Decision decision) {
        List<Long> inserted = jdbc.query("""
                INSERT INTO article_refresh_recommendations (
                    article_id, performance_task_id, recommendation_task_id, period_end,
                    recommended, reason_codes, evidence
                ) VALUES (?, ?, ?, ?, ?, ?::jsonb, ?::jsonb)
                ON CONFLICT (article_id, performance_task_id) DO NOTHING
                RETURNING id
                """, (result, rowNumber) -> result.getLong("id"),
                articleId, performanceTaskId, recommendationTaskId, periodEnd,
                decision.recommended(), json(decision.reasonCodes()), json(decision.evidence()));
        long id = inserted.isEmpty()
                ? jdbc.queryForObject("""
                        SELECT id FROM article_refresh_recommendations
                        WHERE article_id = ? AND performance_task_id = ?
                        """, Long.class, articleId, performanceTaskId)
                : inserted.getFirst();
        return new SaveDecisionResult(id, !inserted.isEmpty());
    }

    EditorialArticleState articleState(long articleId) {
        return EditorialArticleState.valueOf(jdbc.queryForObject(
                "SELECT lifecycle_state FROM articles WHERE id = ?",
                String.class, articleId));
    }

    private static Metrics metrics(ResultSet result) throws SQLException {
        return new Metrics(
                result.getDouble("clicks"),
                result.getDouble("impressions"),
                result.getDouble("ctr"),
                result.getDouble("average_position"));
    }

    private static SnapshotRow snapshot(ResultSet result) throws SQLException {
        return new SnapshotRow(
                result.getLong("id"),
                result.getLong("article_id"),
                result.getLong("article_publication_id"),
                result.getString("language"),
                result.getString("published_path"),
                result.getObject("period_start", LocalDate.class),
                result.getObject("period_end", LocalDate.class),
                new Metrics(
                        result.getDouble("clicks"), result.getDouble("impressions"),
                        result.getDouble("ctr"), result.getDouble("average_position")),
                new Metrics(
                        result.getDouble("previous_clicks"), result.getDouble("previous_impressions"),
                        result.getDouble("previous_ctr"), result.getDouble("previous_average_position")),
                result.getString("evidence_state"),
                result.getString("indexing_state"),
                result.getObject("created_at", OffsetDateTime.class).toInstant());
    }

    private EditorialPerformanceDtos.Recommendation recommendation(ResultSet result) throws SQLException {
        try {
            return new EditorialPerformanceDtos.Recommendation(
                    result.getLong("id"),
                    result.getBoolean("recommended"),
                    objectMapper.readValue(result.getString("reason_codes"), new TypeReference<>() {}),
                    objectMapper.readValue(result.getString("evidence"), new TypeReference<>() {}),
                    result.getObject("period_end", LocalDate.class),
                    result.getObject("created_at", OffsetDateTime.class).toInstant());
        } catch (JsonProcessingException error) {
            throw new SQLException("Invalid editorial performance evidence", error);
        }
    }

    private static EditorialPerformanceDtos.Snapshot dto(SnapshotRow row) {
        return new EditorialPerformanceDtos.Snapshot(
                row.id(), row.articleId(), row.publicationId(), row.language(), row.publishedPath(),
                row.periodStart(), row.periodEnd(), dto(row.current()), dto(row.previous()),
                row.evidenceState(), row.indexingState(), row.createdAt());
    }

    private static EditorialPerformanceDtos.Metrics dto(Metrics value) {
        return new EditorialPerformanceDtos.Metrics(
                value.clicks(), value.impressions(), value.ctr(), value.averagePosition());
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Editorial performance evidence could not be serialized", error);
        }
    }

    private static String path(String language, String slug) {
        String prefix = "EN".equals(language) ? "" : "/" + language.toLowerCase();
        return prefix + "/blog/" + slug;
    }

    record PublicationRoute(long publicationId, String language, String path) {}

    record Metrics(double clicks, double impressions, double ctr, double averagePosition) {}

    record SnapshotRow(
            long id,
            long articleId,
            long publicationId,
            String language,
            String publishedPath,
            LocalDate periodStart,
            LocalDate periodEnd,
            Metrics current,
            Metrics previous,
            String evidenceState,
            String indexingState,
            java.time.Instant createdAt) {}

    record SaveDecisionResult(long id, boolean created) {}
}
