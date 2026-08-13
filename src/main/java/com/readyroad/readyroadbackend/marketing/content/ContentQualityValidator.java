package com.readyroad.readyroadbackend.marketing.content;

import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentQualityValidator {

    private static final List<String> FORBIDDEN_CLAIMS = List.of(
            "guaranteed success", "guarantee success", "official exam center", "government platform",
            "100% success", "garantie de réussite", "centre d'examen officiel", "plateforme gouvernementale",
            "gegarandeerd slagen", "officieel examencentrum", "overheidsplatform",
            "نضمن النجاح", "نجاح مضمون", "مركز امتحانات رسمي", "منصة حكومية", "جهة حكومية");

    private final MarketingProperties properties;

    public ValidatedContent validate(
            ContentLocale locale,
            VerifiedContentSource source,
            ContentGenerationClient.GeneratedContent generated) {
        required(generated.language(), "language");
        required(generated.sourceReference(), "sourceReference");
        required(generated.title(), "title");
        required(generated.summary(), "summary");
        required(generated.body(), "body");
        required(generated.cta(), "cta");
        if (!locale.name().equals(generated.language())) {
            throw invalid("Generated language does not match the requested locale");
        }
        if (!source.sourceReference().equals(generated.sourceReference())) {
            throw invalid("Generated source reference does not match the verified source");
        }
        within(generated.title(), properties.getContent().getMaxTitleCharacters(), "title");
        within(generated.summary(), properties.getContent().getMaxSummaryCharacters(), "summary");
        within(generated.body(), properties.getContent().getMaxBodyCharacters(), "body");
        within(generated.cta(), properties.getContent().getMaxCtaCharacters(), "cta");
        String publicText = String.join("\n", generated.title(), generated.summary(), generated.body(), generated.cta());
        if (publicText.matches("(?s).*[\\p{So}\\x{FE0F}].*")) {
            throw new ContentValidationException("FORBIDDEN_CONTENT", "Generated content contains an emoji or symbol");
        }
        String normalized = publicText.toLowerCase(Locale.ROOT);
        if (FORBIDDEN_CLAIMS.stream().anyMatch(normalized::contains)) {
            throw new ContentValidationException("FORBIDDEN_CONTENT", "Generated content contains a forbidden claim");
        }
        String fingerprint = ContentHashing.sha256(ContentHashing.normalize(publicText));
        return new ValidatedContent(
                locale,
                generated.title().trim(),
                generated.summary().trim(),
                generated.body().trim(),
                generated.cta().trim(),
                fingerprint,
                generated.model(),
                generated.inputTokens(),
                generated.outputTokens(),
                generated.requestOutcome());
    }

    private static void required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw invalid("Generated " + field + " is required");
        }
    }

    private static void within(String value, int limit, String field) {
        if (value.length() > limit) {
            throw invalid("Generated " + field + " exceeds its configured limit");
        }
    }

    private static ContentValidationException invalid(String message) {
        return new ContentValidationException("MALFORMED_STRUCTURED_OUTPUT", message);
    }

    public record ValidatedContent(
            ContentLocale locale,
            String title,
            String summary,
            String body,
            String cta,
            String fingerprint,
            String model,
            long inputTokens,
            long outputTokens,
            String requestOutcome) {}
}
