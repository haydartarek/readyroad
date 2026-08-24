package com.readyroad.readyroadbackend.marketing.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "readyroad.marketing")
@Getter
@Setter
public class MarketingProperties {

    private boolean enabled;
    private Worker worker = new Worker();
    private Analytics analytics = new Analytics();
    private YouTube youtube = new YouTube();
    private Content content = new Content();
    private LocalSeo localSeo = new LocalSeo();

    @Getter
    @Setter
    public static class Worker {
        private long pollIntervalMs = 5_000;
        private int batchSize = 10;
        private Duration lockTtl = Duration.ofMinutes(10);
    }

    @Getter
    @Setter
    public static class Analytics {
        private String serviceAccountFile;
        private String ga4AccountId = "403159538";
        private String ga4PropertyId = "548176182";
        private String searchConsoleSiteUrl = "sc-domain:readyroad.be";
    }

    @Getter
    @Setter
    public static class YouTube {
        private String apiKey;
        private String channelHandle = "@RijBewijsBe";
        private String channelId = "UCs_IDQXCz6zADuHIdfS2C2w";
        private String channelUrl = "https://www.youtube.com/@RijBewijsBe/featured";
        private int monitoringIntervalHours = 24;
        private int pageSize = 13;
    }

    @Getter
    @Setter
    public static class Content {
        private String apiKey;
        private String primaryModel = "gpt-5.6-terra";
        private String reviewModel = "gpt-5.6-sol";
        private String reasoningEffort = "medium";
        private long maxOutputTokens = 1_200;
        private long maxArticleOutputTokens = 7_000;
        private int maxSourceCharacters = 6_000;
        private int maxTitleCharacters = 140;
        private int maxSummaryCharacters = 360;
        private int maxBodyCharacters = 1_600;
        private int maxCtaCharacters = 180;
        private int minArticleWords = 600;
        private int minPillarArticleWords = 900;
        private int maxArticleWords = 1_600;
    }

    @Getter
    @Setter
    public static class LocalSeo {
        private boolean importEnabled;
        private long maxUploadBytes = 5L * 1024L * 1024L;
        private String candidateDomain = "https://rijvia.be";
    }
}
