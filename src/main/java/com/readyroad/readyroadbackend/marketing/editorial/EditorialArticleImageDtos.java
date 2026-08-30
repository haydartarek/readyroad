package com.readyroad.readyroadbackend.marketing.editorial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

final class EditorialArticleImageDtos {

    private EditorialArticleImageDtos() {}

    record UploadMetadata(
            @NotBlank @Size(max = 128) String storedFileName,
            @NotBlank @Size(max = 500) String altTextAr,
            @NotBlank @Size(max = 500) String altTextNl,
            @NotBlank @Size(max = 500) String altTextFr,
            @NotBlank @Size(max = 500) String altTextEn
    ) {
        UploadMetadata(
                String storedFileName,
                String ignoredSourceName,
                String ignoredSourceUrl,
                String ignoredLicenseName,
                String ignoredLicenseUrl,
                String ignoredApprovalReason,
                boolean ignoredRightsConfirmed,
                String altTextAr,
                String altTextNl,
                String altTextFr,
                String altTextEn,
                String ignoredCaptionAr,
                String ignoredCaptionNl,
                String ignoredCaptionFr,
                String ignoredCaptionEn,
                double ignoredFocalPointX,
                double ignoredFocalPointY) {
            this(storedFileName, altTextAr, altTextNl, altTextFr, altTextEn);
        }
    }

    record Variant(
            String type,
            String format,
            String publicPath,
            int width,
            int height,
            int byteSize
    ) {}

    record Localization(
            String language,
            String altText
    ) {}

    record Asset(
            long id,
            long articleId,
            String status,
            String originalFileName,
            String storedFileName,
            int originalWidth,
            int originalHeight,
            List<Variant> variants,
            List<Localization> localizations,
            Instant createdAt,
            String createdBy
    ) {}

    record PublicImage(
            long assetId,
            String heroUrl,
            String cardUrl,
            String mediumUrl,
            String mobileUrl,
            String ogUrl,
            String altText
    ) {}
}
