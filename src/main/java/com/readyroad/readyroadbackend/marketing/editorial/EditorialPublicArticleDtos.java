package com.readyroad.readyroadbackend.marketing.editorial;

import java.time.Instant;
import java.util.Map;

final class EditorialPublicArticleDtos {

    private EditorialPublicArticleDtos() {}

    record Summary(
            String language,
            String slug,
            String title,
            String summary,
            Instant publishedAt) {}

    record Article(
            String language,
            String slug,
            String title,
            String summary,
            String body,
            Instant publishedAt,
            Map<String, String> alternateSlugs) {}
}
