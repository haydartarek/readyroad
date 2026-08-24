package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;

final class EditorialArticleMetadata {

    static final String META_TITLE = "metaTitle";
    static final String META_DESCRIPTION = "metaDescription";
    static final String INTERNAL_LINKS = "internalLinks";

    private EditorialArticleMetadata() {}

    static ObjectNode withSeoMetadata(JsonNode existing, String metaTitle, String metaDescription) {
        ObjectNode metadata = existing != null && existing.isObject()
                ? (ObjectNode) existing.deepCopy()
                : JsonNodeFactory.instance.objectNode();
        metadata.put(META_TITLE, metaTitle.trim());
        metadata.put(META_DESCRIPTION, metaDescription.trim());
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
