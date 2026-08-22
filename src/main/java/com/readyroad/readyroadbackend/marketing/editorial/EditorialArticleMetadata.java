package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

final class EditorialArticleMetadata {

    static final String META_TITLE = "metaTitle";
    static final String META_DESCRIPTION = "metaDescription";

    private EditorialArticleMetadata() {}

    static ObjectNode withSeoMetadata(JsonNode existing, String metaTitle, String metaDescription) {
        ObjectNode metadata = existing != null && existing.isObject()
                ? (ObjectNode) existing.deepCopy()
                : JsonNodeFactory.instance.objectNode();
        metadata.put(META_TITLE, metaTitle.trim());
        metadata.put(META_DESCRIPTION, metaDescription.trim());
        return metadata;
    }

    static String metaTitle(JsonNode metadata) {
        return text(metadata, META_TITLE);
    }

    static String metaDescription(JsonNode metadata) {
        return text(metadata, META_DESCRIPTION);
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
}
