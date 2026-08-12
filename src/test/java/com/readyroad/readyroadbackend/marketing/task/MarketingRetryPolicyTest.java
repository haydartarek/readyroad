package com.readyroad.readyroadbackend.marketing.task;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class MarketingRetryPolicyTest {

    private final MarketingRetryPolicy retryPolicy = new MarketingRetryPolicy();

    @Test
    void usesTheApprovedRetrySchedule() {
        assertThat(retryPolicy.delayAfterAttempt(1)).contains(Duration.ofMinutes(5));
        assertThat(retryPolicy.delayAfterAttempt(2)).contains(Duration.ofMinutes(30));
        assertThat(retryPolicy.delayAfterAttempt(3)).contains(Duration.ofHours(2));
        assertThat(retryPolicy.delayAfterAttempt(4)).isEmpty();
    }

    @Test
    void classifiesOnlyTemporaryFailuresAsRetryable() {
        assertThat(retryPolicy.isRetryable("HTTP_429")).isTrue();
        assertThat(retryPolicy.isRetryable("HTTP_503")).isTrue();
        assertThat(retryPolicy.isRetryable("NETWORK_INTERRUPTION")).isTrue();
        assertThat(retryPolicy.isRetryable("INVALID_CREDENTIALS")).isFalse();
        assertThat(retryPolicy.isRetryable("VALIDATION_ERROR")).isFalse();
        assertThat(retryPolicy.isRetryable("DUPLICATE_TASK")).isFalse();
    }
}
