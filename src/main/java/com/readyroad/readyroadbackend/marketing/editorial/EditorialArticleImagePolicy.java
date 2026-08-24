package com.readyroad.readyroadbackend.marketing.editorial;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
class EditorialArticleImagePolicy {

    static final long MAX_UPLOAD_BYTES = 20L * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");
    private static final Map<EditorialArticleImageDtos.SourcePlatform, Set<String>> SOURCE_HOSTS = Map.of(
            EditorialArticleImageDtos.SourcePlatform.UNSPLASH, Set.of("unsplash.com"),
            EditorialArticleImageDtos.SourcePlatform.PIXABAY, Set.of("pixabay.com"),
            EditorialArticleImageDtos.SourcePlatform.PEXELS, Set.of("pexels.com"));

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
            String actor) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("An article image file is required");
        }
        if (file.getSize() > MAX_UPLOAD_BYTES) {
            throw new IllegalArgumentException("Article image exceeds the 20 MB upload limit");
        }
        String contentType = file.getContentType() == null
                ? ""
                : file.getContentType().trim().toLowerCase(Locale.ROOT);
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Article images must be JPEG or PNG files");
        }
        if (metadata == null || metadata.sourcePlatform() == null) {
            throw new IllegalArgumentException("Article image source metadata is required");
        }
        if (actor == null || actor.isBlank() || actor.trim().length() > 160) {
            throw new IllegalArgumentException("A valid image approver is required");
        }

        String sourceUrl = approvedSourceUrl(metadata.sourceUrl(), metadata.sourcePlatform());
        String photographerUrl = httpsUrl(metadata.photographerUrl(), "photographerUrl");
        String licenseUrl = httpsUrl(metadata.licenseUrl(), "licenseUrl");
        Instant now = clock.instant().plusSeconds(300);
        if (metadata.licenseVerifiedAt() == null || metadata.licenseVerifiedAt().isAfter(now)) {
            throw new IllegalArgumentException("licenseVerifiedAt must describe a completed verification");
        }
        if (metadata.downloadedAt() == null || metadata.downloadedAt().isAfter(now)) {
            throw new IllegalArgumentException("downloadedAt must describe the uploaded source file");
        }

        return new Normalized(
                metadata.sourcePlatform(),
                required(metadata.sourceAssetId(), "sourceAssetId", 255),
                sourceUrl,
                required(metadata.photographerName(), "photographerName", 255),
                photographerUrl,
                required(metadata.licenseName(), "licenseName", 255),
                licenseUrl,
                metadata.licenseVerifiedAt(),
                metadata.downloadedAt(),
                safeOriginalFileName(file.getOriginalFilename()),
                required(metadata.altTextAr(), "altTextAr", 500),
                required(metadata.altTextNl(), "altTextNl", 500),
                required(metadata.altTextFr(), "altTextFr", 500),
                required(metadata.altTextEn(), "altTextEn", 500),
                optional(metadata.captionAr()),
                optional(metadata.captionNl()),
                optional(metadata.captionFr()),
                optional(metadata.captionEn()),
                metadata.focalPointX() == null ? 0.5 : metadata.focalPointX(),
                metadata.focalPointY() == null ? 0.5 : metadata.focalPointY(),
                required(metadata.approvalReason(), "approvalReason", 1000),
                actor.trim(),
                contentType);
    }

    private static String approvedSourceUrl(
            String value,
            EditorialArticleImageDtos.SourcePlatform platform) {
        String normalized = httpsUrl(value, "sourceUrl");
        URI uri = URI.create(normalized);
        String host = uri.getHost().toLowerCase(Locale.ROOT);
        boolean allowed = SOURCE_HOSTS.get(platform).stream()
                .anyMatch(expected -> host.equals(expected) || host.endsWith("." + expected));
        if (!allowed) {
            throw new IllegalArgumentException("sourceUrl does not match sourcePlatform " + platform);
        }
        return normalized;
    }

    private static String httpsUrl(String value, String field) {
        String normalized = required(value, field, 2000);
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
            throw new IllegalArgumentException("The original image filename is required");
        }
        String normalized = value.replace('\\', '/');
        normalized = normalized.substring(normalized.lastIndexOf('/') + 1).trim();
        if (normalized.isBlank() || normalized.length() > 255 || normalized.indexOf('\0') >= 0) {
            throw new IllegalArgumentException("The original image filename is invalid");
        }
        return normalized;
    }

    private static String required(String value, String field, int maxLength) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank() || normalized.length() > maxLength) {
            throw new IllegalArgumentException(field + " is required and must not exceed " + maxLength + " characters");
        }
        return normalized;
    }

    private static String optional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
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
            String contentType) {}
}
