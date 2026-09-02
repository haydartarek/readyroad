package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.content.ContentLocale;
import com.readyroad.readyroadbackend.marketing.content.ContentValidationException;
import org.junit.jupiter.api.Test;

class EditorialTranslationQualityPolicyTest {

    private final EditorialTranslationQualityPolicy policy =
            new EditorialTranslationQualityPolicy(new MarketingProperties());

    @Test
    void acceptsACompleteLocalizedAdaptation() {
        var request = request(ContentLocale.AR, ContentLocale.NL, 41L);

        var validated = policy.validate(
                request,
                generated(
                        "AR",
                        "NL",
                        41L,
                        "belgisch-theorie-examen",
                        translatedBody()));

        assertThat(validated.title()).isEqualTo("Belgisch theorie-examen");
        assertThat(validated.slug()).isEqualTo("belgisch-theorie-examen");
        assertThat(validated.metaTitle()).isEqualTo("Belgisch theorie-examen | RijVia");
        assertThat(validated.requestOutcome()).isEqualTo("SUCCEEDED");
    }

    @Test
    void rejectsAnOutputForTheWrongTargetLanguage() {
        var request = request(ContentLocale.AR, ContentLocale.NL, 41L);

        assertThatThrownBy(() -> policy.validate(
                request,
                generated(
                        "AR",
                        "FR",
                        41L,
                        "examen-theorique",
                        translatedBody())))
                .isInstanceOf(ContentValidationException.class)
                .extracting(error -> ((ContentValidationException) error).errorCode())
                .isEqualTo("ARTICLE_TRANSLATION_INVALID");
    }

    @Test
    void rejectsAnOutputForTheWrongCanonicalVersion() {
        var request = request(ContentLocale.AR, ContentLocale.EN, 41L);

        assertThatThrownBy(() -> policy.validate(
                request,
                generated(
                        "AR",
                        "EN",
                        99L,
                        "belgian-theory-exam",
                        translatedBody())))
                .isInstanceOf(ContentValidationException.class)
                .extracting(error -> ((ContentValidationException) error).errorCode())
                .isEqualTo("ARTICLE_TRANSLATION_INVALID");
    }

    @Test
    void rejectsAnUnsafeLocalizedSlug() {
        var request = request(ContentLocale.AR, ContentLocale.FR, 41L);

        assertThatThrownBy(() -> policy.validate(
                request,
                generated(
                        "AR",
                        "FR",
                        41L,
                        "/fr/examen theorique",
                        translatedBody())))
                .isInstanceOf(ContentValidationException.class)
                .extracting(error -> ((ContentValidationException) error).errorCode())
                .isEqualTo("ARTICLE_TRANSLATION_INVALID");
    }

    @Test
    void rejectsAnUnchangedCanonicalBody() {
        var request = request(ContentLocale.AR, ContentLocale.EN, 41L);

        assertThatThrownBy(() -> policy.validate(
                request,
                generated(
                        "AR",
                        "EN",
                        41L,
                        "belgian-theory-exam",
                        request.sourceBody())))
                .isInstanceOf(ContentValidationException.class)
                .extracting(error -> ((ContentValidationException) error).errorCode())
                .isEqualTo("ARTICLE_TRANSLATION_INVALID");
    }

    private static EditorialTranslationClient.AdaptRequest request(
            ContentLocale source,
            ContentLocale target,
            long sourceVersionId) {

        return new EditorialTranslationClient.AdaptRequest(
                17L,
                sourceVersionId,
                source,
                target,
                "Canonical article title",
                "canonical-article-title",
                "Canonical article summary",
                canonicalBody(),
                "Belgian driving theory exam",
                "Canonical meta title",
                "Canonical meta description",
                "Continue learning");
    }

    private static EditorialTranslationClient.AdaptedContent generated(
            String sourceLanguage,
            String targetLanguage,
            long sourceVersionId,
            String slug,
            String body) {

        return new EditorialTranslationClient.AdaptedContent(
                sourceLanguage,
                targetLanguage,
                sourceVersionId,
                "Belgisch theorie-examen",
                slug,
                "Een duidelijke samenvatting voor Belgische kandidaten.",
                body,
                "Belgisch theorie-examen",
                "Belgisch theorie-examen | RijVia",
                "Bereid je voor op het Belgische theorie-examen met RijVia.",
                "Ga verder met leren",
                "test-translation-model",
                600,
                900,
                "SUCCEEDED");
    }

    private static String canonicalBody() {
        return "canonical educational content ".repeat(30);
    }

    private static String translatedBody() {
        return "gelokaliseerde educatieve inhoud voor belgische kandidaten ".repeat(20);
    }
}
