package com.readyroad.readyroadbackend.marketing.youtube;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class YouTubeDtos {

    private YouTubeDtos() {}

    public record SyncRequest(@NotBlank String idempotencyKey) {}

    public record Status(
            boolean apiKeyConfigured,
            boolean readOnly,
            String channelHandle,
            String channelId,
            int monitoringIntervalHours,
            int videoCount,
            int contentPackageCount,
            int socialDraftCount,
            Map<String, Object> latestSync,
            List<Map<String, Object>> latestVideos,
            List<Map<String, Object>> bestVideos) {}

    public record Thumbnail(String url) {}

    public record PublicChannel(
            String id,
            String handle,
            String title,
            String description,
            Thumbnail thumbnail,
            String uploadsPlaylistId,
            String url) {
        static PublicChannel from(YouTubeModels.Channel source) {
            return new PublicChannel(
                    source.id(), source.handle(), source.title(), source.description(),
                    source.thumbnailUrl().isBlank() ? null : new Thumbnail(source.thumbnailUrl()),
                    source.uploadsPlaylistId(), source.url());
        }
    }

    public record PublicVideo(
            String videoId,
            String title,
            String description,
            Instant publishedAt,
            Thumbnail thumbnail,
            String channelTitle,
            int position,
            String watchUrl,
            String embedUrl) {
        static PublicVideo from(YouTubeModels.Video source) {
            return new PublicVideo(
                    source.videoId(), source.title(), source.description(), source.publishedAt(),
                    new Thumbnail(source.thumbnailUrl()), source.channelTitle(), source.position(),
                    source.watchUrl(), source.embedUrl());
        }
    }

    public record PublicPage(
            PublicChannel channel,
            List<PublicVideo> videos,
            String nextPageToken,
            boolean stale) {
        static PublicPage from(YouTubeModels.VideoPage source) {
            return new PublicPage(
                    PublicChannel.from(source.channel()),
                    source.videos().stream().map(PublicVideo::from).toList(),
                    source.nextPageToken(), source.stale());
        }
    }
}
