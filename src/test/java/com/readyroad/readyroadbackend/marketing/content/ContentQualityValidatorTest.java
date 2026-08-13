package com.readyroad.readyroadbackend.marketing.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContentQualityValidatorTest {

    private ContentQualityValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ContentQualityValidator(new MarketingProperties());
    }

    @Test
    void acceptsBoundedStructuredOutputForEverySupportedLocale() {
        for (ContentLocale locale : ContentLocale.SUPPORTED) {
            var result = validator.validate(locale, ContentTestFixtures.source(),
                    ContentTestFixtures.generated(locale));
            assertThat(result.locale()).isEqualTo(locale);
            assertThat(result.fingerprint()).hasSize(64);
        }
    }

    @Test
    void rejectsMismatchedSourceReferenceAndLanguage() {
        var wrongReference = new ContentGenerationClient.GeneratedContent(
                "AR", "ROAD_SIGN:B1", "Title", "Summary", "Body", "CTA", "model", 1, 1, "SUCCEEDED");
        assertThatThrownBy(() -> validator.validate(ContentLocale.AR, ContentTestFixtures.source(), wrongReference))
                .isInstanceOf(ContentValidationException.class)
                .hasMessageContaining("source reference");

        var wrongLanguage = new ContentGenerationClient.GeneratedContent(
                "EN", "ROAD_SIGN:A1", "Title", "Summary", "Body", "CTA", "model", 1, 1, "SUCCEEDED");
        assertThatThrownBy(() -> validator.validate(ContentLocale.AR, ContentTestFixtures.source(), wrongLanguage))
                .isInstanceOf(ContentValidationException.class)
                .hasMessageContaining("language");
    }

    @Test
    void rejectsForbiddenClaimsAndEmoji() {
        var claim = new ContentGenerationClient.GeneratedContent(
                "EN", "ROAD_SIGN:A1", "Guaranteed success", "Summary", "Body", "CTA", "model", 1, 1,
                "SUCCEEDED");
        assertThatThrownBy(() -> validator.validate(ContentLocale.EN, ContentTestFixtures.source(), claim))
                .isInstanceOf(ContentValidationException.class)
                .extracting(error -> ((ContentValidationException) error).errorCode())
                .isEqualTo("FORBIDDEN_CONTENT");

        var emoji = new ContentGenerationClient.GeneratedContent(
                "EN", "ROAD_SIGN:A1", "Title", "Summary", "Body ⚠", "CTA", "model", 1, 1, "SUCCEEDED");
        assertThatThrownBy(() -> validator.validate(ContentLocale.EN, ContentTestFixtures.source(), emoji))
                .isInstanceOf(ContentValidationException.class)
                .extracting(error -> ((ContentValidationException) error).errorCode())
                .isEqualTo("FORBIDDEN_CONTENT");
    }
}
