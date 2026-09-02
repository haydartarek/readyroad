package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class EditorialArticleMetadata {

    static final String META_TITLE = "metaTitle";
    static final String META_DESCRIPTION = "metaDescription";
    static final String FOCUS_KEYWORD = "focusKeyword";
    static final String INTERNAL_LINKS = "internalLinks";
    static final String TYPOGRAPHY = "typography";

    private EditorialArticleMetadata() {}

    static ObjectNode withSeoMetadata(JsonNode existing, String metaTitle, String metaDescription) {
        ObjectNode metadata = existing != null && existing.isObject()
                ? (ObjectNode) existing.deepCopy()
                : JsonNodeFactory.instance.objectNode();
        metadata.put(META_TITLE, metaTitle.trim());
        metadata.put(META_DESCRIPTION, metaDescription.trim());
        return metadata;
    }

    static ObjectNode withFocusKeyword(JsonNode existing, String focusKeyword) {
        ObjectNode metadata = existing != null && existing.isObject()
                ? (ObjectNode) existing.deepCopy()
                : JsonNodeFactory.instance.objectNode();
        String value = focusKeyword == null ? "" : focusKeyword.trim();
        if (value.isEmpty()) {
            metadata.remove(FOCUS_KEYWORD);
        } else {
            metadata.put(FOCUS_KEYWORD, value);
        }
        return metadata;
    }

    static ObjectNode withInternalLinks(
            JsonNode existing,
            List<EditorialInternalLinkDtos.Link> links) {
        ObjectNode metadata = existing != null && existing.isObject()
                ? (ObjectNode) existing.deepCopy()
                : JsonNodeFactory.instance.objectNode();
        ArrayNode values = metadata.putArray(INTERNAL_LINKS);
        links.forEach(link -> values.addObject()
                .put("type", link.type())
                .put("targetPath", link.targetPath())
                .put("anchorText", link.anchorText()));
        return metadata;
    }

    static ObjectNode withTypography(
            JsonNode existing,
            EditorialEditorDtos.Typography typography) {
        ObjectNode metadata = existing != null && existing.isObject()
                ? (ObjectNode) existing.deepCopy()
                : JsonNodeFactory.instance.objectNode();
        ObjectNode value = metadata.putObject(TYPOGRAPHY);
        value.put("h1Size", typography.h1Size());
        value.put("h2Size", typography.h2Size());
        value.put("h3Size", typography.h3Size());
        value.put("h4Size", typography.h4Size());
        value.put("paragraphSize", typography.paragraphSize());
        value.put("textColor", typography.textColor());
        return metadata;
    }

    static List<EditorialInternalLinkDtos.Link> internalLinks(JsonNode metadata) {
        if (metadata == null || !metadata.isObject() || !metadata.path(INTERNAL_LINKS).isArray()) {
            return List.of();
        }
        List<EditorialInternalLinkDtos.Link> links = new ArrayList<>();
        for (JsonNode value : metadata.path(INTERNAL_LINKS)) {
            String type = value.path("type").asText("").trim();
            String targetPath = value.path("targetPath").asText("").trim();
            String anchorText = value.path("anchorText").asText("").trim();
            if (!type.isEmpty() && !targetPath.isEmpty() && !anchorText.isEmpty()) {
                links.add(new EditorialInternalLinkDtos.Link(type, targetPath, anchorText));
            }
        }
        return List.copyOf(links);
    }

    static EditorialEditorDtos.Typography typography(JsonNode metadata) {
        var defaults = EditorialEditorDtos.Typography.defaults();
        if (metadata == null || !metadata.isObject() || !metadata.path(TYPOGRAPHY).isObject()) {
            return defaults;
        }
        JsonNode value = metadata.path(TYPOGRAPHY);
        return new EditorialEditorDtos.Typography(
                safeTypographyValue(value, "h1Size", defaults.h1Size(), "COMPACT", "DEFAULT", "LARGE"),
                safeTypographyValue(value, "h2Size", defaults.h2Size(), "COMPACT", "DEFAULT", "LARGE"),
                safeTypographyValue(value, "h3Size", defaults.h3Size(), "COMPACT", "DEFAULT", "LARGE"),
                safeTypographyValue(value, "h4Size", defaults.h4Size(), "COMPACT", "DEFAULT", "LARGE"),
                safeTypographyValue(
                        value,
                        "paragraphSize",
                        defaults.paragraphSize(),
                        "COMPACT",
                        "DEFAULT",
                        "LARGE"),
                safeTypographyValue(
                        value,
                        "textColor",
                        defaults.textColor(),
                        "DEFAULT",
                        "MUTED",
                        "PRIMARY",
                        "SECONDARY"));
    }

    static String metaTitle(JsonNode metadata) {
        return text(metadata, META_TITLE);
    }

    static String metaDescription(JsonNode metadata) {
        return text(metadata, META_DESCRIPTION);
    }

    static String focusKeyword(JsonNode metadata) {
        return text(metadata, FOCUS_KEYWORD);
    }

    static String slugFromFocusKeyword(String focusKeyword) {
        if (focusKeyword == null || focusKeyword.isBlank()) {
            return null;
        }
        String value = Normalizer.normalize(focusKeyword.trim(), Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", "-")
                .replaceAll("(^-+|-+$)", "")
                .replaceAll("-+", "-");
        if (value.isBlank()) {
            return null;
        }
        if (value.length() > 255) {
            value = value.substring(0, 255).replaceAll("-+$", "");
        }
        return value;
    }

    static boolean isComplete(JsonNode metadata) {
        return metaTitle(metadata) != null && metaDescription(metadata) != null;
    }

    static String valueOrFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static String text(JsonNode metadata, String field) {
        if (metadata == null || !metadata.isObject()) {
            return null;
        }
        String value = metadata.path(field).asText("").trim();
        return value.isEmpty() ? null : value;
    }

    private static String safeTypographyValue(
            JsonNode value,
            String field,
            String fallback,
            String... allowed) {
        String candidate = value.path(field).asText("").trim();
        for (String item : allowed) {
            if (item.equals(candidate)) {
                return item;
            }
        }
        return fallback;
    }
}
