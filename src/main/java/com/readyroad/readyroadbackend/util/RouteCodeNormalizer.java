package com.readyroad.readyroadbackend.util;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class RouteCodeNormalizer {

    private static final Map<String, String> LEGACY_CODE_ALIASES = Map.of(
            "c11a", "C11",
            "c11b", "C11",
            "c22a", "C22",
            "c43_10", "C43",
            "c43_30", "C43",
            "c43_50", "C43",
            "c43_70", "C43",
            "c43_90", "C43");

    private static final Set<String> LEGACY_CODES_WITHOUT_DIRECT_REPLACEMENT = Set.of(
            "c28a");

    private RouteCodeNormalizer() {
    }

    public static String resolveLegacyAlias(String value) {
        if (value == null) {
            return "";
        }

        String raw = value.trim();
        if (raw.isEmpty()) {
            return "";
        }

        return LEGACY_CODE_ALIASES.getOrDefault(raw.toLowerCase(Locale.ROOT), raw);
    }

    public static boolean isLegacyCodeWithoutDirectReplacement(String value) {
        if (value == null) {
            return false;
        }

        return LEGACY_CODES_WITHOUT_DIRECT_REPLACEMENT.contains(value.trim().toLowerCase(Locale.ROOT));
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }

        String normalized = value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_+", "");

        return normalized;
    }
}
