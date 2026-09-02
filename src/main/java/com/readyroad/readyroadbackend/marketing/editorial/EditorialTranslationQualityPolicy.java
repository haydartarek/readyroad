package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.content.ContentValidationException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class EditorialTranslationQualityPolicy {

    private final MarketingProperties properties;

    ValidatedTranslation validate(
            EditorialTranslationClient.AdaptRequest request,
            EditorialTranslationClient.AdaptedContent generated) {

        if (request == null || generated == null) {
            throw invalid("Translation request and generated result are required");
        }

        if (request.sourceLocale() == request.targetLocale()) {
            throw invalid("Translation target language must differ from the canonical language");
        }

        if (!request.sourceLocale().name().equals(normalize(generated.sourceLanguage()))) {
            throw invalid("Translation source language does not match the canonical article language");
        }

        if (!request.targetLocale().name().equals(normalize(generated.targetLanguage()))) {
            throw invalid("Translation output language does not match the requested target language");
        }

        if (generated.sourceVersionId() != request.sourceVersionId()) {
            throw invalid("Translation output does not reference the current canonical source version");
        }

        String title = required(generated.title(), "title");
        String slug = required(generated.slug(), "slug");
        String summary = required(generated.summary(), "summary");
        String body = required(generated.body(), "body");
        String focusKeyword = required(generated.focusKeyword(), "focusKeyword");
        String metaTitle = required(generated.metaTitle(), "metaTitle");
        String metaDescription = required(generated.metaDescription(), "metaDescription");
        String cta = required(generated.cta(), "cta");

        within(title, properties.getContent().getMaxTitleCharacters(), "title");
        within(summary, properties.getContent().getMaxSummaryCharacters(), "summary");
        within(focusKeyword, 120, "focusKeyword");
        within(cta, properties.getContent().getMaxCtaCharacters(), "cta");

        if (slug.length() > 255) {
            throw invalid("Translated article slug must not exceed 255 characters");
        }

        if (slug.chars().anyMatch(character ->
                Character.isWhitespace(character)
                        || character == '/'
                        || character == '\\'
                        || character == '?'
                        || character == '#')) {
            throw invalid("Translated article slug contains unsafe characters");
        }

        if (body.length() < 300) {
            throw invalid("Translated article body is unexpectedly short");
        }

        if (body.trim().equals(request.sourceBody().trim())) {
            throw invalid("Translated article body is identical to the canonical source");
        }

        return new ValidatedTranslation(
                title,
                slug,
                summary,
                body,
                focusKeyword,
                metaTitle,
                metaDescription,
                cta,
                generated.model(),
                generated.inputTokens(),
                generated.outputTokens(),
                generated.requestOutcome());
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw invalid("Translated article " + field + " is required");
        }
        return value.trim();
    }

    private static void within(String value, int maximum, String field) {
        if (value.length() > maximum) {
            throw invalid("Translated article " + field + " exceeds its configured limit");
        }
    }

    private static ContentValidationException invalid(String message) {
        return new ContentValidationException("ARTICLE_TRANSLATION_INVALID", message);
    }

    record ValidatedTranslation(
            String title,
            String slug,
            String summary,
            String body,
            String focusKeyword,
            String metaTitle,
            String metaDescription,
            String cta,
            String model,
            long inputTokens,
            long outputTokens,
            String requestOutcome) {}
}
