package com.readyroad.readyroadbackend.marketing.youtube;

import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class YouTubeSyncService {

    private final YouTubeReadClient client;
    private final YouTubeStore store;
    private final YouTubeContentPackageService contentPackageService;
    private final YouTubeScheduleActivator scheduleActivator;
    private final MarketingAuditService auditService;

    @Transactional
    public YouTubeModels.SyncResult synchronize(Long taskId, String correlationId) {
        YouTubeModels.Snapshot snapshot = client.fetchChannelSnapshot();
        store.saveChannel(snapshot.channel());
        int inserted = 0;
        int packages = 0;
        int drafts = 0;
        for (YouTubeModels.Video video : snapshot.videos()) {
            boolean isNew = store.upsertVideo(video, sourceHash(video), taskId);
            if (isNew) {
                inserted++;
                var result = contentPackageService.createFor(video, taskId);
                packages += result.packages();
                drafts += result.drafts();
                auditService.recordEntityEvent(
                        "YOUTUBE_VIDEO_DISCOVERED", "YOUTUBE_WORKER", "YOUTUBE_VIDEO",
                        video.videoId(), taskId, correlationId);
            }
        }
        Instant completedAt = Instant.now();
        scheduleActivator.activateAfterSuccessfulSync(completedAt);
        auditService.recordEntityEvent(
                "YOUTUBE_CHANNEL_SYNC_COMPLETED", "YOUTUBE_WORKER", "YOUTUBE_CHANNEL",
                snapshot.channel().id(), taskId, correlationId);
        return new YouTubeModels.SyncResult(
                snapshot.videos().size(), inserted, snapshot.videos().size() - inserted,
                packages, drafts, completedAt);
    }

    static String sourceHash(YouTubeModels.Video video) {
        String source = String.join("\n",
                video.videoId(), video.title(), video.description(), video.publishedAt().toString(),
                String.valueOf(video.viewCount()), String.valueOf(video.likeCount()),
                String.valueOf(video.commentCount()));
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(source.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("SHA-256 is unavailable", error);
        }
    }
}
