package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.storage.MediaStorageBucket;
import com.readyroad.readyroadbackend.storage.MediaStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Set;
import java.util.Locale;
import java.util.UUID;

/**
 * Service for handling secure file uploads.
 * Validates images and delegates persistence to MediaStorageService,
 * preserving the existing /images/** URL contract.
 */
@Slf4j
@Service
public class FileUploadService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp");

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "webp");

    @Value("${readyroad.upload.max-file-size-mb:5}")
    private int maxFileSizeMb;

    private final BackendMessageService messages;
    private final MediaStorageService mediaStorageService;

    public FileUploadService(BackendMessageService messages, MediaStorageService mediaStorageService) {
        this.messages = messages;
        this.mediaStorageService = mediaStorageService;
    }

    /**
     * Upload and validate an image file.
     *
     * @param file the multipart file from the request
     * @return the relative URL path to access the uploaded file (e.g.
     *         /images/quiz/abc123.png)
     */
    public String uploadImage(MultipartFile file) {
        return uploadImage(file, null);
    }

    public String uploadImage(
            MultipartFile file,
            String requestedBaseName) {

        return uploadValidatedImage(
                file,
                requestedBaseName,
                "quiz/");
    }

    public String uploadLessonImage(
            MultipartFile file,
            String lessonCode,
            String requestedBaseName) {

        if (lessonCode != null && (lessonCode.contains("..")
                || lessonCode.contains("/") || lessonCode.contains("\\")
                || lessonCode.contains(":") || lessonCode.indexOf('\0') >= 0)) {
            throw new SecurityException(messages.get("upload.invalid_path"));
        }

        String safeLessonCode =
                sanitizeBaseName(lessonCode);

        if (safeLessonCode.isBlank()) {
            throw new IllegalArgumentException(
                    "A valid lesson code is required");
        }

        return uploadValidatedImage(
                file,
                requestedBaseName,
                "lessons/"
                        + safeLessonCode
                        + "/");
    }

    private String uploadValidatedImage(
            MultipartFile file,
            String requestedBaseName,
            String storageKeyPrefix) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException(
                    messages.get(
                            "upload.file_empty"));
        }

        String contentType =
                file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(
                        contentType.toLowerCase())) {

            throw new IllegalArgumentException(
                    messages.get(
                            "upload.invalid_type",
                            contentType));
        }

        String originalFilename =
                file.getOriginalFilename();

        String extension =
                getExtension(
                        originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(
                extension.toLowerCase())) {

            throw new IllegalArgumentException(
                    messages.get(
                            "upload.invalid_extension",
                            extension));
        }

        String normalizedType =
                contentType.toLowerCase(
                        Locale.ROOT);

        String normalizedExtension =
                extension.toLowerCase(
                        Locale.ROOT);

        if (!matchesDeclaredType(
                normalizedType,
                normalizedExtension)
                || !hasValidImageSignature(
                        file,
                        normalizedType)) {

            throw new IllegalArgumentException(
                    messages.get(
                            "upload.unreadable_image"));
        }

        long maxBytes =
                (long) maxFileSizeMb
                        * 1024
                        * 1024;

        if (file.getSize() > maxBytes) {

            throw new IllegalArgumentException(
                    messages.get(
                            "upload.file_too_large",
                            file.getSize()
                                    / 1024
                                    / 1024,
                            maxFileSizeMb));
        }

        String safeBaseName =
                sanitizeBaseName(
                        requestedBaseName);

        String uniqueSuffix =
                UUID.randomUUID()
                        .toString()
                        .substring(0, 12);

        String uniqueName =
                (
                        safeBaseName.isBlank()
                                ? uniqueSuffix
                                : safeBaseName
                                        + "-"
                                        + uniqueSuffix
                )
                        + "."
                        + normalizedExtension;

        String storageKey = storageKeyPrefix + uniqueName;

        try (InputStream input = file.getInputStream()) {
            mediaStorageService.put(
                    MediaStorageBucket.PUBLIC,
                    storageKey,
                    input,
                    file.getSize(),
                    normalizedType);
        } catch (IOException exception) {

            throw new RuntimeException(
                    messages.get(
                            "upload.store_failed"),
                    exception);
        }

        return "/images/" + storageKey;
    }

    static String sanitizeBaseName(String requestedBaseName) {
        if (requestedBaseName == null || requestedBaseName.isBlank()) {
            return "";
        }

        String name = requestedBaseName.trim();
        String lowerName = name.toLowerCase(Locale.ROOT);
        for (String extension : ALLOWED_EXTENSIONS) {
            String suffix = "." + extension;
            if (lowerName.endsWith(suffix) && name.length() > suffix.length()) {
                name = name.substring(0, name.length() - suffix.length());
                break;
            }
        }

        String normalized = Normalizer.normalize(name, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", "-")
                .replaceAll("^-+|-+$", "");
        if (normalized.length() > 80) {
            normalized = normalized.substring(0, 80).replaceAll("-+$", "");
        }
        return normalized;
    }

    /**
     * Delete an uploaded image file by its URL path.
     *
     * @param imageUrl the URL path (e.g. /images/quiz/abc123.png)
     * @return true if file was deleted, false if not found
     */
    public boolean deleteImage(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/images/quiz/")) {
            return false;
        }

        String filename = imageUrl.substring("/images/quiz/".length());

        // Security: prevent path traversal
        if (filename.isBlank() || filename.equals(".") || filename.contains("..")
                || filename.contains("/") || filename.contains("\\")
                || filename.contains(":") || filename.indexOf('\0') >= 0) {
            log.warn("⚠️ Suspicious filename in delete request: {}", filename);
            return false;
        }

        try {
            boolean deleted = mediaStorageService.delete(MediaStorageBucket.PUBLIC, "quiz/" + filename);
            if (deleted) {
                log.info("🗑️ Image deleted: {}", filename);
            }
            return deleted;
        } catch (IOException e) {
            log.error("❌ Failed to delete file: {}", filename, e);
            return false;
        }
    }

    /**
     * Permanently removes a lesson image through the media storage abstraction.
     * The storage key is the value persisted in lesson_media_assets
     * (for example, lessons/les-30/page-1.png).
     */
    public boolean deleteLessonImage(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) {
            return false;
        }

        if (!storageKey.startsWith("lessons/") || storageKey.contains("..")
                || storageKey.contains("\\") || storageKey.contains(":")
                || storageKey.indexOf('\0') >= 0) {
            throw new SecurityException(messages.get("upload.invalid_path"));
        }

        for (String segment : storageKey.split("/", -1)) {
            if (segment.isBlank() || segment.equals(".")) {
                throw new SecurityException(messages.get("upload.invalid_path"));
            }
        }

        try {
            boolean deleted = mediaStorageService.delete(MediaStorageBucket.PUBLIC, storageKey);
            if (deleted) {
                log.info("Lesson image permanently deleted: {}", storageKey);
            }
            return deleted;
        } catch (IOException exception) {
            log.error("Failed to permanently delete lesson image: {}", storageKey, exception);
            throw new IllegalStateException("Unable to delete lesson image from storage", exception);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private boolean matchesDeclaredType(String contentType, String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> contentType.equals("image/jpeg") || contentType.equals("image/jpg");
            case "png" -> contentType.equals("image/png");
            case "webp" -> contentType.equals("image/webp");
            default -> false;
        };
    }

    private boolean hasValidImageSignature(MultipartFile file, String contentType) {
        try (InputStream input = file.getInputStream()) {
            byte[] header = input.readNBytes(12);
            if (contentType.equals("image/jpeg") || contentType.equals("image/jpg")) {
                return header.length >= 3 &&
                        (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;
            }
            if (contentType.equals("image/png")) {
                byte[] png = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
                if (header.length < png.length) return false;
                for (int index = 0; index < png.length; index++) {
                    if (header[index] != png[index]) return false;
                }
                return true;
            }
            return contentType.equals("image/webp") && header.length >= 12 &&
                    header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F' &&
                    header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
        } catch (IOException exception) {
            log.warn("Unreadable image upload rejected: {}", exception.getClass().getSimpleName());
            return false;
        }
    }
}
