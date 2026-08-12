package com.readyroad.readyroadbackend.marketing.task;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class MarketingRetryPolicy {

    private static final Set<String> RETRYABLE_CODES = Set.of(
            "TIMEOUT",
            "HTTP_429",
            "HTTP_502",
            "HTTP_503",
            "HTTP_504",
            "NETWORK_INTERRUPTION",
            "TEMPORARY_DATABASE_CONNECTION_FAILURE",
            "EXTERNAL_API_TEMPORARY_OUTAGE",
            "RATE_LIMIT");

    public Optional<Duration> delayAfterAttempt(int completedAttempt) {
        return switch (completedAttempt) {
            case 1 -> Optional.of(Duration.ofMinutes(5));
            case 2 -> Optional.of(Duration.ofMinutes(30));
            case 3 -> Optional.of(Duration.ofHours(2));
            default -> Optional.empty();
        };
    }

    public boolean isRetryable(String errorCode) {
        if (errorCode == null || errorCode.isBlank()) {
            return false;
        }
        return RETRYABLE_CODES.contains(errorCode.trim().toUpperCase(Locale.ROOT));
    }
}
