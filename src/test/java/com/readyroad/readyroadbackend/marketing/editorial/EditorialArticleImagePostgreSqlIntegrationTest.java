package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("postgresql")
@Testcontainers
class EditorialArticleImagePostgreSqlIntegrationTest {

    private static final Path IMAGE_DIRECTORY = Path.of(
            "target", "editorial-image-test-" + UUID.randomUUID()).toAbsolutePath();

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add("readyroad.marketing.enabled", () -> "false");
        registry.add("rijvia.editorial.images.directory", () -> IMAGE_DIRECTORY.toString());
        registry.add("jwt.secret-key",
                () -> "ZWRpdG9yaWFsLWltYWdlLXRlc3Qta2V5LW5vdC1mb3ItcHJvZHVjdGlvbg==");
        registry.add("readyroad.admin.default-password", () -> "Image-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired EditorialArticleImageService service;
    @Autowired EditorialArticleImageStore store;

    private JdbcTemplate jdbc;

    @BeforeEach
    void resetData() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("""
                TRUNCATE article_refresh_recommendations, article_performance_snapshots,
                         article_publications, article_image_licenses, article_image_localizations,
                         article_image_variants, article_image_assets, article_versions, articles
                RESTART IDENTITY
                """);
        jdbc.update("DELETE FROM audit_logs WHERE event_type = ?", EditorialArticleImageService.AUDIT_EVENT);
    }

