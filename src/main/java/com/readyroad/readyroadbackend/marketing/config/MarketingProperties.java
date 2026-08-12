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

    @Getter
    @Setter
    public static class Worker {
        private long pollIntervalMs = 5_000;
        private int batchSize = 10;
        private Duration lockTtl = Duration.ofMinutes(10);
    }
}
