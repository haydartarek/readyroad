package com.readyroad.readyroadbackend.util;

import java.util.Locale;

public final class RouteCodeNormalizer {

    private RouteCodeNormalizer() {
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