    @Test
    void storesAConfirmedLocalUploadWithFiveResponsiveVariantsAndALicenseRecord() throws Exception {
        long articleId = imageRequiredArticle(1, "priority-from-right");

        var asset = service.upload(articleId, image("source-a"), metadata("source-a"), "admin@rijvia.be");

        assertThat(asset.status()).isEqualTo("APPROVED");
        assertThat(asset.storedFileName()).isEqualTo("rijvia-en-source-a-hero");
        assertThat(asset.originalWidth()).isEqualTo(2048);
        assertThat(asset.originalHeight()).isEqualTo(1200);
        assertThat(asset.variants()).extracting(EditorialArticleImageDtos.Variant::type)
                .containsExactlyInAnyOrder("HERO", "CARD", "MEDIUM", "MOBILE", "OG");
        assertThat(asset.variants()).allSatisfy(variant -> {
            assertThat(variant.publicPath()).startsWith("/images/articles/");
            assertThat(Files.size(publicFile(variant.publicPath()))).isEqualTo(variant.byteSize());
        });
        assertThat(asset.variants().stream().filter(value -> value.type().equals("HERO")).findFirst().orElseThrow().byteSize())
                .isLessThan(420_000);
        assertThat(asset.localizations()).extracting(EditorialArticleImageDtos.Localization::language)
                .containsExactly("AR", "NL", "FR", "EN");
        assertThat(asset.license().sourcePlatform()).isEqualTo("LOCAL_UPLOAD");
        assertThat(asset.license().sourceAssetId()).matches("[0-9a-f]{64}");
        assertThat(asset.license().photographerName()).isEqualTo("RijVia owner upload");
        assertThat(asset.license().approvedBy()).isEqualTo("admin@rijvia.be");
        assertThat(store.requireApprovalReady(articleId).assetId()).isEqualTo(asset.id());
        assertThat(store.publicImage(asset.id(), "EN")).get()
                .extracting(EditorialArticleImageDtos.PublicImage::altText)
                .isEqualTo("A Belgian priority junction");
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM audit_logs
                WHERE event_type = ? AND entity_id = ?
                """, Integer.class, EditorialArticleImageService.AUDIT_EVENT, String.valueOf(asset.id())))
                .isOne();
    }

    @Test
    void preventsDuplicateSourcesAndKeepsTheExistingApprovedAsset() throws Exception {
        long firstArticle = imageRequiredArticle(1, "first-article");
        long secondArticle = imageRequiredArticle(2, "second-article");
        var first = service.upload(firstArticle, image("same-source"), metadata("same-source"), "admin");

        assertThatThrownBy(() -> service.upload(
                secondArticle,
                image("same-source"),
                metadata("same-source"),
                "admin"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");

        assertThat(service.current(firstArticle)).get()
                .extracting(EditorialArticleImageDtos.Asset::id)
                .isEqualTo(first.id());
        assertThat(service.current(secondArticle)).isEmpty();
    }

    @Test
    void replacesAnImageOnlyInsideImageRequiredAndRetainsTheLicenseHistory() throws Exception {
        long articleId = imageRequiredArticle(1, "replaceable-article");
        var first = service.upload(articleId, image("source-first"), metadata("source-first"), "admin");
        var replacement = service.upload(articleId, image("source-second"), metadata("source-second"), "admin");

        assertThat(replacement.id()).isNotEqualTo(first.id());
        assertThat(jdbc.queryForObject(
                "SELECT status FROM article_image_assets WHERE id = ?",
                String.class,
                first.id())).isEqualTo("SUPERSEDED");
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_image_licenses WHERE article_id = ?",
                Integer.class,
                articleId)).isEqualTo(2);

        jdbc.update("UPDATE articles SET lifecycle_state = 'WAITING_APPROVAL' WHERE id = ?", articleId);
        assertThatThrownBy(() -> service.upload(
                articleId,
                image("source-third"),
                metadata("source-third"),
                "admin"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");
    }

    @Test
    void removesTheCurrentImageBeforeApprovalWithoutDeletingItsAuditHistory() throws Exception {
        long articleId = imageRequiredArticle(1, "removable-article");
        var asset = service.upload(articleId, image("source-remove"), metadata("source-remove"), "admin");

        service.remove(articleId, "admin");

        assertThat(service.current(articleId)).isEmpty();
        assertThat(jdbc.queryForObject(
                "SELECT status FROM article_image_assets WHERE id = ?",
                String.class,
                asset.id())).isEqualTo("SUPERSEDED");
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_image_licenses WHERE image_asset_id = ?",
                Integer.class,
                asset.id())).isOne();
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM audit_logs
                WHERE event_type = ? AND entity_id = ?
                """, Integer.class, EditorialArticleImageService.REMOVE_AUDIT_EVENT,
                String.valueOf(asset.id()))).isOne();
    }

    private long imageRequiredArticle(long topicId, String canonicalKey) {
        return jdbc.queryForObject("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language
                ) VALUES (?, ?, 'IMAGE_REQUIRED', 'EN')
                RETURNING id
                """, Long.class, topicId, canonicalKey);
    }

    private static EditorialArticleImageDtos.UploadMetadata metadata(String sourceAssetId) {
        return new EditorialArticleImageDtos.UploadMetadata(
                "rijvia-en-" + sourceAssetId + "-hero",
                "RijVia owner upload",
                "https://rijvia.be/image-sources/" + sourceAssetId,
                "Owner-approved local file",
                null,
                "Usage rights and relevance verified by the administrator",
                true,
                "تقاطع أولوية بلجيكي",
                "Een Belgisch voorrangskruispunt",
                "Un carrefour de priorité belge",
                "A Belgian priority junction",
                null,
                null,
                null,
                null,
                0.5,
                0.5
        );
    }

    private static MockMultipartFile image(String seed) throws Exception {
        BufferedImage image = new BufferedImage(2048, 1200, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            int accent = Math.floorMod(seed.hashCode(), 180) + 40;
            graphics.setPaint(new GradientPaint(
                    0, 0, new Color(accent, 90, 120),
                    2048, 1200, new Color(30, 130, accent)));
            graphics.fillRect(0, 0, 2048, 1200);
            graphics.setColor(Color.WHITE);
            graphics.fillRect(900, 0, 240, 1200);
        } finally {
            graphics.dispose();
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", bytes);
        return new MockMultipartFile("file", seed + ".jpg", "image/jpeg", bytes.toByteArray());
    }

    private static Path publicFile(String publicPath) {
        String relative = publicPath.substring("/images/articles/".length());
        return IMAGE_DIRECTORY.resolve("optimized").resolve(relative);
    }
}
