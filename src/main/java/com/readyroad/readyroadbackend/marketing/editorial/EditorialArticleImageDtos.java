package com.readyroad.readyroadbackend.marketing.editorial;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public final class EditorialArticleImageDtos {

    private EditorialArticleImageDtos() {
    }

    public enum SourcePlatform {
        LOCAL_UPLOAD
    }

    public record UploadMetadata(
            @NotBlank @Size(max = 128) String storedFileName,
            @NotBlank @Size(max = 255) String sourceName,
            @Size(max = 2000) String sourceUrl,
            @NotBlank @Size(max = 255) String licenseName,
            @Size(max = 2000) String licenseUrl,
            @NotBlank @Size(max = 1000) String approvalReason,
            @AssertTrue(message = "Image usage rights must be confirmed") boolean rightsConfirmed,
            @NotBlank @Size(max = 500) String altTextAr,
            @NotBlank @Size(max = 500) String altTextNl,
            @NotBlank @Size(max = 500) String altTextFr,
            @NotBlank @Size(max = 500) String altTextEn,
            @Size(max = 2000) String captionAr,
            @Size(max = 2000) String captionNl,
            @Size(max = 2000) String captionFr,
            @Size(max = 2000) String captionEn,
            @DecimalMin("0.0") @DecimalMax("1.0") Double focalPointX,
            @DecimalMin("0.0") @DecimalMax("1.0") Double focalPointY
    ) {
    }

    public record Variant(
            String type,
            String format,
            String publicPath,
            int width,
            int height,
            int byteSize
    ) {
    }

    public record Localization(
            String language,
            String altText,
            String caption
    ) {
    }

    public record License(
            long id,
            String sourcePlatform,
            String sourceAssetId,
            String sourceUrl,
            String photographerName,
            String photographerUrl,
            String licenseName,
            String licenseUrl,
            Instant licenseVerifiedAt,
            Instant downloadedAt,
            String originalFileName,
            String approvedBy,
            Instant approvedAt,
            String approvalReason
    ) {
    }

    public record Asset(
            long id,
            long articleId,
            String status,
            String originalFileName,
            String storedFileName,
            int originalWidth,
            int originalHeight,
            double focalPointX,
            double focalPointY,
            List<Variant> variants,
            List<Localization> localizations,
            License license,
            Instant createdAt,
            String createdBy
    ) {
    }

    public record PublicImage(
            long assetId,
            String heroUrl,
            String cardUrl,
            String mobileUrl,
            String thumbnailUrl,
            String ogUrl,
            String altText,
            String caption,
            String sourcePlatform,
            String sourceUrl,
            String photographerName,
            String photographerUrl,
            String licenseName,
            String licenseUrl
    ) {
    }
}
