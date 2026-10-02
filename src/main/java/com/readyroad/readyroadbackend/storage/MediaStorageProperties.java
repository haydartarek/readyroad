package com.readyroad.readyroadbackend.storage;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.media.storage")
public class MediaStorageProperties {

    private String provider = "local";
    private String endpoint = "";
    private String region = "eu-west-1";
    private String accessKey = "";
    private String secretKey = "";
    private String publicBucket = "rijvia-public";
    private String privateBucket = "rijvia-private";
    private String publicBaseUrl = "";
    private String localRootDirectory = "public/images";
    private Map<String, String> localPublicDirectories = new LinkedHashMap<>();
    private boolean pathStyleAccess = true;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPublicBucket() {
        return publicBucket;
    }

    public void setPublicBucket(String publicBucket) {
        this.publicBucket = publicBucket;
    }

    public String getPrivateBucket() {
        return privateBucket;
    }

    public void setPrivateBucket(String privateBucket) {
        this.privateBucket = privateBucket;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public String getLocalRootDirectory() {
        return localRootDirectory;
    }

    public void setLocalRootDirectory(String localRootDirectory) {
        this.localRootDirectory = localRootDirectory;
    }

    public Map<String, String> getLocalPublicDirectories() {
        return localPublicDirectories;
    }

    public void setLocalPublicDirectories(Map<String, String> localPublicDirectories) {
        this.localPublicDirectories = localPublicDirectories == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(localPublicDirectories);
    }

    public boolean isPathStyleAccess() {
        return pathStyleAccess;
    }

    public void setPathStyleAccess(boolean pathStyleAccess) {
        this.pathStyleAccess = pathStyleAccess;
    }

    void validateForS3() {
        if (!"s3".equalsIgnoreCase(provider)) {
            return;
        }

        StringBuilder missing = new StringBuilder();
        requireValue(endpoint, "STORAGE_ENDPOINT", missing);
        requireValue(region, "STORAGE_REGION", missing);
        requireValue(accessKey, "STORAGE_ACCESS_KEY", missing);
        requireValue(secretKey, "STORAGE_SECRET_KEY", missing);
        requireValue(publicBucket, "STORAGE_PUBLIC_BUCKET", missing);
        requireValue(privateBucket, "STORAGE_PRIVATE_BUCKET", missing);
        requireValue(publicBaseUrl, "STORAGE_PUBLIC_BASE_URL", missing);

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "STORAGE_PROVIDER=s3 requires: " + missing);
        }
        if (publicBucket.equals(privateBucket)) {
            throw new IllegalStateException(
                    "STORAGE_PUBLIC_BUCKET and STORAGE_PRIVATE_BUCKET must be different");
        }
    }

    private static void requireValue(String value, String name, StringBuilder missing) {
        if (value == null || value.isBlank()) {
            if (!missing.isEmpty()) {
                missing.append(", ");
            }
            missing.append(name);
        }
    }
}
