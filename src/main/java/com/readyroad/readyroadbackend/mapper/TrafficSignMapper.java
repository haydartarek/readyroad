package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.dto.response.AdminTrafficSignResponse;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.service.CanonicalSignCatalogService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrafficSignMapper {

    private final CanonicalSignCatalogService canonicalSignCatalogService;

    public TrafficSignMapper(CanonicalSignCatalogService canonicalSignCatalogService) {
        this.canonicalSignCatalogService = canonicalSignCatalogService;
    }

    // ── SignCategory enum → category letter code (for API responses / folder
    // lookup)
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
        CATEGORY_TO_LETTER.put(SignCategory.ROAD_MANAGEMENT, "F");
    }

    // ── Category letter → public images folder ──────────────────────────────
    private static final Map<String, String> CATEGORY_FOLDER_MAP;
    static {
        CATEGORY_FOLDER_MAP = new HashMap<>();
        CATEGORY_FOLDER_MAP.put("A", "danger_signs");
        CATEGORY_FOLDER_MAP.put("B", "priority_signs");
        CATEGORY_FOLDER_MAP.put("C", "prohibition_signs");
        CATEGORY_FOLDER_MAP.put("D", "mandatory_signs");
        CATEGORY_FOLDER_MAP.put("E", "parking_signs");
        CATEGORY_FOLDER_MAP.put("F", "information_signs");
        CATEGORY_FOLDER_MAP.put("G", "additional_signs");
        CATEGORY_FOLDER_MAP.put("H", "information_signs");
        CATEGORY_FOLDER_MAP.put("M", "additional_signs");
        CATEGORY_FOLDER_MAP.put("T", "delineation_signs");
        CATEGORY_FOLDER_MAP.put("Z", "zone_signs");
    }

    // ── Explicit overrides for signs with known path issues ─────────────────
    private static final Map<String, String> SIGN_CODE_OVERRIDES;
    static {
        SIGN_CODE_OVERRIDES = new HashMap<>();
        SIGN_CODE_OVERRIDES.put("F39", "/images/signs/road_markings/F39 Aankondiging van een omleiding.png");
        SIGN_CODE_OVERRIDES.put("F79",
                "/images/signs/road_markings/F79 Tijdelijke verdeling van de rijstroken (met afstandsaanduiding).png");
        SIGN_CODE_OVERRIDES.put("F81", "/images/signs/road_markings/F81 Voorwegwijzer uitwijking.png");
        SIGN_CODE_OVERRIDES.put("F83", "/images/signs/road_markings/F83 Versmalling van de rijbaan.png");
        SIGN_CODE_OVERRIDES.put("F85", "/images/signs/road_markings/F85 Verlegging van de rijbaan.png");
        SIGN_CODE_OVERRIDES.put("F89",
                "/images/signs/road_markings/F89 Aanduiding van de maximumsnelheid per rijstrook.png");
        SIGN_CODE_OVERRIDES.put("F91",
                "/images/signs/road_markings/F91 Aanduiding van de maximumsnelheid per rijstrook (zonder afstand).png");
        SIGN_CODE_OVERRIDES.put("F95", "/images/signs/road_markings/F95 Einde van een rijstrook.png");
        SIGN_CODE_OVERRIDES.put("F98", "/images/signs/road_markings/F98 Bijzondere rijstrookregeling.png");
    }

    // ── Resolver ─────────────────────────────────────────────────────────────
    private String buildCanonicalImageUrl(String signCode, String categoryCode, String rawImagePath) {
        // 1. Explicit override
        if (signCode != null && SIGN_CODE_OVERRIDES.containsKey(signCode)) {
            return SIGN_CODE_OVERRIDES.get(signCode);
        }
        // 2. Category-based canonical path
        String catLetter = (categoryCode != null && !categoryCode.isEmpty())
                ? categoryCode.substring(0, 1).toUpperCase()
                : (signCode != null && !signCode.isEmpty() ? signCode.substring(0, 1).toUpperCase() : "");
        String folder = CATEGORY_FOLDER_MAP.get(catLetter);
        String filename = null;
        if (rawImagePath != null && !rawImagePath.isEmpty()) {
            int lastSlash = rawImagePath.lastIndexOf('/');
            filename = (lastSlash >= 0) ? rawImagePath.substring(lastSlash + 1) : rawImagePath;
        }
        if (folder != null && filename != null && !filename.isEmpty()) {
            return "/images/signs/" + folder + "/" + filename;
        }
        // 3. Fallback — return raw value unchanged
        return rawImagePath;
    }

    public TrafficSignResponse toResponse(RoadSign sign) {
        String catLetter = CATEGORY_TO_LETTER.getOrDefault(sign.getCategory(), "");
        CanonicalSignCatalogService.ResolvedSignData resolved = canonicalSignCatalogService.resolve(sign);
        return new TrafficSignResponse(
                sign.getId(),
                resolved.signCode(),
                catLetter,
                canonicalSignCatalogService.routeCodeFor(sign),
                resolved.nameAr(),
                resolved.nameEn(),
                resolved.nameNl(),
                resolved.nameFr(),
                resolved.descriptionAr(),
                resolved.descriptionEn(),
                resolved.descriptionNl(),
                resolved.descriptionFr(),
                resolved.longDescriptionEn(),
                resolved.longDescriptionNl(),
                resolved.longDescriptionFr(),
                resolved.longDescriptionAr(),
                resolved.hasLongDescription(),
                buildCanonicalImageUrl(resolved.signCode(), catLetter, resolved.imagePath()));
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
                resolved.longDescriptionEn(),
                resolved.longDescriptionNl(),
                resolved.longDescriptionFr(),
                resolved.longDescriptionAr(),
                resolved.hasLongDescription(),
                buildCanonicalImageUrl(sign.getSignCode(), catLetter, resolved.imagePath()),
                sign.getIsActive(),
                sign.getCreatedAt(),
                sign.getUpdatedAt());
    }
}
