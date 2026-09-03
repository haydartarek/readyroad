package com.readyroad.readyroadbackend.marketing.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.errors.OpenAIServiceException;
import com.readyroad.readyroadbackend.marketing.task.MarketingRetryPolicy;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OpenAIRequestFailureTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final MarketingRetryPolicy retry = new MarketingRetryPolicy();

    @Test
    void quotaExhaustionIsActionableAndDoesNotConsumeAutomaticRetries() {
        var failure = raw("insufficient_quota");
        assertThat(failure.errorCode()).isEqualTo("OPENAI_QUOTA_EXHAUSTED");
        assertThat(retry.isRetryable(failure.errorCode())).isFalse();
        assertThat(failure.getMessage()).doesNotContain("private-provider-detail");
    }

    @Test
    void temporaryRateLimitUsesTheExistingBoundedRetryPolicy() {
        var failure = raw("rate_limit_exceeded");
        assertThat(failure.errorCode()).isEqualTo("HTTP_429");
        assertThat(retry.isRetryable(failure.errorCode())).isTrue();
    }

    @Test
    void malformedErrorBodyDoesNotHideTheOriginalHttpFailure() {
        var failure = OpenAIRequestFailure.from(429,
                new ByteArrayInputStream("not-json".getBytes(StandardCharsets.UTF_8)), mapper);
        assertThat(failure.errorCode()).isEqualTo("HTTP_429");
    }

    @Test
    void sdkExceptionsUseTheSameQuotaClassificationWithoutLoggingProviderMessages() {
        var error = mock(OpenAIServiceException.class);
        when(error.statusCode()).thenReturn(429);
        when(error.code()).thenReturn(Optional.of("credit_balance_exhausted"));
        when(error.type()).thenReturn(Optional.of("insufficient_quota"));
        assertThat(OpenAIRequestFailure.from(error).errorCode()).isEqualTo("OPENAI_QUOTA_EXHAUSTED");
    }

    @Test
    void realCreditBalanceErrorTypeIsNonRetryableForRawResponsesToo() {
        var body = "{\"error\":{\"code\":\"credit_balance_exhausted\",\"type\":\"insufficient_quota\"}}";
        var failure = OpenAIRequestFailure.from(429,
                new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)), mapper);
        assertThat(failure.errorCode()).isEqualTo("OPENAI_QUOTA_EXHAUSTED");
        assertThat(retry.isRetryable(failure.errorCode())).isFalse();
    }

    @Test
    void authenticationFailureRemainsNonRetryable() {
        var failure = OpenAIRequestFailure.from(401, null, mapper);
        assertThat(failure.errorCode()).isEqualTo("INVALID_API_KEY");
        assertThat(retry.isRetryable(failure.errorCode())).isFalse();
    }

    private OpenAIContentGenerationException raw(String code) {
        var body = "{\"error\":{\"code\":\"" + code
                + "\",\"message\":\"private-provider-detail\"}}";
        return OpenAIRequestFailure.from(429,
                new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)), mapper);
    }
}
