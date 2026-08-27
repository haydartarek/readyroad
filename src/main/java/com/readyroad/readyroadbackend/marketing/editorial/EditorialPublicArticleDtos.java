package com.readyroad.readyroadbackend.marketing.editorial;

import java.time.Instant;
import java.util.List;
import java.util.Map;

final class EditorialPublicArticleDtos {

    private EditorialPublicArticleDtos() {}

    record Summary(
            String language,
            String slug,
            String title,
            String summary,
            Instant publishedAt,
            EditorialArticleImageDtos.PublicImage image,
            Map<String, String> alternateSlugs) {}

    record Article(
            String language,
            String slug,
            String title,
            String summary,
            String body,
            String metaTitle,
            String metaDescription,
            Instant publishedAt,
            EditorialArticleImageDtos.PublicImage image,
            List<EditorialInternalLinkDtos.Link> internalLinks,
            EditorialEditorDtos.Typography typography,
            Map<String, String> alternateSlugs) {}
}
