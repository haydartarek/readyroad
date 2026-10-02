package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.dto.response.AdminTrafficSignResponse;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.service.CanonicalSignCatalogService;
import com.readyroad.readyroadbackend.storage.MediaUrlResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrafficSignMapper {

    private final CanonicalSignCatalogService canonicalSignCatalogService;
    private final MediaUrlResolver mediaUrlResolver;

    @Autowired
    public TrafficSignMapper(CanonicalSignCatalogService canonicalSignCatalogService,
            MediaUrlResolver mediaUrlResolver) {
        this.canonicalSignCatalogService = canonicalSignCatalogService;
        this.mediaUrlResolver = mediaUrlResolver;
    }

    /** Compatibility constructor retained for mapper unit tests. */
    public TrafficSignMapper(CanonicalSignCatalogService canonicalSignCatalogService) {
        this(canonicalSignCatalogService, null);
    }

    // ── SignCategory enum → category letter code (for API responses)
    private static final Map<SignCategory, String> CATEGORY_TO_LETTER;
    static {
        CATEGORY_TO_LETTER = new HashMap<>();
        CATEGORY_TO_LETTER.put(SignCategory.DANGER, "A");
        CATEGORY_TO_LETTER.put(SignCategory.PRIORITY, "B");
        CATEGORY_TO_LETTER.put(SignCategory.PROHIBITION, "C");
        CATEGORY_TO_LETTER.put(SignCategory.MANDATORY, "D");
        CATEGORY_TO_LETTER.put(SignCategory.PARKING, "E");
        CATEGORY_TO_LETTER.put(SignCategory.INFORMATION, "F");
        CATEGORY_TO_LETTER.put(SignCategory.ADDITIONAL, "G");
        CATEGORY_TO_LETTER.put(SignCategory.CYCLIST, "M");
        CATEGORY_TO_LETTER.put(SignCategory.DELINEATION, "T");
        CATEGORY_TO_LETTER.put(SignCategory.ZONE, "Z");
        CATEGORY_TO_LETTER.put(SignCategory.ROAD_MANAGEMENT, "FM");
    }

    public TrafficSignResponse toResponse(RoadSign sign) {
        String catLetter = CATEGORY_TO_LETTER.getOrDefault(sign.getCategory(), "");
        CanonicalSignCatalogService.ResolvedSignData resolved = canonicalSignCatalogService.resolve(sign);
        return new TrafficSignResponse(
                sign.getId(),
                resolved.signCode(),
                catLetter,
                canonicalSignCatalogService.routeCodeFor(sign),
                null,
                null,
                resolved.nameAr(),
                resolved.nameEn(),
                resolved.nameNl(),
                resolved.nameFr(),
                resolved.summaryAr(),
                resolved.summaryEn(),
                resolved.summaryNl(),
                resolved.summaryFr(),
                resolved.descriptionAr(),
                resolved.descriptionEn(),
                resolved.descriptionNl(),
                resolved.descriptionFr(),
                resolved.driverGuidanceAr(),
                resolved.driverGuidanceEn(),
                resolved.driverGuidanceNl(),
                resolved.driverGuidanceFr(),
                resolved.exceptionsAr(),
                resolved.exceptionsEn(),
                resolved.exceptionsNl(),
                resolved.exceptionsFr(),
                resolveImageUrl(resolved.imagePath()));
    }

    public AdminTrafficSignResponse toAdminResponse(RoadSign sign) {
        String catLetter = CATEGORY_TO_LETTER.getOrDefault(sign.getCategory(), "");
        CanonicalSignCatalogService.ResolvedSignData resolved = canonicalSignCatalogService.resolve(sign);
        return new AdminTrafficSignResponse(
                sign.getId(),
                sign.getSignCode(),
                catLetter,
                resolved.nameAr(),
                resolved.nameEn(),
                resolved.nameNl(),
                resolved.nameFr(),
                resolved.descriptionAr(),
                resolved.descriptionEn(),
                resolved.descriptionNl(),
                resolved.descriptionFr(),
                resolveImageUrl(resolved.imagePath()),
                sign.getIsActive(),
                sign.getCreatedAt(),
                sign.getUpdatedAt());
    }

    private String resolveImageUrl(String imagePath) {
        return mediaUrlResolver == null ? imagePath : mediaUrlResolver.resolvePublicUrl(imagePath);
    }
}
