package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.content.ContentGenerationClient;
import com.readyroad.readyroadbackend.marketing.content.ContentLocale;
import com.readyroad.readyroadbackend.marketing.content.ContentValidationException;
import com.readyroad.readyroadbackend.marketing.content.VerifiedContentSource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class EditorialDraftQualityPolicy {

    private static final Pattern WORD = Pattern.compile("[\\p{L}\\p{N}]+", Pattern.UNICODE_CHARACTER_CLASS);
    private static final List<String> FORBIDDEN_CLAIMS = List.of(
            "guaranteed success", "official exam center", "government platform", "100% success",
            "garantie de réussite", "centre d'examen officiel", "plateforme gouvernementale",
            "gegarandeerd slagen", "officieel examencentrum", "overheidsplatform",
            "نضمن النجاح", "نجاح مضمون", "مركز امتحانات رسمي", "منصة حكومية", "جهة حكومية");

    private final MarketingProperties properties;

    ValidatedDraft validate(
            ContentLocale locale,
            VerifiedContentSource source,
            ContentGenerationClient.GeneratedContent generated,
            boolean pillar) {
        require(generated.language(), "language");
        require(generated.sourceReference(), "sourceReference");
        require(generated.title(), "title");
        require(generated.summary(), "summary");
        require(generated.body(), "body");
        require(generated.cta(), "cta");
        if (!locale.name().equals(generated.language())) {
            throw invalid("Generated article language does not match the approved brief");
        }
        if (!source.sourceReference().equals(generated.sourceReference())) {
            throw invalid("Generated article source reference does not match the verified evidence package");
        }
        within(generated.title(), properties.getContent().getMaxTitleCharacters(), "title");
        within(generated.summary(), properties.getContent().getMaxSummaryCharacters(), "summary");
        within(generated.cta(), properties.getContent().getMaxCtaCharacters(), "cta");

        int words = wordCount(generated.body());
        int minimum = pillar
                ? properties.getContent().getMinPillarArticleWords()
                : properties.getContent().getMinArticleWords();
        if (words < minimum || words > properties.getContent().getMaxArticleWords()) {
            throw new ContentValidationException(
                    "ARTICLE_WORD_COUNT_INVALID",
                    "Generated article word count must be between " + minimum + " and "
                            + properties.getContent().getMaxArticleWords());
        }

        String publicText = String.join(
                "\n", generated.title(), generated.summary(), generated.body(), generated.cta());
        if (publicText.matches("(?s).*[\\p{So}\\x{FE0F}].*")) {
            throw new ContentValidationException(
                    "FORBIDDEN_CONTENT", "Generated article contains an emoji or unsupported symbol");
        }
        String normalized = normalize(publicText);
        if (FORBIDDEN_CLAIMS.stream().anyMatch(normalized::contains)) {
            throw new ContentValidationException(
                    "FORBIDDEN_CONTENT", "Generated article contains a forbidden claim");
        }
        return new ValidatedDraft(
                generated.title().trim(), generated.summary().trim(), generated.body().trim(),
                generated.cta().trim(), sha256(normalized), words, generated.model(),
                generated.inputTokens(), generated.outputTokens(), generated.requestOutcome());
    }

    private static int wordCount(String value) {
        int count = 0;
        var matcher = WORD.matcher(value == null ? "" : value);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private static void require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw invalid("Generated article " + field + " is required");
        }
    }

    private static void within(String value, int limit, String field) {
        if (value.length() > limit) {
            throw invalid("Generated article " + field + " exceeds its configured limit");
        }
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFKC)
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private static String sha256(String value) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 is unavailable", impossible);
        }
    }

    private static ContentValidationException invalid(String message) {
        return new ContentValidationException("MALFORMED_STRUCTURED_OUTPUT", message);
    }

    record ValidatedDraft(
            String title,
            String summary,
            String body,
            String cta,
            String fingerprint,
            int wordCount,
            String model,
            long inputTokens,
            long outputTokens,
            String requestOutcome) {}
}
