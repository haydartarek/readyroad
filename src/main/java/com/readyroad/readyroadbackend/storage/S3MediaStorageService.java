package com.readyroad.readyroadbackend.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@ConditionalOnProperty(
        prefix = "app.media.storage",
        name = "provider",
        havingValue = "s3")
public class S3MediaStorageService implements MediaStorageService {

    private final S3Client client;
    private final MediaStorageProperties properties;
    private final String publicBaseUrl;

    public S3MediaStorageService(S3Client client, MediaStorageProperties properties) {
        this.client = client;
        this.properties = properties;
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

        try {
            client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName(bucket))
                            .key(MediaStorageKeys.normalize(key))
                            .contentLength(contentLength)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromInputStream(content, contentLength));
        } catch (RuntimeException exception) {
            throw new IOException("Unable to store media in object storage", exception);
        }
    }

    @Override
    public InputStream open(MediaStorageBucket bucket, String key) throws IOException {
        try {
            ResponseInputStream<?> response = client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName(bucket))
                    .key(MediaStorageKeys.normalize(key))
                    .build());
            return response;
        } catch (RuntimeException exception) {
            throw new IOException("Unable to open media from object storage", exception);
        }
    }

    @Override
    public boolean exists(MediaStorageBucket bucket, String key) {
        try {
            client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName(bucket))
                    .key(MediaStorageKeys.normalize(key))
                    .build());
            return true;
        } catch (NoSuchKeyException exception) {
            return false;
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                return false;
            }
            throw exception;
        }
    }

    @Override
    public boolean delete(MediaStorageBucket bucket, String key) throws IOException {
        try {
            client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName(bucket))
                    .key(MediaStorageKeys.normalize(key))
                    .build());
            return true;
        } catch (RuntimeException exception) {
            throw new IOException("Unable to delete media from object storage", exception);
        }
    }

    @Override
    public String resolveUrl(MediaStorageBucket bucket, String key) {
        if (bucket != MediaStorageBucket.PUBLIC) {
            throw new IllegalArgumentException("Private media cannot resolve to a public URL");
        }
        String normalized = MediaStorageKeys.normalize(key);
        if (!publicBaseUrl.isBlank()) {
            return publicBaseUrl + "/" + encodePath(normalized);
        }
        return "s3://" + bucketName(MediaStorageBucket.PUBLIC) + "/" + normalized;
    }

    private String bucketName(MediaStorageBucket bucket) {
        if (bucket == null) {
            throw new IllegalArgumentException("Media storage bucket is required");
        }
        String value = bucket == MediaStorageBucket.PUBLIC
                ? properties.getPublicBucket()
                : properties.getPrivateBucket();
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("S3 " + bucket + " bucket is not configured");
        }
        return value;
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
            segments[index] = URLEncoder.encode(segments[index], StandardCharsets.UTF_8)
                    .replace("+", "%20");
        }
        return String.join("/", segments);
    }
}
