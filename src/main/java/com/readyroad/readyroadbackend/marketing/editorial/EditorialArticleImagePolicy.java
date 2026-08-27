package com.readyroad.readyroadbackend.marketing.editorial;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
class EditorialArticleImagePolicy {

    static final long MAX_UPLOAD_BYTES = 5L * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png");

    private final Clock clock;

    EditorialArticleImagePolicy() {
        this(Clock.systemUTC());
    }

    EditorialArticleImagePolicy(Clock clock) {
        this.clock = clock;
    }

    Normalized normalize(
            MultipartFile file,
            EditorialArticleImageDtos.UploadMetadata metadata,
            String actor
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("An article image file is required");
        }

        if (file.getSize() > MAX_UPLOAD_BYTES) {
            throw new IllegalArgumentException(
                    "Article image exceeds the 5 MB upload limit"
            );
        }

        String contentType = file.getContentType() == null
                ? ""
                : file.getContentType().trim().toLowerCase(Locale.ROOT);

        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Article images must be JPEG or PNG files"
            );
        }

        if (metadata == null) {
            throw new IllegalArgumentException("Article image metadata is required");
        }
        if (!metadata.rightsConfirmed()) {
            throw new IllegalArgumentException("Image usage rights must be confirmed");
        }

        if (actor == null || actor.isBlank() || actor.trim().length() > 160) {
            throw new IllegalArgumentException("A valid image uploader is required");
        }

        Instant uploadedAt = clock.instant();

        return new Normalized(
                EditorialArticleImageDtos.SourcePlatform.LOCAL_UPLOAD,
                "PENDING_SHA256",
                optionalHttps(metadata.sourceUrl(), "sourceUrl"),
                required(metadata.sourceName(), "sourceName", 255),
                null,
                required(metadata.licenseName(), "licenseName", 255),
                optionalHttps(metadata.licenseUrl(), "licenseUrl"),
                uploadedAt,
                uploadedAt,
                safeOriginalFileName(file.getOriginalFilename()),
                safeStoredFileName(metadata.storedFileName()),
                required(metadata.altTextAr(), "altTextAr", 500),
                required(metadata.altTextNl(), "altTextNl", 500),
                required(metadata.altTextFr(), "altTextFr", 500),
                required(metadata.altTextEn(), "altTextEn", 500),
                optional(metadata.captionAr(), 2000),
                optional(metadata.captionNl(), 2000),
                optional(metadata.captionFr(), 2000),
                optional(metadata.captionEn(), 2000),
                metadata.focalPointX() == null ? 0.5 : metadata.focalPointX(),
                metadata.focalPointY() == null ? 0.5 : metadata.focalPointY(),
                required(metadata.approvalReason(), "approvalReason", 1000),
                actor.trim(),
                contentType
        );
    }

    private static String safeStoredFileName(String value) {
        String normalized = required(value, "storedFileName", 128)
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\.[a-z0-9]{2,5}$", "")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("storedFileName must contain ASCII letters or numbers");
        }
        return normalized;
    }

    private static String optionalHttps(String value, String field) {
        String normalized = optional(value, 2000);
        if (normalized == null) {
            return null;
        }
        try {
            URI uri = URI.create(normalized);
            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || uri.getHost() == null
                    || uri.getUserInfo() != null) {
                throw new IllegalArgumentException(field + " must be a public HTTPS URL");
            }
            return uri.normalize().toString();
        } catch (IllegalArgumentException invalid) {
            throw new IllegalArgumentException(field + " must be a public HTTPS URL", invalid);
        }
    }

    private static String safeOriginalFileName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "The original image filename is required"
            );
        }

        String normalized = value.replace('\\', '/');
        normalized = normalized.substring(normalized.lastIndexOf('/') + 1).trim();

        if (normalized.isBlank()
                || normalized.length() > 255
                || normalized.indexOf('\0') >= 0) {
            throw new IllegalArgumentException(
                    "The original image filename is invalid"
            );
        }

        return normalized;
    }

    private static String required(String value, String field, int maxLength) {
        String normalized = value == null ? "" : value.trim();

        if (normalized.isBlank() || normalized.length() > maxLength) {
            throw new IllegalArgumentException(
                    field + " is required and must not exceed "
                            + maxLength + " characters"
            );
        }

        return normalized;
    }

    private static String optional(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();

        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(
                    "Optional text must not exceed " + maxLength + " characters"
            );
        }

        return normalized;
    }

    record Normalized(
            EditorialArticleImageDtos.SourcePlatform sourcePlatform,
            String sourceAssetId,
            String sourceUrl,
            String photographerName,
            String photographerUrl,
            String licenseName,
            String licenseUrl,
            Instant licenseVerifiedAt,
            Instant downloadedAt,
            String originalFileName,
            String storedFileName,
            String altTextAr,
            String altTextNl,
            String altTextFr,
            String altTextEn,
            String captionAr,
            String captionNl,
            String captionFr,
            String captionEn,
            double focalPointX,
            double focalPointY,
            String approvalReason,
            String approvedBy,
            String contentType
    ) {
        Normalized withSourceAssetId(String value) {
            return new Normalized(
                    sourcePlatform,
                    required(value, "sourceAssetId", 255),
                    sourceUrl,
                    photographerName,
                    photographerUrl,
                    licenseName,
                    licenseUrl,
                    licenseVerifiedAt,
                    downloadedAt,
                    originalFileName,
                    storedFileName,
                    altTextAr,
                    altTextNl,
                    altTextFr,
                    altTextEn,
                    captionAr,
                    captionNl,
                    captionFr,
                    captionEn,
                    focalPointX,
                    focalPointY,
                    approvalReason,
                    approvedBy,
                    contentType);
        }
    }
}
