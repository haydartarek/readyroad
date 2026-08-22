package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;

final class EditorialArticleVersionDtos {

    private EditorialArticleVersionDtos() {}

    record AppendRequest(
            long articleId,
            String language,
            String title,
            String slug,
            String summary,
            String body,
            JsonNode metadata,
            JsonNode generationMetadata,
            String status) {}

    record Version(
            long id,
            long articleId,
            int versionNumber,
            String language,
            String title,
            String slug,
            String summary,
            String body,
            JsonNode metadata,
            JsonNode generationMetadata,
            String status,
            boolean current,
            Instant createdAt,
            String createdBy) {}
}
