package com.readyroad.readyroadbackend.storage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Storage abstraction for media files.
 *
 * <p>Keys are logical, provider-independent paths such as
 * {@code media/item-1.png}. Callers keep those keys in the database
 * and do not need to know whether the active provider is local disk or S3.
 */
public interface MediaStorageService {

    void put(MediaStorageBucket bucket, String key, InputStream content,
            long contentLength, String contentType)
            throws IOException;

    InputStream open(MediaStorageBucket bucket, String key) throws IOException;

    boolean exists(MediaStorageBucket bucket, String key);

    boolean delete(MediaStorageBucket bucket, String key) throws IOException;

    /**
     * Resolves a public URL. Private objects must never be resolved to a URL.
     */
    String resolveUrl(MediaStorageBucket bucket, String key);
}
