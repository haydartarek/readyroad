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
}
