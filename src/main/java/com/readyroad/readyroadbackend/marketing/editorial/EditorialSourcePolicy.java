package com.readyroad.readyroadbackend.marketing.editorial;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

final class EditorialSourcePolicy {

    private static final Set<String> SOURCE_TYPES = Set.of(
            "READYROAD_CORE_DATA",
            "OFFICIAL_LEGAL_SOURCE",
            "OFFICIAL_GOVERNMENT_SOURCE",
            "OFFICIAL_PUBLIC_AUTHORITY_SOURCE",
            "APPROVED_INTERNAL_SOURCE",
            "APPROVED_REFERENCE_SOURCE");
    private static final Set<String> INTERNAL_TYPES = Set.of(
            "READYROAD_CORE_DATA", "APPROVED_INTERNAL_SOURCE");
    private static final Set<String> OFFICIAL_TYPES = Set.of(
            "OFFICIAL_LEGAL_SOURCE",
            "OFFICIAL_GOVERNMENT_SOURCE",
            "OFFICIAL_PUBLIC_AUTHORITY_SOURCE");
    private static final Set<String> CLAIM_TYPES = Set.of(
            "FACTUAL", "LEGAL", "REGIONAL", "DATE_SENSITIVE", "STATISTIC", "PRODUCT_FACT");
    private static final Set<String> LANGUAGES = Set.of("AR", "NL", "FR", "EN", "UNKNOWN");
    private static final Set<String> VERIFICATION_STATUSES = Set.of(
            "UNVERIFIED", "VERIFIED", "REQUIRES_REVIEW", "REJECTED", "STALE");
    private static final Set<String> TRUST_STATUSES = Set.of(
            "CORE_TRUSTED", "OFFICIAL", "APPROVED_REFERENCE", "UNTRUSTED");
    private static final Set<String> LEGAL_STATUSES = Set.of(
            "NOT_REQUIRED", "REQUIRES_REVIEW", "VERIFIED", "REJECTED", "STALE");

    void validate(EditorialSourceDtos.SourceCollectionRequest request) {
        if (request == null || request.articleTopicId() == null || request.articleTopicId() <= 0) {
            throw new IllegalArgumentException("A valid article topic is required");
        }
        if (request.claims() == null || request.claims().isEmpty()) {
            throw new IllegalArgumentException("At least one explicit claim is required");
        }
        for (EditorialSourceDtos.ClaimInput claim : request.claims()) {
            String claimType = upper(claim.claimType());
            requireAllowed("claim type", claimType, CLAIM_TYPES);
            requireAllowed("claim language", upper(claim.language()), Set.of("AR", "NL", "FR", "EN"));
            if ("LEGAL".equals(claimType) && !claim.legalReviewRequired()) {
                throw new IllegalArgumentException("Legal claims require legal review");
            }
            if (claim.sources() == null) {
                throw new IllegalArgumentException("Claim sources must be an explicit list");
            }
            claim.sources().forEach(this::validateSource);
        }
    }

    private void validateSource(EditorialSourceDtos.SourceInput source) {
        String sourceType = upper(source.sourceType());
        String locationType = upper(source.locationType());
        String verificationStatus = upper(source.verificationStatus());
        String trustStatus = upper(source.trustStatus());
        String legalStatus = upper(source.legalReviewStatus());
        requireAllowed("source type", sourceType, SOURCE_TYPES);
        requireAllowed("location type", locationType, Set.of("INTERNAL", "EXTERNAL"));
        requireAllowed("source language", upper(source.language()), LANGUAGES);
        requireAllowed("verification status", verificationStatus, VERIFICATION_STATUSES);
        requireAllowed("trust status", trustStatus, TRUST_STATUSES);
        requireAllowed("legal review status", legalStatus, LEGAL_STATUSES);

        boolean internal = INTERNAL_TYPES.contains(sourceType);
        if (internal != "INTERNAL".equals(locationType)) {
            throw new IllegalArgumentException("Source location does not match source type");
        }
        if (internal) {
            if (isBlank(source.internalReference()) || !isBlank(source.url())) {
                throw new IllegalArgumentException("Internal sources require only an internal reference");
            }
        } else if (isBlank(source.url()) || !isBlank(source.internalReference())) {
            throw new IllegalArgumentException("External sources require only an HTTPS URL");
        } else {
            canonicalUrl(source.url());
        }

        if ("READYROAD_CORE_DATA".equals(sourceType) && !"CORE_TRUSTED".equals(trustStatus)) {
            throw new IllegalArgumentException("ReadyRoad core data must use CORE_TRUSTED status");
        }
        if (OFFICIAL_TYPES.contains(sourceType) && !"OFFICIAL".equals(trustStatus)) {
            throw new IllegalArgumentException("Official sources must use OFFICIAL trust status");
        }
        if (("APPROVED_INTERNAL_SOURCE".equals(sourceType)
                || "APPROVED_REFERENCE_SOURCE".equals(sourceType))
                && !"APPROVED_REFERENCE".equals(trustStatus)) {
            throw new IllegalArgumentException("Approved references must use APPROVED_REFERENCE trust status");
        }
        if ("VERIFIED".equals(verificationStatus) && "UNTRUSTED".equals(trustStatus)) {
            throw new IllegalArgumentException("An untrusted source cannot be verified");
        }
        if ((source.legalReviewRequired() && "NOT_REQUIRED".equals(legalStatus))
                || (!source.legalReviewRequired() && !"NOT_REQUIRED".equals(legalStatus))) {
            throw new IllegalArgumentException("Legal review status does not match its requirement");
        }
    }

    boolean supportsClaim(String claimType, EditorialSourceStore.StoredSource source) {
        if (!source.active() || !"VERIFIED".equals(source.verificationStatus())) {
            return false;
        }
        if ("LEGAL".equals(upper(claimType))) {
            return "VERIFIED".equals(source.legalReviewStatus())
                    && (("READYROAD_CORE_DATA".equals(source.sourceType())
                                    && "CORE_TRUSTED".equals(source.trustStatus()))
                            || (OFFICIAL_TYPES.contains(source.sourceType())
                                    && "OFFICIAL".equals(source.trustStatus())));
        }
        return Set.of("CORE_TRUSTED", "OFFICIAL", "APPROVED_REFERENCE")
                .contains(source.trustStatus());
    }

    String evidencePurpose(String claimType) {
        return switch (upper(claimType)) {
            case "LEGAL" -> "LEGAL";
            case "STATISTIC" -> "STATISTICAL";
            case "PRODUCT_FACT" -> "PRODUCT";
            case "REGIONAL", "DATE_SENSITIVE" -> "CONTEXTUAL";
            default -> "FACTUAL";
        };
    }

    String canonicalUrl(String rawUrl) {
        try {
            URI uri = new URI(rawUrl.trim()).normalize();
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null || uri.getUserInfo() != null) {
                throw new IllegalArgumentException("External sources require a public HTTPS URL");
            }
            int port = uri.getPort() == 443 ? -1 : uri.getPort();
            String path = uri.getPath();
            if (path == null || path.isBlank()) {
                path = "/";
            } else if (path.length() > 1 && path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            return new URI(
                    "https",
                    null,
                    uri.getHost().toLowerCase(Locale.ROOT),
                    port,
                    path,
                    uri.getQuery(),
                    null).toASCIIString();
        } catch (URISyntaxException error) {
            throw new IllegalArgumentException("External source URL is invalid", error);
        }
    }

    static String upper(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private static void requireAllowed(String field, String value, Set<String> allowed) {
        if (!allowed.contains(value)) {
            throw new IllegalArgumentException("Unsupported " + field + ": " + value);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
