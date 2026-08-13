package com.readyroad.readyroadbackend.marketing.youtube;

import java.time.Instant;
import java.util.List;

public final class YouTubeModels {

    private YouTubeModels() {}

    public record Channel(
            String id,
            String handle,
            String title,
            String description,
            String thumbnailUrl,
            String uploadsPlaylistId,
            String url,
            Long viewCount,
            Long subscriberCount,
            Long videoCount) {}

    public record Video(
            String videoId,
            String channelId,
            String channelTitle,
            String title,
            String description,
            Instant publishedAt,
            String thumbnailUrl,
            String watchUrl,
            String embedUrl,
            int position,
            String sourceLanguage,
            String categoryId,
            Integer durationSeconds,
            Long viewCount,
            Long likeCount,
            Long commentCount) {}

    public record Snapshot(Channel channel, List<Video> videos) {}

    public record VideoPage(Channel channel, List<Video> videos, String nextPageToken, boolean stale) {}

    public record SyncResult(
            int received,
            int inserted,
            int updated,
            int contentPackages,
            int socialDrafts,
            Instant completedAt) {}
}
