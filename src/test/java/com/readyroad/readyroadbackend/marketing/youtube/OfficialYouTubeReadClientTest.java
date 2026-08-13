package com.readyroad.readyroadbackend.marketing.youtube;

import static org.assertj.core.api.Assertions.assertThat;

import com.readyroad.readyroadbackend.marketing.task.MarketingRetryPolicy;
import java.net.ConnectException;
import java.net.http.HttpTimeoutException;
import org.junit.jupiter.api.Test;

class OfficialYouTubeReadClientTest {

    @Test
    void normalizesKnownLanguagesAndDetectsArabicTitles() {
        assertThat(OfficialYouTubeReadClient.normalizeLanguage("nl-BE", "Verkeersborden")).isEqualTo("NL");
        assertThat(OfficialYouTubeReadClient.normalizeLanguage("", "شرح علامات المرور")).isEqualTo("AR");
        assertThat(OfficialYouTubeReadClient.normalizeLanguage("de", "Theorie")).isEqualTo("UNKNOWN");
    }

    @Test
    void parsesIsoDurationsWithoutInventingInvalidValues() {
        assertThat(OfficialYouTubeReadClient.durationSeconds("PT2M5S")).isEqualTo(125);
        assertThat(OfficialYouTubeReadClient.durationSeconds("invalid")).isNull();
        assertThat(OfficialYouTubeReadClient.durationSeconds("")).isNull();
    }

    @Test
    void mapsTransientTransportFailuresToTheExistingRetryPolicyCodes() {
        var retryPolicy = new MarketingRetryPolicy();
        var timeout = OfficialYouTubeReadClient.transportFailure(new HttpTimeoutException("timeout"));
        var interruption = OfficialYouTubeReadClient.transportFailure(new ConnectException("connection reset"));

        assertThat(timeout.errorCode()).isEqualTo("TIMEOUT");
        assertThat(interruption.errorCode()).isEqualTo("NETWORK_INTERRUPTION");
        assertThat(retryPolicy.isRetryable(timeout.errorCode())).isTrue();
        assertThat(retryPolicy.isRetryable(interruption.errorCode())).isTrue();
    }
}
