package com.readyroad.readyroadbackend.marketing.youtube;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class YouTubeAdminService {

    public static final String AGENT_TYPE = "YOUTUBE";

    private final MarketingProperties properties;
    private final YouTubeStore store;
    private final TaskCreationService taskCreationService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public YouTubeDtos.Status status() {
        return new YouTubeDtos.Status(
                configured(), true, properties.getYoutube().getChannelHandle(),
                properties.getYoutube().getChannelId(), properties.getYoutube().getMonitoringIntervalHours(),
                store.videoCount(), store.contentPackageCount(), store.socialDraftCount(),
                store.latestSyncTask(), store.recentVideos(20), store.bestVideos(10));
    }

    @Transactional
    public MarketingTaskLifecycleResponse requestSync(String idempotencyKey, String actor) {
        if (!configured()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "YouTube Data API key is not configured");
        }
        var payload = objectMapper.createObjectNode().put("mode", "READ_ONLY_CHANNEL_MONITOR");
        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                AGENT_TYPE, YouTubeTaskHandler.CHANNEL_SYNC, payload, TaskPriority.NORMAL, null,
                actor, idempotencyKey, null, null, "YOUTUBE_CHANNEL",
                properties.getYoutube().getChannelId(), ApprovalMetadata.standingOwnerAuthorization()));
        return MarketingTaskLifecycleResponse.from(result.task());
    }

    private boolean configured() {
        return properties.getYoutube().getApiKey() != null && !properties.getYoutube().getApiKey().isBlank();
    }
}
