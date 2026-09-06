package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.content.ContentLocale;

interface EditorialTranslationClient {

    AdaptedContent adapt(AdaptRequest request);

    default AdaptedContent adaptKeyword(AdaptRequest request) {
        return adapt(request);
    }

    record AdaptRequest(
            long articleId,
            long sourceVersionId,
            ContentLocale sourceLocale,
            ContentLocale targetLocale,
            String sourceTitle,
            String sourceSlug,
            String sourceSummary,
            String sourceBody,
            String sourceFocusKeyword,
            String sourceMetaTitle,
            String sourceMetaDescription,
            String sourceCta) {}

    record AdaptedContent(
            String sourceLanguage,
            String targetLanguage,
            long sourceVersionId,
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
