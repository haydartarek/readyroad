package com.readyroad.readyroadbackend.marketing.editorial;

import com.readyroad.readyroadbackend.service.BackendMessageService;
import com.readyroad.readyroadbackend.storage.MediaStorageBucket;
import com.readyroad.readyroadbackend.storage.MediaStorageService;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
class EditorialArticleImageProcessor {

    private static final List<VariantSpec> VARIANTS = List.of(
            new VariantSpec("HERO", 1920, 1080, 420_000),
            new VariantSpec("CARD", 1200, 675, 260_000),
            new VariantSpec("MEDIUM", 800, 450, 143_360),
            new VariantSpec("MOBILE", 480, 270, 81_920),
            new VariantSpec("OG", 1200, 630, 307_200));

    private final MediaStorageService storage;
    private final BackendMessageService messages;

    EditorialArticleImageProcessor(
            MediaStorageService storage,
            BackendMessageService messages) {
        this.storage = storage;
        this.messages = messages;
    }

    Processed process(
            MultipartFile file,
            String storedFileName,
            String contentType,
            double focalPointX,
            double focalPointY) {
        String storageKey = UUID.randomUUID().toString().replace("-", "");
        List<StoredObject> storedObjects = new ArrayList<>();

        try {
            byte[] sourceBytes = file.getBytes();
            verifySignature(sourceBytes, contentType);
            BufferedImage source = ImageIO.read(new ByteArrayInputStream(sourceBytes));
            if (source == null) {
                throw new IllegalArgumentException(messages.get("upload.unreadable_image"));
            }

            String extension = "image/png".equals(contentType) ? "png" : "jpg";
            String originalKey = "originals/articles/" + storageKey + "/original." + extension;
            put(MediaStorageBucket.PRIVATE, originalKey, sourceBytes, contentType, storedObjects);

            String hash = sha256(sourceBytes);
            String seoName = seoFileName(storedFileName);
            List<ProcessedVariant> variants = VARIANTS.stream()
                    .map(spec -> writeVariant(
                            source,
                            storageKey,
                            seoName,
                            spec,
                            focalPointX,
                            focalPointY,
                            storedObjects))
                    .toList();
            return new Processed(
                    storageKey,
                    hash,
                    "archive/" + storageKey + "/original." + extension,
                    source.getWidth(),
                    source.getHeight(),
                    variants,
                    List.copyOf(storedObjects));
        } catch (RuntimeException | IOException error) {
            deleteObjects(storedObjects);
            if (error instanceof IllegalArgumentException invalid) {
                throw invalid;
            }
            throw new IllegalStateException("Unable to process the editorial article image", error);
        }
    }

    void delete(Processed processed) {
        if (processed != null) {
            deleteObjects(processed.storedObjects());
        }
    }

    private ProcessedVariant writeVariant(
            BufferedImage source,
            String storageKey,
            String seoName,
            VariantSpec spec,
            double focalPointX,
            double focalPointY,
            List<StoredObject> storedObjects) {
        int width = spec.width();
        int height = spec.height();
        // Reuse the smaller schema-approved renditions when the source cannot fill the larger one.
        if (source.getWidth() < width || source.getHeight() < height) {
            if (spec.type().equals("HERO")) {
                width = 1600;
                height = 900;
            } else if (spec.type().equals("CARD")) {
                width = 800;
                height = 450;
            }
        }
        BufferedImage rendered = render(source, width, height, focalPointX, focalPointY);
        byte[] bytes = encodeJpegWithinBudget(rendered, spec.maxBytes());
        String fileName = seoName + "-" + spec.type().toLowerCase(Locale.ROOT) + ".jpg";
        String key = "articles/" + storageKey + "/" + fileName;
        put(MediaStorageBucket.PUBLIC, key, bytes, "image/jpeg", storedObjects);
        return new ProcessedVariant(
                spec.type(),
                "JPEG",
                "/images/articles/" + storageKey + "/" + fileName,
                rendered.getWidth(),
                rendered.getHeight(),
                bytes.length);
    }

    private void put(
            MediaStorageBucket bucket,
            String key,
            byte[] bytes,
            String contentType,
            List<StoredObject> storedObjects) {
        try {
            // Track the attempted key as well: a provider may commit before reporting a failure.
            storedObjects.add(new StoredObject(bucket, key));
            storage.put(bucket, key, new ByteArrayInputStream(bytes), bytes.length, contentType);
        } catch (IOException error) {
            throw new IllegalStateException("Unable to store editorial article image", error);
        }
    }

