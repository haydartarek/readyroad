package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class EditorialArticleVersionService {

    private static final Set<String> LANGUAGES = Set.of("AR", "NL", "FR", "EN");

    private final EditorialArticleVersionStore store;
    private final ObjectMapper objectMapper;

    @Transactional
    public EditorialArticleVersionDtos.Version append(
            EditorialArticleVersionDtos.AppendRequest request,
            String actor) {
        validate(request, actor);
        String language = language(request.language());
        JsonNode metadata = objectOrEmpty(request.metadata(), "metadata");
        JsonNode generationMetadata = objectOrEmpty(request.generationMetadata(), "generationMetadata");
        store.lockArticle(request.articleId());
        return store.append(request, language, metadata, generationMetadata, actor.trim());
    }

    @Transactional(readOnly = true)
    public List<EditorialArticleVersionDtos.Version> history(long articleId, String language) {
        requireArticleId(articleId);
        return store.history(articleId, language(language));
    }

    @Transactional(readOnly = true)
    public Optional<EditorialArticleVersionDtos.Version> current(long articleId, String language) {
        requireArticleId(articleId);
        return store.current(articleId, language(language));
    }

    private void validate(EditorialArticleVersionDtos.AppendRequest request, String actor) {
        if (request == null) {
            throw new IllegalArgumentException("Version request is required");
        }
        requireArticleId(request.articleId());
        requireText(request.title(), "title");
        requireText(request.body(), "body");
        requireText(request.status(), "status");
        requireText(actor, "actor");
        if (request.slug() != null && request.slug().trim().length() > 255) {
            throw new IllegalArgumentException("slug must not exceed 255 characters");
        }
    }

    private JsonNode objectOrEmpty(JsonNode value, String field) {
        JsonNode effective = value == null ? objectMapper.createObjectNode() : value;
        if (!effective.isObject()) {
            throw new IllegalArgumentException(field + " must be a JSON object");
        }
        return effective;
    }

    private static String language(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        if (!LANGUAGES.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported article language: " + value);
        }
        return normalized;
    }

    private static void requireArticleId(long articleId) {
        if (articleId <= 0) {
            throw new IllegalArgumentException("articleId must be positive");
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }
}
