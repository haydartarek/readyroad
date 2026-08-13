package com.readyroad.readyroadbackend.marketing.editorial;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public final class EditorialSourceDtos {

    private EditorialSourceDtos() {
    }

    public record SourceCollectionRequest(
            @NotNull Long articleTopicId,
            @NotBlank @Size(max = 255) String briefReference,
            @NotEmpty List<@Valid ClaimInput> claims,
            @NotBlank @Size(max = 255) String idempotencyKey) {
    }

    public record ClaimInput(
            @NotBlank @Size(max = 128) String claimKey,
            @NotBlank @Size(max = 8000) String claimText,
            @NotBlank String claimType,
            @NotBlank String language,
            boolean legalReviewRequired,
            @NotNull List<@Valid SourceInput> sources) {
    }

    public record SourceInput(
            @NotBlank String sourceType,
            @NotBlank String locationType,
            @NotBlank @Size(max = 2000) String title,
            @NotBlank @Size(max = 255) String publisher,
            @Size(max = 4000) String url,
            @Size(max = 512) String internalReference,
            @Size(max = 128) String jurisdiction,
            @NotBlank String language,
            @NotBlank String verificationStatus,
            @NotBlank String trustStatus,
            boolean legalReviewRequired,
            @NotBlank String legalReviewStatus,
            @Size(max = 128) String fingerprint,
            @Size(max = 512) String etag,
            @Size(max = 255) String lastModified) {
    }

    public record Source(
            long id,
            String sourceType,
            String locationType,
            String title,
            String publisher,
            String url,
            String internalReference,
            String jurisdiction,
            String language,
            String verificationStatus,
            String trustStatus,
            boolean legalReviewRequired,
            String legalReviewStatus,
            Instant verifiedAt,
            String verifiedBy,
            Instant lastCheckedAt,
            String fingerprint,
            boolean active,
            long claimCount) {
    }
}
