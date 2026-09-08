package com.readyroad.readyroadbackend.marketing.editorial;

import java.util.Locale;
import java.util.Set;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
class EditorialArticleImagePolicy {

    private static final long MAX_SOURCE_BYTES = 5L * 1024L * 1024L;
    private static final Set<String> CONTENT_TYPES = Set.of("image/jpeg", "image/png");
    private final BackendMessageService messages;

    Normalized normalize(
            MultipartFile file,
            EditorialArticleImageDtos.UploadMetadata metadata,
            String actor) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(messages.get("upload.file_empty"));
        }
        if (file.getSize() > MAX_SOURCE_BYTES) {
            throw new IllegalArgumentException(messages.get("upload.file_too_large_request", 5));
        }
        if (metadata == null) {
            throw new IllegalArgumentException(messages.get("editorial.image.metadata_required"));
        }
        if (actor == null || actor.isBlank() || actor.trim().length() > 160) {
            throw new IllegalArgumentException("A valid article image uploader is required");
        }

        String contentType = normalizeContentType(file.getContentType());
        if (!CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(messages.get("editorial.image.invalid_type"));
        }

        return new Normalized(
                safeOriginalFileName(file.getOriginalFilename(), contentType),
                storedFileName(metadata.storedFileName()),
                required(metadata.altTextAr(), "altTextAr", 500),
                required(metadata.altTextNl(), "altTextNl", 500),
                required(metadata.altTextFr(), "altTextFr", 500),
                required(metadata.altTextEn(), "altTextEn", 500),
                actor.trim(),
                contentType);
    }

    private static String normalizeContentType(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String storedFileName(String value) {
        String normalized = required(value, "storedFileName", 128)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "")
                .replaceAll("-+", "-");
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(messages.get("editorial.image.metadata_required"));
        }
        return normalized;
    }

    private static String safeOriginalFileName(String value, String contentType) {
        String extension = "image/png".equals(contentType) ? ".png" : ".jpg";
        if (value == null || value.isBlank()) {
            return "article-image" + extension;
        }
        String normalized = value.trim().replace('\\', '/');
        normalized = normalized.substring(normalized.lastIndexOf('/') + 1)
                .replaceAll("[\\r\\n\\t]", "_");
        if (normalized.length() > 255) {
            normalized = normalized.substring(normalized.length() - 255);
        }
        return normalized.isBlank() ? "article-image" + extension : normalized;
    }

    private String required(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field.startsWith("altText")
                    ? messages.get("editorial.image.alt_required", field.substring(7).toUpperCase(Locale.ROOT))
                    : messages.get("editorial.image.metadata_required"));
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(messages.get("editorial.image.text_too_long", maxLength));
        }
        return normalized;
    }

    record Normalized(
            String originalFileName,
            String storedFileName,
            String altTextAr,
            String altTextNl,
            String altTextFr,
            String altTextEn,
            String uploadedBy,
            String contentType
    ) {}
}
