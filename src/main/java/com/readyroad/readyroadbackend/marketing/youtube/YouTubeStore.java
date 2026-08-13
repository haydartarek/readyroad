package com.readyroad.readyroadbackend.marketing.youtube;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class YouTubeStore {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveChannel(YouTubeModels.Channel channel) {
        String value = json(Map.of(
                "id", channel.id(),
                "handle", channel.handle(),
                "title", channel.title(),
                "description", channel.description(),
                "thumbnailUrl", channel.thumbnailUrl(),
                "uploadsPlaylistId", channel.uploadsPlaylistId(),
                "url", channel.url(),
                "viewCount", value(channel.viewCount()),
                "subscriberCount", value(channel.subscriberCount()),
                "videoCount", value(channel.videoCount())));
        jdbc.update("""
                INSERT INTO agent_settings (
                    agent_type, setting_key, setting_value, updated_by, created_at, updated_at)
                VALUES ('YOUTUBE', 'youtube.channel.metadata', ?::jsonb, 'YOUTUBE_WORKER', now(), now())
                ON CONFLICT (agent_type, setting_key) DO UPDATE SET
                    setting_value = EXCLUDED.setting_value,
                    updated_by = EXCLUDED.updated_by,
                    updated_at = now()
                """, value);
    }

    @Transactional
    public boolean upsertVideo(YouTubeModels.Video video, String sourceHash, Long taskId) {
        List<Long> inserted = jdbc.queryForList("""
                INSERT INTO youtube_videos (
                    video_id, channel_id, channel_title, title, description, published_at,
                    thumbnail_url, watch_url, embed_url, source_language, category_id,
                    duration_seconds, view_count, like_count, comment_count, source_metadata,
                    source_hash, first_seen_at, last_synced_at, task_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?, now(), now(), ?)
                ON CONFLICT (video_id) DO NOTHING
                RETURNING id
                """, Long.class,
                video.videoId(), video.channelId(), video.channelTitle(), video.title(), video.description(),
                Timestamp.from(video.publishedAt()), video.thumbnailUrl(), video.watchUrl(), video.embedUrl(),
                video.sourceLanguage(), blankToNull(video.categoryId()), video.durationSeconds(),
                video.viewCount(), video.likeCount(), video.commentCount(),
                json(Map.of("position", video.position())), sourceHash, taskId);
        if (!inserted.isEmpty()) {
            return true;
        }
        jdbc.update("""
                UPDATE youtube_videos SET
                    channel_id = ?, channel_title = ?, title = ?, description = ?, published_at = ?,
                    thumbnail_url = ?, watch_url = ?, embed_url = ?, source_language = ?, category_id = ?,
                    duration_seconds = ?, view_count = ?, like_count = ?, comment_count = ?,
                    source_metadata = ?::jsonb, source_hash = ?, last_synced_at = now(), task_id = ?
                WHERE video_id = ?
                """, video.channelId(), video.channelTitle(), video.title(), video.description(),
                Timestamp.from(video.publishedAt()), video.thumbnailUrl(), video.watchUrl(), video.embedUrl(),
                video.sourceLanguage(), blankToNull(video.categoryId()), video.durationSeconds(),
                video.viewCount(), video.likeCount(), video.commentCount(),
                json(Map.of("position", video.position())), sourceHash, taskId, video.videoId());
        return false;
    }

    @Transactional(readOnly = true)
    public YouTubeModels.VideoPage page(int offset, int requestedLimit) {
        int limit = Math.max(1, Math.min(requestedLimit, 50));
        int safeOffset = Math.max(0, offset);
        List<YouTubeModels.Video> videos = jdbc.query("""
                SELECT video_id, channel_id, channel_title, title, description, published_at,
                       thumbnail_url, watch_url, embed_url, source_language, category_id,
                       duration_seconds, view_count, like_count, comment_count,
                       COALESCE((source_metadata->>'position')::int, 0) AS position
                FROM youtube_videos
                ORDER BY published_at DESC, id DESC
                LIMIT ? OFFSET ?
                """, (row, index) -> new YouTubeModels.Video(
                row.getString("video_id"), row.getString("channel_id"), row.getString("channel_title"),
                row.getString("title"), row.getString("description"),
                row.getTimestamp("published_at").toInstant(), row.getString("thumbnail_url"),
                row.getString("watch_url"), row.getString("embed_url"), row.getInt("position"),
                row.getString("source_language"), row.getString("category_id"),
                nullableInteger(row, "duration_seconds"), nullableLong(row, "view_count"),
                nullableLong(row, "like_count"), nullableLong(row, "comment_count")), limit, safeOffset);
        int total = videoCount();
        String next = safeOffset + videos.size() < total ? String.valueOf(safeOffset + videos.size()) : null;
        return new YouTubeModels.VideoPage(channel(), videos, next, false);
    }

    @Transactional(readOnly = true)
    public YouTubeModels.Channel channel() {
        List<JsonNode> values = jdbc.query("""
                SELECT setting_value FROM agent_settings
                WHERE agent_type = 'YOUTUBE' AND setting_key = 'youtube.channel.metadata'
                """, (row, index) -> parse(row.getString(1)));
        if (values.isEmpty()) {
            return null;
        }
        JsonNode value = values.getFirst();
        return new YouTubeModels.Channel(
                value.path("id").asText(), value.path("handle").asText(), value.path("title").asText(),
                value.path("description").asText(), value.path("thumbnailUrl").asText(),
                value.path("uploadsPlaylistId").asText(), value.path("url").asText(),
                nullable(value, "viewCount"), nullable(value, "subscriberCount"), nullable(value, "videoCount"));
    }

    @Transactional(readOnly = true)
    public int videoCount() {
        return jdbc.queryForObject("SELECT count(*) FROM youtube_videos", Integer.class);
    }

    @Transactional(readOnly = true)
    public int contentPackageCount() {
        return jdbc.queryForObject(
                "SELECT count(*) FROM content_items WHERE item_type = 'YOUTUBE_CONTENT_PACKAGE'",
                Integer.class);
    }

    @Transactional(readOnly = true)
    public int socialDraftCount() {
        return jdbc.queryForObject(
                "SELECT count(*) FROM content_items WHERE item_type = 'YOUTUBE_SOCIAL_DRAFT'",
                Integer.class);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> latestSyncTask() {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT id, status, attempts, created_at, updated_at, error_code
                FROM agent_tasks
                WHERE agent_type = 'YOUTUBE' AND task_type = 'YOUTUBE_CHANNEL_SYNC'
                ORDER BY created_at DESC LIMIT 1
                """);
        return rows.isEmpty()
                ? Map.of()
                : java.util.Collections.unmodifiableMap(new LinkedHashMap<>(rows.getFirst()));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> recentVideos(int limit) {
        return jdbc.queryForList("""
                SELECT video_id, title, source_language, published_at, view_count, like_count,
                       comment_count, last_synced_at
                FROM youtube_videos
                ORDER BY published_at DESC, id DESC LIMIT ?
                """, Math.max(1, Math.min(limit, 100)));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> bestVideos(int limit) {
        return jdbc.queryForList("""
                SELECT video_id, title, view_count, like_count, comment_count, published_at
                FROM youtube_videos
                ORDER BY view_count DESC NULLS LAST, published_at DESC LIMIT ?
                """, Math.max(1, Math.min(limit, 50)));
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Unable to serialize YouTube data", error);
        }
    }

    private JsonNode parse(String value) {
        try {
            return objectMapper.readTree(value);
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Stored YouTube channel metadata is invalid", error);
        }
    }

    private static Object value(Long value) {
        return value == null ? 0L : value;
    }

    private static Long nullable(JsonNode value, String field) {
        return value.path(field).isNumber() ? value.path(field).asLong() : null;
    }

    private static Integer nullableInteger(java.sql.ResultSet row, String field) throws java.sql.SQLException {
        int value = row.getInt(field);
        return row.wasNull() ? null : value;
    }

    private static Long nullableLong(java.sql.ResultSet row, String field) throws java.sql.SQLException {
        long value = row.getLong(field);
        return row.wasNull() ? null : value;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
