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
    private final EditorialArticleImageStore imageStore;

    @Transactional(readOnly = true)
    public List<EditorialPublicArticleDtos.Summary> summaries(String requestedLanguage) {
        String language = language(requestedLanguage);
        return store.summaries(language).stream().map(row -> summary(row, language)).toList();
    }

    @Transactional(readOnly = true)
    public List<EditorialPublicArticleDtos.Summary> related(
            String requestedLanguage,
            String requestedTargetPath,
            int limit) {
        if (limit < 1 || limit > 6) {
            throw new IllegalArgumentException("Related article limit must be between 1 and 6");
        }
        String language = language(requestedLanguage);
        return store.related(
                language,
                EditorialInternalLinkPolicy.cleanPath(requestedTargetPath),
                limit).stream().map(row -> summary(row, language)).toList();
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
                value.imageAssetId() == null
                        ? null
                        : imageStore.publicImage(value.imageAssetId(), language).orElse(null),
                EditorialArticleMetadata.internalLinks(value.metadata()),
                store.alternateSlugs(value.articleId())));
    }

    private EditorialPublicArticleDtos.Summary summary(
            EditorialPublicArticleStore.SummaryRow row,
            String language) {
        return new EditorialPublicArticleDtos.Summary(
                row.language(),
                row.slug(),
                row.title(),
                row.summary(),
                row.publishedAt(),
                row.imageAssetId() == null
                        ? null
                        : imageStore.publicImage(row.imageAssetId(), language).orElse(null),
                row.alternateSlugs());
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
