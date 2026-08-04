package com.readyroad.readyroadbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.Locale;
import java.util.UUID;

/**
 * Service for handling secure file uploads.
 * Validates file type and size, stores in the public/images directory
 * so files are served by the existing /images/** resource handler.
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

    @Value("${readyroad.upload.directory:public/images/quiz}")
    private String uploadDirectory;

    private final BackendMessageService messages;

    public FileUploadService(BackendMessageService messages) {
        this.messages = messages;
    }

    private Path uploadPath;

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
            log.info("📁 Upload directory ready: {}", uploadPath);
        } catch (IOException e) {
            log.error("❌ Could not create upload directory: {}", uploadPath, e);
            throw new RuntimeException(messages.get("upload.directory_create_failed"), e);
        }
    }

    /**
     * Upload and validate an image file.
     *
     * @param file the multipart file from the request
     * @return the relative URL path to access the uploaded file (e.g.
     *         /images/quiz/abc123.png)
     */
    public String uploadImage(MultipartFile file) {
        // Validate not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException(messages.get("upload.file_empty"));
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    messages.get("upload.invalid_type", contentType));
        }

        // Validate extension
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    messages.get("upload.invalid_extension", extension));
        }

        String normalizedType = contentType.toLowerCase(Locale.ROOT);
        String normalizedExtension = extension.toLowerCase(Locale.ROOT);
        if (!matchesDeclaredType(normalizedType, normalizedExtension) ||
                !hasValidImageSignature(file, normalizedType)) {
            throw new IllegalArgumentException(messages.get("upload.unreadable_image"));
        }

        // Validate file size
        long maxBytes = (long) maxFileSizeMb * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException(
                    messages.get("upload.file_too_large", file.getSize() / 1024 / 1024, maxFileSizeMb));
        }

        // Generate unique filename to prevent collisions and path traversal
        String uniqueName = UUID.randomUUID().toString() + "." + extension.toLowerCase();
        Path targetPath = uploadPath.resolve(uniqueName).normalize();

        // Security: ensure target is still within upload directory
        if (!targetPath.startsWith(uploadPath)) {
            throw new SecurityException(messages.get("upload.invalid_path"));
        }

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("✅ Image uploaded: {} ({} bytes)", uniqueName, file.getSize());
        } catch (IOException e) {
            log.error("❌ Failed to store file: {}", uniqueName, e);
            throw new RuntimeException(messages.get("upload.store_failed"), e);
        }

        // Return the URL path that matches the /images/** resource handler
        return "/images/quiz/" + uniqueName;
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
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            log.warn("⚠️ Suspicious filename in delete request: {}", filename);
            return false;
        }

        Path filePath = uploadPath.resolve(filename).normalize();
        if (!filePath.startsWith(uploadPath)) {
            return false;
        }

        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("🗑️ Image deleted: {}", filename);
            }
            return deleted;
        } catch (IOException e) {
            log.error("❌ Failed to delete file: {}", filename, e);
            return false;
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
