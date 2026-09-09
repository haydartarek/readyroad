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
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.sql.DataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
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
    @Autowired WebApplicationContext context;
    @Autowired ObjectMapper objectMapper;

    private JdbcTemplate jdbc;

    @BeforeEach
    void resetData() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("""
                TRUNCATE article_refresh_recommendations, article_performance_snapshots,
                         article_publications, article_image_localizations,
                         article_image_variants, article_image_assets, article_versions, articles
                RESTART IDENTITY
                """);
        jdbc.update("DELETE FROM audit_logs WHERE event_type IN (?, ?)",
                EditorialArticleImageService.AUDIT_EVENT,
                EditorialArticleImageService.REMOVE_AUDIT_EVENT);
    }

    @Test
    void storesLocalUploadWithResponsiveVariantsAndLocalizedAltText() throws Exception {
        long articleId = imageRequiredArticle(1, "priority-from-right");

        var asset = service.upload(articleId, image("source-a"), metadata("source-a"), "admin@rijvia.be");

        assertThat(asset.status()).isEqualTo("APPROVED");
        assertThat(asset.storedFileName()).isEqualTo("rijvia-en-source-a-hero");
        assertThat(asset.originalWidth()).isEqualTo(2048);
        assertThat(asset.originalHeight()).isEqualTo(1200);
        assertThat(asset.createdBy()).isEqualTo("admin@rijvia.be");
        assertThat(asset.variants()).extracting(EditorialArticleImageDtos.Variant::type)
                .containsExactlyInAnyOrder("HERO", "CARD", "MEDIUM", "MOBILE", "OG");
        assertThat(asset.variants()).allSatisfy(variant -> {
            assertThat(variant.publicPath()).startsWith("/images/articles/");
            assertThat(Files.size(publicFile(variant.publicPath()))).isEqualTo(variant.byteSize());
        });
        assertThat(asset.variants().stream()
                .filter(value -> value.type().equals("HERO"))
                .findFirst().orElseThrow().byteSize()).isLessThan(420_000);
        assertThat(asset.localizations()).extracting(EditorialArticleImageDtos.Localization::language)
                .containsExactly("AR", "NL", "FR", "EN");
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
    void migrationRemovesObsoleteLicensingCaptionAndFocalStructures() {
        assertThat(tableExists("article_image_licenses")).isFalse();
        assertThat(columnExists("article_image_assets", "focal_point_x")).isFalse();
        assertThat(columnExists("article_image_assets", "focal_point_y")).isFalse();
        assertThat(columnExists("article_image_localizations", "caption")).isFalse();
    }

    @Test
    void acceptsOwnerImageDimensionsWithoutUpscalingAndPersistsActualVariantSizes() throws Exception {
        long articleId = imageRequiredArticle(1, "owner-image");
        var asset = service.upload(articleId, image("owner-image", 1672, 941),
                metadata("owner-image"), "admin");

        assertThat(asset.originalWidth()).isEqualTo(1672);
        assertThat(asset.originalHeight()).isEqualTo(941);
        assertThat(asset.variants().stream().filter(variant -> variant.type().equals("HERO")).findFirst()).get()
                .satisfies(variant -> {
                    assertThat(variant.width()).isEqualTo(1600);
                    assertThat(variant.height()).isEqualTo(900);
                });
        assertThat(asset.variants()).hasSize(5).allSatisfy(variant -> {
            assertThat(variant.width()).isPositive().isLessThanOrEqualTo(1672);
            assertThat(variant.height()).isPositive().isLessThanOrEqualTo(941);
            var decoded = ImageIO.read(publicFile(variant.publicPath()).toFile());
            assertThat(decoded.getWidth()).isEqualTo(variant.width());
            assertThat(decoded.getHeight()).isEqualTo(variant.height());
        });
        assertThat(store.requireApprovalReady(articleId).assetId()).isEqualTo(asset.id());
        assertThat(service.current(articleId)).get().isEqualTo(asset);
    }

    @ParameterizedTest
    @CsvSource({"1448,1086", "1536,1024", "1024,768", "800,450"})
    void acceptsSourceDimensionsIndependentlyOfGeneratedRenditions(int width, int height) throws Exception {
        long articleId = imageRequiredArticle(1, "local-source");
        var asset = service.upload(articleId, image("local-source", width, height),
                metadata("local-source"), "admin");

        assertThat(asset.originalWidth()).isEqualTo(width);
        assertThat(asset.originalHeight()).isEqualTo(height);
        assertThat(asset.variants()).hasSize(5).allSatisfy(variant -> {
            var decoded = ImageIO.read(publicFile(variant.publicPath()).toFile());
            assertThat(decoded.getWidth()).isEqualTo(variant.width());
            assertThat(decoded.getHeight()).isEqualTo(variant.height());
        });
        assertThat(store.requireApprovalReady(articleId).assetId()).isEqualTo(asset.id());
        assertThat(service.current(articleId)).contains(asset);
    }

    @Test
    void uploadsThroughAuthenticatedControllerAndExplainsDuplicateInArabic() throws Exception {
        long articleId = imageRequiredArticle(1, "owner-upload");
        var mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        var file = image("owner-upload", 1448, 1086);
        var metadataPart = new MockMultipartFile("metadata", "metadata.json", "application/json",
                objectMapper.writeValueAsBytes(metadata("owner-upload")));
        String url = "/api/admin/marketing/editorial/editor/articles/" + articleId + "/image";
        mvc.perform(multipart(url).file(file).file(metadataPart)
                .with(user("admin").roles("ADMIN")).header("Accept-Language", "ar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalWidth").value(1448))
                .andExpect(jsonPath("$.originalHeight").value(1086))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.variants.length()").value(5))
                .andExpect(jsonPath("$.localizations.length()").value(4));
        mvc.perform(get(url).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.originalWidth").value(1448));
        mvc.perform(multipart(url).file(file).file(metadataPart)
                .with(user("admin").roles("ADMIN")).header("Accept-Language", "ar"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("هذه الصورة مسجلة مسبقًا. اختر صورة مختلفة."));
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_image_assets", Integer.class)).isOne();
        assertThat(service.current(articleId)).isPresent();
    }

    @ParameterizedTest
    @ValueSource(strings = {"DRAFTING", "DRAFT_READY", "FACT_CHECK_REQUIRED", "LEGAL_REVIEW_REQUIRED", "TRANSLATION_REQUIRED"})
    void uploadsReplacesAndRemovesDraftImagesWithoutAdvancingOrPublishing(String state) throws Exception {
        long articleId = imageRequiredArticle(1, "draft-image");
        jdbc.update("UPDATE articles SET lifecycle_state = ? WHERE id = ?", state, articleId);
        var first = service.upload(articleId, image("draft-first"), metadata("draft-first"), "admin");
        var second = service.upload(articleId, image("draft-second"), metadata("draft-second"), "admin");
        assertThat(service.current(articleId)).get().extracting(EditorialArticleImageDtos.Asset::id)
                .isEqualTo(second.id());
        assertThat(second.localizations()).hasSize(4);
        assertThat(second.variants()).hasSize(5);
        service.remove(articleId, "admin");
        assertThat(service.current(articleId)).isEmpty();
        assertThat(jdbc.queryForList("SELECT status FROM article_image_assets WHERE id IN (?, ?)",
                String.class, first.id(), second.id())).containsOnly("SUPERSEDED");
        assertThat(jdbc.queryForObject("SELECT lifecycle_state FROM articles WHERE id = ?",
                String.class, articleId)).isEqualTo(state);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_publications", Integer.class)).isZero();
        assertThat(store.publicImage(first.id(), "EN")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"WAITING_APPROVAL", "APPROVED", "SCHEDULED", "PUBLISHED", "ARCHIVED", "REJECTED"})
    void rejectsImageChangesAfterEditorialLock(String state) throws Exception {
        long articleId = imageRequiredArticle(1, "locked-image");
        var original = service.upload(articleId, image("locked-first"), metadata("locked-first"), "admin");
        jdbc.update("UPDATE articles SET lifecycle_state = ? WHERE id = ?", state, articleId);
        assertThatThrownBy(() -> service.upload(articleId, image("locked-second"), metadata("locked-second"), "admin"))
                .isInstanceOf(ResponseStatusException.class).hasMessageContaining("409 CONFLICT");
        assertThatThrownBy(() -> service.remove(articleId, "admin"))
                .isInstanceOf(ResponseStatusException.class).hasMessageContaining("409 CONFLICT");
        assertThat(service.current(articleId)).get().extracting(EditorialArticleImageDtos.Asset::id)
                .isEqualTo(original.id());
    }

    @Test
    void preventsDuplicateBinarySourcesAndKeepsExistingApprovedAsset() throws Exception {
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
    void replacesImageOnlyInsideImageRequiredAndKeepsAssetAuditHistory() throws Exception {
        long articleId = imageRequiredArticle(1, "replaceable-article");
        var first = service.upload(articleId, image("source-first"), metadata("source-first"), "admin");
        var replacement = service.upload(articleId, image("source-second"), metadata("source-second"), "admin");

        assertThat(replacement.id()).isNotEqualTo(first.id());
        assertThat(jdbc.queryForObject(
                "SELECT status FROM article_image_assets WHERE id = ?",
                String.class,
                first.id())).isEqualTo("SUPERSEDED");
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_image_assets WHERE article_id = ?",
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
    void removesCurrentImageBeforeApprovalWithoutDeletingAssetAuditHistory() throws Exception {
        long articleId = imageRequiredArticle(1, "removable-article");
        var asset = service.upload(articleId, image("source-remove"), metadata("source-remove"), "admin");

        service.remove(articleId, "admin");

        assertThat(service.current(articleId)).isEmpty();
        assertThat(jdbc.queryForObject(
                "SELECT status FROM article_image_assets WHERE id = ?",
                String.class,
                asset.id())).isEqualTo("SUPERSEDED");
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_image_assets WHERE id = ?",
                Integer.class,
                asset.id())).isOne();
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM audit_logs
                WHERE event_type = ? AND entity_id = ?
                """, Integer.class, EditorialArticleImageService.REMOVE_AUDIT_EVENT,
                String.valueOf(asset.id()))).isOne();
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbc.queryForObject("""
                SELECT count(*) FROM information_schema.tables
                WHERE table_schema = 'public' AND table_name = ?
                """, Integer.class, tableName);
        return count != null && count > 0;
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbc.queryForObject("""
                SELECT count(*) FROM information_schema.columns
                WHERE table_schema = 'public' AND table_name = ? AND column_name = ?
                """, Integer.class, tableName, columnName);
        return count != null && count > 0;
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
                "تقاطع أولوية بلجيكي",
                "Een Belgisch voorrangskruispunt",
                "Un carrefour de priorité belge",
                "A Belgian priority junction");
    }

    private static MockMultipartFile image(String seed) throws Exception {
        return image(seed, 2048, 1200);
    }

    private static MockMultipartFile image(String seed, int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            int accent = Math.floorMod(seed.hashCode(), 180) + 40;
            graphics.setPaint(new GradientPaint(
                    0, 0, new Color(accent, 90, 120),
                    width, height, new Color(30, 130, accent)));
            graphics.fillRect(0, 0, width, height);
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
