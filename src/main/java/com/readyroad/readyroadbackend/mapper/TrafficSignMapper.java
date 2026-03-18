package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import com.readyroad.readyroadbackend.dto.response.AdminTrafficSignResponse;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrafficSignMapper {

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
    private String buildCanonicalImageUrl(String signCode, String categoryCode, String rawImageUrl) {
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
        if (rawImageUrl != null && !rawImageUrl.isEmpty()) {
            int lastSlash = rawImageUrl.lastIndexOf('/');
            filename = (lastSlash >= 0) ? rawImageUrl.substring(lastSlash + 1) : rawImageUrl;
        }
        if (folder != null && filename != null && !filename.isEmpty()) {
            return "/images/signs/" + folder + "/" + filename;
        }
        // 3. Fallback — return raw value unchanged
        return rawImageUrl;
    }

    public TrafficSignResponse toResponse(TrafficSign sign) {
        return new TrafficSignResponse(
                sign.getId(),
                sign.getSignCode(),
                sign.getCategory().getCode(),
                sign.getNameAr(),
                sign.getNameEn(),
                sign.getNameNl(),
                sign.getNameFr(),
                sign.getDescriptionAr(),
                sign.getDescriptionEn(),
                sign.getDescriptionNl(),
                sign.getDescriptionFr(),
                sign.getLongDescriptionEn(),
                sign.getLongDescriptionNl(),
                sign.getLongDescriptionFr(),
                sign.getLongDescriptionAr(),
                sign.isLongDescriptionComplete(),
                buildCanonicalImageUrl(sign.getSignCode(), sign.getCategory().getCode(), sign.getImageUrl()));
    }

    public AdminTrafficSignResponse toAdminResponse(TrafficSign sign) {
        return new AdminTrafficSignResponse(
                sign.getId(),
                sign.getSignCode(),
                sign.getCategory().getCode(),
                sign.getNameAr(),
                sign.getNameEn(),
                sign.getNameNl(),
                sign.getNameFr(),
                sign.getDescriptionAr(),
                sign.getDescriptionEn(),
                sign.getDescriptionNl(),
                sign.getDescriptionFr(),
                sign.getLongDescriptionEn(),
                sign.getLongDescriptionNl(),
                sign.getLongDescriptionFr(),
                sign.getLongDescriptionAr(),
                sign.isLongDescriptionComplete(),
                buildCanonicalImageUrl(sign.getSignCode(), sign.getCategory().getCode(), sign.getImageUrl()),
                sign.getIsActive(),
                sign.getCreatedAt(),
                sign.getUpdatedAt());
    }
}
