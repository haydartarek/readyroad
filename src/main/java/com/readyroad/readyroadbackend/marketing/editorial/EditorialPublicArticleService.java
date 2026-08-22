package com.readyroad.readyroadbackend.marketing.editorial;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class EditorialPublicArticleService {

    private static final Set<String> LANGUAGES = Set.of("AR", "NL", "FR", "EN");

    private final EditorialPublicArticleStore store;

    @Transactional(readOnly = true)
    public List<EditorialPublicArticleDtos.Summary> summaries(String requestedLanguage) {
        return store.summaries(language(requestedLanguage));
    }

    @Transactional(readOnly = true)
    public Optional<EditorialPublicArticleDtos.Article> article(
            String requestedLanguage,
            String requestedSlug) {
        String language = language(requestedLanguage);
        String slug = slug(requestedSlug);
        Optional<EditorialPublicArticleStore.ArticleRow> row = store.exact(language, slug);
        if (row.isEmpty()) {
            List<Long> matchingArticles = store.articleIdsForSlug(slug);
            if (matchingArticles.size() != 1) {
                return Optional.empty();
            }
            row = store.byArticle(matchingArticles.getFirst(), language);
        }
        return row.map(value -> new EditorialPublicArticleDtos.Article(
                value.language(),
                value.slug(),
                value.title(),
                value.summary(),
                value.body(),
                EditorialArticleMetadata.valueOrFallback(value.metaTitle(), value.title()),
                EditorialArticleMetadata.valueOrFallback(value.metaDescription(), value.summary()),
                value.publishedAt(),
                store.alternateSlugs(value.articleId())));
    }

    private static String language(String value) {
        String normalized = value == null ? "EN" : value.trim().toUpperCase(Locale.ROOT);
        if (!LANGUAGES.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported article language: " + value);
        }
        return normalized;
    }

    private static String slug(String value) {
        String normalized = value == null ? "" : value.trim();
        if (!EditorialArticlePublicationService.isRouteSlugValid(normalized)) {
            throw new IllegalArgumentException("Invalid article slug");
        }
        return normalized;
    }
}
