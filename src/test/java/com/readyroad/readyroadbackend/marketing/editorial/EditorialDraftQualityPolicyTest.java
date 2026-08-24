package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.content.ContentGenerationClient;
import com.readyroad.readyroadbackend.marketing.content.ContentLocale;
import com.readyroad.readyroadbackend.marketing.content.ContentSourceType;
import com.readyroad.readyroadbackend.marketing.content.ContentValidationException;
import com.readyroad.readyroadbackend.marketing.content.VerifiedContentSource;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EditorialDraftQualityPolicyTest {

    private final EditorialDraftQualityPolicy policy = new EditorialDraftQualityPolicy(new MarketingProperties());
    private final VerifiedContentSource source = new VerifiedContentSource(
            ContentSourceType.EDITORIAL_BRIEF,
            "1",
            "ARTICLE_BRIEF:1:verified",
            "verified",
            Map.of(ContentLocale.EN, new VerifiedContentSource.LocalizedFacts("Brief", "Verified fact")),
            null);

    @Test
    void enforcesNormalAndPillarArticleWordBoundaries() {
        assertThatThrownBy(() -> policy.validate(
                ContentLocale.EN, source, generated(599, "Educational"), false))
                .isInstanceOf(ContentValidationException.class)
                .extracting(error -> ((ContentValidationException) error).errorCode())
                .isEqualTo("ARTICLE_WORD_COUNT_INVALID");

        assertThat(policy.validate(ContentLocale.EN, source, generated(600, "Educational"), false).wordCount())
                .isEqualTo(600);

        assertThatThrownBy(() -> policy.validate(
                ContentLocale.EN, source, generated(899, "Educational"), true))
                .isInstanceOf(ContentValidationException.class);
        assertThat(policy.validate(ContentLocale.EN, source, generated(900, "Educational"), true).wordCount())
                .isEqualTo(900);
    }

    @Test
    void rejectsUnsupportedOfficialAndGovernmentClaims() {
        assertThatThrownBy(() -> policy.validate(
                ContentLocale.EN, source, generated(600, "RijVia is a government platform"), false))
                .isInstanceOf(ContentValidationException.class)
                .extracting(error -> ((ContentValidationException) error).errorCode())
                .isEqualTo("FORBIDDEN_CONTENT");
    }

    private ContentGenerationClient.GeneratedContent generated(int words, String title) {
        return new ContentGenerationClient.GeneratedContent(
                "EN",
                source.sourceReference(),
                title,
                "Verified summary",
                String.join(" ", Collections.nCopies(words, "word")),
                "Continue learning",
                "test-model",
                10,
                20,
                "SUCCEEDED");
    }
}
