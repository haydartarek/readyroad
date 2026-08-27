package com.readyroad.readyroadbackend.marketing.editorial;

import jakarta.annotation.PostConstruct;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.springframework.beans.factory.annotation.Value;
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

    private final Path storageRoot;

    EditorialArticleImageProcessor(
            @Value("${rijvia.editorial.images.directory:data/editorial-images}") String storageDirectory) {
        this.storageRoot = Path.of(storageDirectory).toAbsolutePath().normalize();
    }

    @PostConstruct
    void initialize() {
        try {
            Files.createDirectories(storageRoot.resolve("archive"));
            Files.createDirectories(storageRoot.resolve("optimized"));
        } catch (IOException error) {
            throw new IllegalStateException("Unable to initialize editorial image storage", error);
        }
    }

    Processed process(
            MultipartFile file,
            String storedFileName,
            String contentType,
            double focalPointX,
            double focalPointY) {
        String storageKey = UUID.randomUUID().toString().replace("-", "");
        Path archiveDirectory = storageRoot.resolve("archive").resolve(storageKey).normalize();
        Path optimizedDirectory = storageRoot.resolve("optimized").resolve(storageKey).normalize();
        requireInsideStorage(archiveDirectory);
        requireInsideStorage(optimizedDirectory);

        try {
            byte[] sourceBytes = file.getBytes();
            verifySignature(sourceBytes, contentType);
            BufferedImage source = ImageIO.read(new ByteArrayInputStream(sourceBytes));
            if (source == null) {
                throw new IllegalArgumentException("The uploaded article image cannot be decoded");
            }
            if (source.getWidth() < 1920 || source.getHeight() < 1080) {
                throw new IllegalArgumentException("Article source images must be at least 1920 x 1080 pixels");
            }

            Files.createDirectories(archiveDirectory);
            Files.createDirectories(optimizedDirectory);
            String extension = "image/png".equals(contentType) ? "png" : "jpg";
            Path original = archiveDirectory.resolve("original." + extension);
            Files.write(original, sourceBytes, StandardOpenOption.CREATE_NEW);

            String hash = sha256(sourceBytes);
            String seoName = seoFileName(storedFileName);
            List<ProcessedVariant> variants = VARIANTS.stream()
                    .map(spec -> writeVariant(
                            source,
                            optimizedDirectory,
                            storageKey,
                            seoName,
                            spec,
                            focalPointX,
                            focalPointY))
                    .toList();
            return new Processed(
                    storageKey,
                    hash,
                    storageRoot.relativize(original).toString().replace('\\', '/'),
                    source.getWidth(),
                    source.getHeight(),
                    variants);
        } catch (RuntimeException | IOException error) {
            deleteStorageKey(storageKey);
            if (error instanceof IllegalArgumentException invalid) {
                throw invalid;
            }
            throw new IllegalStateException("Unable to process the editorial article image", error);
        }
    }

    void delete(Processed processed) {
        if (processed != null) {
            deleteStorageKey(processed.storageKey());
        }
    }

    private ProcessedVariant writeVariant(
            BufferedImage source,
            Path directory,
            String storageKey,
            String seoName,
            VariantSpec spec,
            double focalPointX,
            double focalPointY) {
        BufferedImage rendered = render(source, spec.width(), spec.height(), focalPointX, focalPointY);
        byte[] bytes = encodeJpegWithinBudget(rendered, spec.maxBytes());
        String fileName = seoName + "-" + spec.type().toLowerCase(Locale.ROOT) + ".jpg";
        Path output = directory.resolve(fileName).normalize();
        requireInsideStorage(output);
        try {
            Files.write(output, bytes, StandardOpenOption.CREATE_NEW);
        } catch (IOException error) {
            throw new IllegalStateException("Unable to store optimized article image variant", error);
        }
        return new ProcessedVariant(
                spec.type(),
                "JPEG",
                "/images/articles/" + storageKey + "/" + fileName,
                spec.width(),
                spec.height(),
                bytes.length);
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

    private static byte[] encodeJpegWithinBudget(BufferedImage image, int maxBytes) {
        for (float quality = 0.86f; quality >= 0.38f; quality -= 0.04f) {
            byte[] encoded = encodeJpeg(image, quality);
            if (encoded.length < maxBytes) {
                return encoded;
            }
        }
        throw new IllegalArgumentException("The optimized article image cannot meet its byte budget");
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

    private static void verifySignature(byte[] bytes, String contentType) {
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
            throw new IllegalArgumentException("The article image signature does not match its content type");
        }
    }

    private void deleteStorageKey(String storageKey) {
        deleteTree(storageRoot.resolve("archive").resolve(storageKey));
        deleteTree(storageRoot.resolve("optimized").resolve(storageKey));
    }

    private static void deleteTree(Path root) {
        if (!Files.exists(root)) {
            return;
        }
        try (var paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Best-effort rollback cleanup; the database transaction remains authoritative.
                }
            });
        } catch (IOException ignored) {
            // Best-effort rollback cleanup; the database transaction remains authoritative.
        }
    }

    private void requireInsideStorage(Path path) {
        if (!path.normalize().startsWith(storageRoot)) {
            throw new SecurityException("Invalid editorial image storage path");
        }
    }

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
            List<ProcessedVariant> variants) {}

    record ProcessedVariant(
            String type,
            String format,
            String publicPath,
            int width,
            int height,
            int byteSize) {}

    private record VariantSpec(String type, int width, int height, int maxBytes) {}
}
