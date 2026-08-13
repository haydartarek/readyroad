package com.readyroad.readyroadbackend.marketing.youtube;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfficialYouTubeReadClient implements YouTubeReadClient {

    private static final String API_BASE = "https://www.googleapis.com/youtube/v3";
    private static final int API_PAGE_SIZE = 50;
    private static final int MAX_PLAYLIST_PAGES = 20;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final ObjectMapper objectMapper;
    private final MarketingProperties properties;
    private final HttpClient httpClient;

    @Autowired
    public OfficialYouTubeReadClient(ObjectMapper objectMapper, MarketingProperties properties) {
        this(objectMapper, properties, HttpClient.newBuilder().connectTimeout(TIMEOUT).build());
    }

    OfficialYouTubeReadClient(
            ObjectMapper objectMapper,
            MarketingProperties properties,
            HttpClient httpClient) {
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.httpClient = httpClient;
    }

    @Override
    public YouTubeModels.Snapshot fetchChannelSnapshot() {
        ensureConfigured();
        YouTubeModels.Channel channel = channel();
        List<String> videoIds = uploadVideoIds(channel.uploadsPlaylistId());
        Map<String, YouTubeModels.Video> details = videoDetails(videoIds);
        List<YouTubeModels.Video> videos = new ArrayList<>();
        for (int position = 0; position < videoIds.size(); position++) {
            YouTubeModels.Video video = details.get(videoIds.get(position));
            if (video != null) {
                videos.add(withPosition(video, position));
            }
        }
        return new YouTubeModels.Snapshot(channel, List.copyOf(videos));
    }

    private YouTubeModels.Channel channel() {
        JsonNode response = get("channels", Map.of(
                "part", "snippet,contentDetails,statistics",
                "forHandle", properties.getYoutube().getChannelHandle()));
        JsonNode item = response.path("items").path(0);
        String id = text(item, "id");
        if (id.isBlank() || !id.equals(properties.getYoutube().getChannelId())) {
            throw failure("YOUTUBE_CHANNEL_MISMATCH", "Configured YouTube channel was not found");
        }
        JsonNode snippet = item.path("snippet");
        JsonNode statistics = item.path("statistics");
        String uploads = text(item.path("contentDetails").path("relatedPlaylists"), "uploads");
        if (uploads.isBlank()) {
            throw failure("YOUTUBE_UPLOADS_PLAYLIST_MISSING", "YouTube uploads playlist is unavailable");
        }
        return new YouTubeModels.Channel(
                id,
                properties.getYoutube().getChannelHandle(),
                text(snippet, "title"),
                text(snippet, "description"),
                thumbnail(snippet.path("thumbnails")),
                uploads,
                properties.getYoutube().getChannelUrl(),
                number(statistics, "viewCount"),
                number(statistics, "subscriberCount"),
                number(statistics, "videoCount"));
    }

    private List<String> uploadVideoIds(String playlistId) {
        List<String> ids = new ArrayList<>();
        String pageToken = "";
        for (int page = 0; page < MAX_PLAYLIST_PAGES; page++) {
            Map<String, String> parameters = new LinkedHashMap<>();
            parameters.put("part", "contentDetails,status");
            parameters.put("playlistId", playlistId);
            parameters.put("maxResults", String.valueOf(API_PAGE_SIZE));
            if (!pageToken.isBlank()) {
                parameters.put("pageToken", pageToken);
            }
            JsonNode response = get("playlistItems", parameters);
            response.path("items").forEach(item -> {
                if ("public".equals(text(item.path("status"), "privacyStatus"))) {
                    String videoId = text(item.path("contentDetails"), "videoId");
                    if (!videoId.isBlank()) {
                        ids.add(videoId);
                    }
                }
            });
            pageToken = text(response, "nextPageToken");
            if (pageToken.isBlank()) {
                return List.copyOf(ids);
            }
        }
        throw failure("YOUTUBE_PLAYLIST_PAGE_LIMIT", "YouTube uploads playlist exceeded the safe page limit");
    }

    private Map<String, YouTubeModels.Video> videoDetails(List<String> ids) {
        Map<String, YouTubeModels.Video> videos = new LinkedHashMap<>();
        for (int offset = 0; offset < ids.size(); offset += API_PAGE_SIZE) {
            List<String> batch = ids.subList(offset, Math.min(offset + API_PAGE_SIZE, ids.size()));
            JsonNode response = get("videos", Map.of(
                    "part", "snippet,contentDetails,statistics,status",
                    "id", String.join(",", batch),
                    "maxResults", String.valueOf(API_PAGE_SIZE)));
            response.path("items").forEach(item -> {
                YouTubeModels.Video video = mapVideo(item);
                if (video != null) {
                    videos.put(video.videoId(), video);
                }
            });
        }
        return videos;
    }

    private YouTubeModels.Video mapVideo(JsonNode item) {
        if (!"public".equals(text(item.path("status"), "privacyStatus"))) {
            return null;
        }
        String videoId = text(item, "id");
        JsonNode snippet = item.path("snippet");
        String title = text(snippet, "title");
        String thumbnail = thumbnail(snippet.path("thumbnails"));
        Instant publishedAt = instant(text(snippet, "publishedAt"));
        if (videoId.isBlank() || title.isBlank() || thumbnail.isBlank() || publishedAt == null) {
            return null;
        }
        JsonNode statistics = item.path("statistics");
        return new YouTubeModels.Video(
                videoId,
                text(snippet, "channelId"),
                text(snippet, "channelTitle"),
                title,
                text(snippet, "description"),
                publishedAt,
                thumbnail,
                "https://www.youtube.com/watch?v=" + videoId,
                "https://www.youtube-nocookie.com/embed/" + videoId,
                0,
                normalizeLanguage(text(snippet, "defaultLanguage"), title),
                text(snippet, "categoryId"),
                durationSeconds(text(item.path("contentDetails"), "duration")),
                number(statistics, "viewCount"),
                number(statistics, "likeCount"),
                number(statistics, "commentCount"));
    }

    private JsonNode get(String resource, Map<String, String> parameters) {
        HttpResponse<String> response;
        try {
            StringBuilder url = new StringBuilder(API_BASE).append('/').append(resource).append('?');
            Map<String, String> all = new LinkedHashMap<>(parameters);
            all.put("key", properties.getYoutube().getApiKey().trim());
            boolean first = true;
            for (Map.Entry<String, String> entry : all.entrySet()) {
                if (!first) {
                    url.append('&');
                }
                first = false;
                url.append(encode(entry.getKey())).append('=').append(encode(entry.getValue()));
            }
            HttpRequest request = HttpRequest.newBuilder(URI.create(url.toString()))
                    .timeout(TIMEOUT)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException error) {
            throw transportFailure(error);
        } catch (IOException error) {
            throw transportFailure(error);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw failure("YOUTUBE_SOURCE_INTERRUPTED", "YouTube request was interrupted");
        }

        if (response.statusCode() >= 400) {
            String code = switch (response.statusCode()) {
                case 429 -> "HTTP_429";
                case 502, 503, 504 -> "EXTERNAL_API_TEMPORARY_OUTAGE";
                default -> "YOUTUBE_SOURCE_HTTP_" + response.statusCode();
            };
            throw failure(code, "YouTube read request failed");
        }
        try {
            return objectMapper.readTree(response.body());
        } catch (JsonProcessingException error) {
            throw failure("YOUTUBE_SOURCE_INVALID_RESPONSE", "YouTube returned an invalid response");
        }
    }

    static MarketingTaskExecutionException transportFailure(IOException error) {
        return error instanceof HttpTimeoutException
                ? failure("TIMEOUT", "YouTube read request timed out")
                : failure("NETWORK_INTERRUPTION", "YouTube read request was interrupted by the network");
    }

    private void ensureConfigured() {
        if (properties.getYoutube().getApiKey() == null
                || properties.getYoutube().getApiKey().isBlank()) {
            throw failure("YOUTUBE_API_KEY_NOT_CONFIGURED", "YouTube Data API key is not configured");
        }
    }

    private static YouTubeModels.Video withPosition(YouTubeModels.Video video, int position) {
        return new YouTubeModels.Video(
                video.videoId(), video.channelId(), video.channelTitle(), video.title(), video.description(),
                video.publishedAt(), video.thumbnailUrl(), video.watchUrl(), video.embedUrl(), position,
                video.sourceLanguage(), video.categoryId(), video.durationSeconds(), video.viewCount(),
                video.likeCount(), video.commentCount());
    }

    static String normalizeLanguage(String value, String title) {
        String normalized = value == null ? "" : value.toLowerCase();
        if (normalized.startsWith("ar") || title.codePoints().anyMatch(code -> code >= 0x0600 && code <= 0x06ff)) {
            return "AR";
        }
        if (normalized.startsWith("nl")) return "NL";
        if (normalized.startsWith("fr")) return "FR";
        if (normalized.startsWith("en")) return "EN";
        return "UNKNOWN";
    }

    static Integer durationSeconds(String value) {
        try {
            return value == null || value.isBlank() ? null : Math.toIntExact(Duration.parse(value).toSeconds());
        } catch (ArithmeticException | java.time.format.DateTimeParseException error) {
            return null;
        }
    }

    private static String thumbnail(JsonNode thumbnails) {
        for (String quality : List.of("maxres", "standard", "high", "medium", "default")) {
            String value = text(thumbnails.path(quality), "url");
            if (value.startsWith("https://")) {
                return value;
            }
        }
        return "";
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isTextual() ? value.asText().trim() : "";
    }

    private static Long number(JsonNode node, String field) {
        String value = text(node, field);
        try {
            return value.isBlank() ? null : Long.parseLong(value);
        } catch (NumberFormatException error) {
            return null;
        }
    }

    private static Instant instant(String value) {
        try {
            return value.isBlank() ? null : Instant.parse(value);
        } catch (java.time.format.DateTimeParseException error) {
            return null;
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static MarketingTaskExecutionException failure(String code, String message) {
        return new MarketingTaskExecutionException(code, message);
    }
}
