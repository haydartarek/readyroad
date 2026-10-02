package com.readyroad.readyroadbackend.storage;

import org.springframework.stereotype.Component;

/**
 * Converts persisted logical public-media paths into URLs for the active
 * storage provider. Private object prefixes are rejected before they can be
 * passed to the public URL resolver.
 */
@Component
public class MediaUrlResolver {

    private final MediaStorageService mediaStorageService;

    public MediaUrlResolver(MediaStorageService mediaStorageService) {
        this.mediaStorageService = mediaStorageService;
    }

    public String resolvePublicUrl(String mediaPath) {
        if (mediaPath == null || mediaPath.isBlank()) {
            return null;
        }

        String value = mediaPath.trim();
        if (value.startsWith("http://") || value.startsWith("https://")
                || value.startsWith("data:")) {
            return value;
        }

        String key = toLogicalKey(value);
        if (key.startsWith("originals/") || key.startsWith("archive/")
                || key.startsWith("private/")) {
            throw new IllegalArgumentException("Private media cannot resolve to a public URL");
        }
        return mediaStorageService.resolveUrl(MediaStorageBucket.PUBLIC, key);
    }

    private static String toLogicalKey(String value) {
        if (value.startsWith("/images/")) {
            return value.substring("/images/".length());
        }
        if (value.startsWith("images/")) {
            return value.substring("images/".length());
        }
        if (value.startsWith("/")) {
            return value.substring(1);
        }
        return value;
    }
}
