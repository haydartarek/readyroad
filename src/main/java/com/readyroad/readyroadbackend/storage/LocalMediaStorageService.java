package com.readyroad.readyroadbackend.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        prefix = "app.media.storage",
        name = "provider",
        havingValue = "local",
        matchIfMissing = true)
public class LocalMediaStorageService implements MediaStorageService {

    private final Path rootDirectory;
    private final Path privateRootDirectory;
    private final Map<String, Path> publicDirectories;
    private final String publicBaseUrl;

    public LocalMediaStorageService(MediaStorageProperties properties) {
        this.rootDirectory = Path.of(properties.getLocalRootDirectory())
                .toAbsolutePath()
                .normalize();
        this.privateRootDirectory = rootDirectory.resolveSibling("private-media")
                .toAbsolutePath()
                .normalize();
        this.publicDirectories = configuredPublicDirectories(properties.getLocalPublicDirectories());
        this.publicBaseUrl = trimTrailingSlash(properties.getPublicBaseUrl());
    }

    @Override
    public void put(MediaStorageBucket bucket, String key, InputStream content,
            long contentLength, String contentType)
            throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("Media content is required");
        }
        if (contentLength < 0) {
            throw new IllegalArgumentException("Media content length cannot be negative");
        }

        Path target = resolvePath(bucket, key);
        Files.createDirectories(target.getParent());
        Path temporary = Files.createTempFile(target.getParent(), ".media-upload-", ".tmp");
        try {
            Files.copy(content, temporary, StandardCopyOption.REPLACE_EXISTING);
            try {
                Files.move(temporary, target,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    @Override
    public InputStream open(MediaStorageBucket bucket, String key) throws IOException {
        return Files.newInputStream(resolvePath(bucket, key));
    }

    @Override
    public boolean exists(MediaStorageBucket bucket, String key) {
        return Files.isRegularFile(resolvePath(bucket, key));
    }

    @Override
    public boolean delete(MediaStorageBucket bucket, String key) throws IOException {
        return Files.deleteIfExists(resolvePath(bucket, key));
    }

    @Override
    public String resolveUrl(MediaStorageBucket bucket, String key) {
        requirePublicBucket(bucket);
        String normalized = MediaStorageKeys.normalize(key);
        String path = "/images/" + encodePath(normalized);
        return publicBaseUrl.isBlank() ? path : publicBaseUrl + path;
    }

    Path getRootDirectory() {
        return rootDirectory;
    }

    private Path resolvePath(MediaStorageBucket bucket, String key) {
        String normalized = MediaStorageKeys.normalize(key);
        Path bucketRoot = bucketRoot(bucket, normalized);
        String relativeKey = normalized;
        if (bucket == MediaStorageBucket.PUBLIC) {
            var mapping = publicDirectory(normalized);
            if (mapping != null) {
                relativeKey = normalized.substring(mapping.getKey().length());
                if (relativeKey.startsWith("/")) {
                    relativeKey = relativeKey.substring(1);
                }
            }
        }
        Path resolved = bucketRoot.resolve(relativeKey).normalize();
        if (!resolved.startsWith(bucketRoot)) {
            throw new IllegalArgumentException("Invalid media storage key");
        }
        return resolved;
    }

    private Path bucketRoot(MediaStorageBucket bucket, String key) {
        if (bucket == null) {
            throw new IllegalArgumentException("Media storage bucket is required");
        }
        if (bucket == MediaStorageBucket.PRIVATE) {
            return privateRootDirectory;
        }
        var mapping = publicDirectory(key);
        return mapping == null ? rootDirectory : mapping.getValue();
    }

    private Map.Entry<String, Path> publicDirectory(String key) {
        return publicDirectories.entrySet().stream()
                .filter(entry -> key.equals(entry.getKey()) || key.startsWith(entry.getKey() + "/"))
                .max(Map.Entry.comparingByKey((left, right) -> Integer.compare(left.length(), right.length())))
                .orElse(null);
    }

    private static Map<String, Path> configuredPublicDirectories(Map<String, String> configured) {
        Map<String, Path> directories = new LinkedHashMap<>();
        if (configured == null) {
            return directories;
        }
        configured.forEach((prefix, directory) -> {
            if (prefix == null || prefix.isBlank() || directory == null || directory.isBlank()) {
                return;
            }
            directories.put(
                    MediaStorageKeys.normalize(prefix),
                    Path.of(directory).toAbsolutePath().normalize());
        });
        return directories;
    }

    private static void requirePublicBucket(MediaStorageBucket bucket) {
        if (bucket != MediaStorageBucket.PUBLIC) {
            throw new IllegalArgumentException("Private media cannot resolve to a public URL");
        }
    }

    private static String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.replaceFirst("/+$", "");
    }

    private static String encodePath(String key) {
        String[] segments = key.split("/", -1);
        for (int index = 0; index < segments.length; index++) {
            segments[index] = java.net.URLEncoder.encode(
                    segments[index], StandardCharsets.UTF_8).replace("+", "%20");
        }
        return String.join("/", segments);
    }
}