    private static BufferedImage render(
            BufferedImage source,
            int targetWidth,
            int targetHeight,
            double focalPointX,
            double focalPointY) {
        double targetRatio = (double) targetWidth / targetHeight;
        int cropWidth = source.getWidth();
        int cropHeight = (int) Math.round(cropWidth / targetRatio);
        if (cropHeight > source.getHeight()) {
            cropHeight = source.getHeight();
            cropWidth = (int) Math.round(cropHeight * targetRatio);
        }
        int centerX = (int) Math.round(focalPointX * source.getWidth());
        int centerY = (int) Math.round(focalPointY * source.getHeight());
        int cropX = Math.max(0, Math.min(source.getWidth() - cropWidth, centerX - cropWidth / 2));
        int cropY = Math.max(0, Math.min(source.getHeight() - cropHeight, centerY - cropHeight / 2));

        BufferedImage output = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = output.createGraphics();
        try {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, targetWidth, targetHeight);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawImage(
                    source,
                    0,
                    0,
                    targetWidth,
                    targetHeight,
                    cropX,
                    cropY,
                    cropX + cropWidth,
                    cropY + cropHeight,
                    null);
        } finally {
            graphics.dispose();
        }
        return output;
    }

    private byte[] encodeJpegWithinBudget(BufferedImage image, int maxBytes) {
        for (float quality = 0.86f; quality >= 0.38f; quality -= 0.04f) {
            byte[] encoded = encodeJpeg(image, quality);
            if (encoded.length < maxBytes) {
                return encoded;
            }
        }
        throw new IllegalArgumentException(messages.get("editorial.image.optimization_failed"));
    }

    private static byte[] encodeJpeg(BufferedImage image, float quality) {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ImageOutputStream output = ImageIO.createImageOutputStream(bytes)) {
            writer.setOutput(output);
            ImageWriteParam parameters = writer.getDefaultWriteParam();
            parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            parameters.setCompressionQuality(quality);
            writer.write(null, new IIOImage(image, null, null), parameters);
            return bytes.toByteArray();
        } catch (IOException error) {
            throw new IllegalStateException("Unable to encode optimized article image", error);
        } finally {
            writer.dispose();
        }
    }

    private void verifySignature(byte[] bytes, String contentType) {
        boolean jpeg = bytes.length >= 3
                && (bytes[0] & 0xff) == 0xff
                && (bytes[1] & 0xff) == 0xd8
                && (bytes[2] & 0xff) == 0xff;
        boolean png = bytes.length >= 8
                && (bytes[0] & 0xff) == 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4e
                && bytes[3] == 0x47
                && bytes[4] == 0x0d
                && bytes[5] == 0x0a
                && bytes[6] == 0x1a
                && bytes[7] == 0x0a;
        if (("image/jpeg".equals(contentType) && !jpeg)
                || ("image/png".equals(contentType) && !png)) {
            throw new IllegalArgumentException(messages.get("upload.unreadable_image"));
        }
    }

    private void deleteObjects(List<StoredObject> storedObjects) {
        if (storedObjects == null) {
            return;
        }
        storedObjects.forEach(object -> {
            try {
                storage.delete(object.bucket(), object.key());
            } catch (IOException | RuntimeException ignored) {
                // Best-effort rollback cleanup; the database transaction remains authoritative.
            }
        });
    }

    private record StoredObject(MediaStorageBucket bucket, String key) {}

    private static String sha256(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 is not available", impossible);
        }
    }

    private static String seoFileName(String value) {
        String normalized = value == null ? "article" : value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        if (normalized.isBlank()) {
            normalized = "article";
        }
        return normalized.length() > 96 ? normalized.substring(0, 96).replaceAll("-+$", "") : normalized;
    }

    record Processed(
            String storageKey,
            String sha256,
            String originalStoragePath,
            int originalWidth,
            int originalHeight,
            List<ProcessedVariant> variants,
            List<StoredObject> storedObjects) {}

    record ProcessedVariant(
            String type,
            String format,
            String publicPath,
            int width,
            int height,
            int byteSize) {}

    private record VariantSpec(String type, int width, int height, int maxBytes) {}
}
