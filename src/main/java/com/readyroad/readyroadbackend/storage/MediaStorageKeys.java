package com.readyroad.readyroadbackend.storage;

import java.nio.file.Path;

final class MediaStorageKeys {

    private MediaStorageKeys() {
    }

    static String normalize(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Media storage key is required");
        }

        String normalized = key.trim();
        if (normalized.isBlank() || normalized.indexOf('\0') >= 0) {
            throw new IllegalArgumentException("Invalid media storage key");
        }
        if (normalized.startsWith("/") || normalized.startsWith("\\")
                || normalized.indexOf('\\') >= 0) {
            throw new IllegalArgumentException("Invalid media storage key: use '/' separators");
        }

        for (String segment : normalized.split("/", -1)) {
            if (segment.isBlank() || segment.equals(".") || segment.equals("..")
                    || segment.indexOf(':') >= 0) {
                throw new IllegalArgumentException("Invalid media storage key");
            }
        }

        Path path = Path.of(normalized).normalize();
        if (path.isAbsolute() || path.startsWith("..")) {
            throw new IllegalArgumentException("Invalid media storage key");
        }

        return normalized;
    }
}
