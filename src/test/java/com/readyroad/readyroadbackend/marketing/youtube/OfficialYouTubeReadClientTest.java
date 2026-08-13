package com.readyroad.readyroadbackend.marketing.youtube;

import static org.assertj.core.api.Assertions.assertThat;

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
}
