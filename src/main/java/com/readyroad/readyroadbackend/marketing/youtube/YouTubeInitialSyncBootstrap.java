package com.readyroad.readyroadbackend.marketing.youtube;

import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "readyroad.marketing.enabled", havingValue = "true")
@RequiredArgsConstructor
public class YouTubeInitialSyncBootstrap implements ApplicationRunner {

    private static final ZoneId OPERATIONS_ZONE = ZoneId.of("Europe/Brussels");

    private final MarketingProperties properties;
    private final YouTubeStore store;
    private final YouTubeAdminService adminService;

    @Override
    public void run(ApplicationArguments args) {
        String apiKey = properties.getYoutube().getApiKey();
        if (apiKey != null && !apiKey.isBlank() && store.videoCount() == 0) {
            adminService.requestSync(
                    "youtube-initial-sync-" + LocalDate.now(OPERATIONS_ZONE), "YOUTUBE_BOOTSTRAP");
        }
    }
}
