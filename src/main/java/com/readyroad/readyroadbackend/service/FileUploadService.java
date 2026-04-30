package com.readyroad.readyroadbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
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
}
