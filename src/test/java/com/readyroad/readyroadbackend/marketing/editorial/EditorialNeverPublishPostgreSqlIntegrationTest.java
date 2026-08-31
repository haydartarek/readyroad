package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.readyroad.readyroadbackend.marketing.content.ContentGenerationClient;
import com.readyroad.readyroadbackend.marketing.content.ContentLocale;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextRequest;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("postgresql")
@Testcontainers
@Import(EditorialNeverPublishPostgreSqlIntegrationTest.E2EConfiguration.class)
class EditorialNeverPublishPostgreSqlIntegrationTest {

    private static final Path IMAGE_DIRECTORY = Path.of(
            "target",
            "editorial-never-publish-" + UUID.randomUUID())
            .toAbsolutePath();

    @Container
    static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);

        registry.add("readyroad.marketing.enabled", () -> "false");
        registry.add(
                "readyroad.marketing.content.api-key",
                () -> "editorial-never-publish-test-key");

        registry.add(
                "rijvia.editorial.images.directory",
                () -> IMAGE_DIRECTORY.toString());

        registry.add(
                "jwt.secret-key",
                () -> "ZWRpdG9yaWFsLW5ldmVyLXB1Ymxpc2gtdGVzdC1rZXktbm90LXByb2R1Y3Rpb24=");

        registry.add(
                "readyroad.admin.default-password",
                () -> "Never-Publish-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;

    @Autowired EditorialBriefService briefService;
    @Autowired EditorialBriefTaskHandler briefHandler;

    @Autowired EditorialDraftService draftService;
    @Autowired EditorialDraftTaskHandler draftHandler;

    @Autowired EditorialEditorService editorService;

    @Autowired EditorialTranslationService translationService;
    @Autowired EditorialTranslationTaskHandler translationHandler;

    @Autowired EditorialArticleImageService imageService;

    @Autowired EditorialArticleApprovalService approvalRequestService;

    @Autowired AgentTaskRepository taskRepository;

    @Autowired AtomicInteger generationCalls;
    @Autowired TranslationStub translationStub;

    @Autowired WebApplicationContext webApplicationContext;

    private JdbcTemplate jdbc;
    private MockMvc mockMvc;

    @BeforeEach
    void reset() {
        jdbc = new JdbcTemplate(dataSource);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        jdbc.update("""
                DELETE FROM agent_approvals
                WHERE task_id IN (
                    SELECT id
                    FROM agent_tasks
                    WHERE task_type IN (
                        'ARTICLE_BRIEF_CREATE',
                        'ARTICLE_DRAFT_CREATE',
                        'ARTICLE_TRANSLATION_ADAPT',
                        'ARTICLE_APPROVAL',
                        'ARTICLE_PUBLISH'
                    )
                )
                """);

        jdbc.update("""
                DELETE FROM agent_tasks
                WHERE task_type IN (
                    'ARTICLE_BRIEF_CREATE',
                    'ARTICLE_DRAFT_CREATE',
                    'ARTICLE_TRANSLATION_ADAPT',
                    'ARTICLE_APPROVAL',
                    'ARTICLE_PUBLISH'
                )
                """);

        jdbc.execute("""
                TRUNCATE
                    article_refresh_recommendations,
                    article_performance_snapshots,
                    article_publications,
                    article_image_localizations,
                    article_image_variants,
                    article_image_assets,
                    article_versions,
                    article_briefs,
                    articles
                RESTART IDENTITY
                """);

        jdbc.update("DELETE FROM editorial_claim_sources");
        jdbc.update("DELETE FROM editorial_claims");
        jdbc.update("DELETE FROM editorial_source_versions");
        jdbc.update("DELETE FROM editorial_sources");

        jdbc.update("""
                DELETE FROM audit_logs
                WHERE entity_type = 'EDITORIAL_ARTICLE'
                   OR event_type LIKE 'EDITORIAL_ARTICLE_%'
                   OR event_type = 'ARTICLE_PUBLISHED'
                """);

        jdbc.update("""
                UPDATE article_topics
                SET status = 'PLANNED',
                    primary_language = NULL,
                    usp_id = NULL,
                    icp_id = NULL,
                    content_pillar_id = NULL,
                    funnel_stage_id = NULL,
                    conversion_goal_id = NULL
                """);

        generationCalls.set(0);
        translationStub.reset();
    }

    @Test
    void completesTheOwnerArticleFlowAndStopsAtWaitingApprovalWithoutPublishing()
            throws Exception {

        /*
         * 1. Official backlog exists.
         */
        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_topics
                WHERE official_backlog_order = 2
                """, Integer.class))
                .isOne();

        /*
         * 2. Brief through the real brief service/handler.
         */
        var briefResponse = briefService.request(
                2,
                briefRequest(strategy()),
                "never-publish-owner");

        AgentTask briefTask =
                taskRepository.findById(briefResponse.id()).orElseThrow();

        briefHandler.execute(claimed(briefTask));

        long articleId = jdbc.queryForObject("""
                SELECT id
                FROM articles
                WHERE article_topic_id = 2
                """, Long.class);

        assertThat(state(articleId)).isEqualTo("BRIEF_READY");

        /*
         * 3. Real verified evidence package.
         */
        insertSupportedCoreClaim(2);

        /*
         * 4. Draft request through the real draft generator.
         */
        var draftResponse = draftService.request(
                articleId,
                new EditorialDraftDtos.CreateRequest(
                        "never-publish-source-grounded-draft"),
                "never-publish-owner");

        AgentTask draftTask =
                taskRepository.findById(draftResponse.id()).orElseThrow();

        draftHandler.execute(claimed(draftTask));

        assertThat(state(articleId)).isEqualTo("DRAFT_READY");
        assertThat(generationCalls).hasValue(1);

        var generatedHistory = editorService.versions(articleId, "AR");

        assertThat(generatedHistory).hasSize(1);
        assertThat(generatedHistory.getFirst().status())
                .isEqualTo("DRAFT_READY");

        /*
         * 5. Owner manually edits/saves the canonical article.
         *    This also exercises SEO metadata, internal links and typography.
         */
        var typography = new EditorialEditorDtos.Typography(
                "LARGE",
                "DEFAULT",
                "COMPACT",
                "DEFAULT",
                "LARGE",
                "SECONDARY");

        String manualBody =
                String.join(" ", Collections.nCopies(
                        450,
                        "?????? ????? ??????? ??? ?????? ??????? ?????? ?? ??????"));

        var saved = editorService.save(
                2,
                "AR",
                new EditorialEditorDtos.SaveRequest(
                        "???? RijVia ?????? ??????? ???????? ??????",
                        "never-publish-ar",
                        "???? ???? ??? ??????? ??? ??? ???????",
                        manualBody,
                        "???? ???????? ?????? | RijVia",
                        "??? ???? ?????? ??????? ??????? ??????? ?????? ?? ??????.",
                        List.of(new EditorialInternalLinkDtos.Input(
                                "/ar/exam",
                                "???? ?????? ??????? ??????")),
                        typography,
                        1),
                "never-publish-owner");

        assertThat(saved.created()).isTrue();
        assertThat(saved.articleCreated()).isFalse();
        assertThat(saved.version().versionNumber()).isEqualTo(2);
        assertThat(saved.version().status()).isEqualTo("DRAFT");
        assertThat(saved.version().slug()).isEqualTo("never-publish-ar");
        assertThat(saved.version().typography()).isEqualTo(typography);

        assertThat(saved.version().internalLinks())
                .containsExactly(new EditorialInternalLinkDtos.Link(
                        "EXAM",
                        "/ar/exam",
                        "???? ?????? ??????? ??????"));

        /*
         * History / preview contract:
         * current manually-edited version plus immutable generated predecessor.
         */
        var history = editorService.versions(articleId, "AR");

        assertThat(history)
                .extracting(EditorialEditorDtos.Version::versionNumber)
                .containsExactly(2, 1);

        assertThat(history.getFirst().body()).isEqualTo(manualBody);
        assertThat(history.getFirst().metaTitle())
                .isEqualTo("???? ???????? ?????? | RijVia");

        /*
         * The manual save must preserve draft generation metadata,
         * especially primaryCta required by translation.
         */
        assertThat(jdbc.queryForObject("""
                SELECT metadata ->> 'primaryCta'
                FROM article_versions
                WHERE article_id = ?
                  AND language = 'AR'
                  AND is_current
                """, String.class, articleId))
                .isNotBlank();

        /*
         * Phase bridge only.
         *
         * Workflow transitions themselves already have their dedicated
         * PostgreSQL integration suite. This E2E focuses on the owner-facing
         * authoring chain and never-publish invariant.
         */
        jdbc.update("""
                UPDATE articles
                SET lifecycle_state = 'TRANSLATION_REQUIRED'
                WHERE id = ?
                """, articleId);

        /*
         * 6. Real translation request + handler.
         */
        var translationResponse = translationService.request(
                articleId,
                new EditorialTranslationDtos.CreateRequest(
                        "never-publish-translation-" + articleId),
                "never-publish-owner");

        AgentTask translationTask =
                taskRepository.findById(translationResponse.id()).orElseThrow();

        translationHandler.execute(claimed(translationTask));

        assertThat(translationStub.calls()).isEqualTo(3);
        assertThat(translationStub.targets())
                .containsExactlyInAnyOrder(
                        ContentLocale.NL,
                        ContentLocale.FR,
                        ContentLocale.EN);

        assertThat(state(articleId)).isEqualTo("IMAGE_REQUIRED");

        assertThat(currentLanguages(articleId))
                .containsExactlyInAnyOrder("AR", "NL", "FR", "EN");

        /*
         * 7. Real local image upload with responsive variants,
         *    localized alt text and license metadata.
         */
        var image = imageService.upload(
                articleId,
                image("never-publish"),
                imageMetadata("never-publish"),
                "never-publish-owner");

        assertThat(image.status()).isEqualTo("APPROVED");

        assertThat(image.variants())
                .extracting(EditorialArticleImageDtos.Variant::type)
                .containsExactlyInAnyOrder(
                        "HERO",
                        "CARD",
                        "MEDIUM",
                        "MOBILE",
                        "OG");

        assertThat(image.localizations())
                .extracting(EditorialArticleImageDtos.Localization::language)
                .containsExactly("AR", "NL", "FR", "EN");

        /*
         * 8. Request human approval ONLY.
         *
         * Deliberately do NOT call:
         * approvalService.approve(...)
         * approvalTaskHandler.execute(...)
         * publication dispatcher
         */
        var approvalResponse = approvalRequestService.request(
                articleId,
                new EditorialArticleApprovalDtos.Request(
                        EnumSet.allOf(EditorialArticleQualityGate.class),
                        "Never-publish E2E: content, sources, translations, metadata and image reviewed"),
                "never-publish-owner");

        AgentTask approvalTask =
                taskRepository.findById(approvalResponse.id()).orElseThrow();

        assertThat(approvalTask.getStatus())
                .isEqualTo(TaskStatus.WAITING_APPROVAL);

        assertThat(approvalTask.isRequiresApproval()).isTrue();

        assertThat(state(articleId))
                .isEqualTo("WAITING_APPROVAL");

        /*
         * 9. Hard never-publish invariants.
         */
        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM article_publications
                WHERE article_id = ?
                """, Integer.class, articleId))
                .isZero();

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM agent_tasks
                WHERE task_type = 'ARTICLE_PUBLISH'
                  AND source_id = ?
                """, Integer.class, String.valueOf(articleId)))
                .isZero();

        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM articles
                WHERE id = ?
                  AND lifecycle_state IN ('APPROVED', 'SCHEDULED', 'PUBLISHED')
                """, Integer.class, articleId))
                .isZero();

        /*
         * 10. Public runtime must not expose the article.
         */
        mockMvc.perform(get("/api/articles")
                        .param("language", "AR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(get(
                        "/api/articles/{slug}",
                        "never-publish-ar")
                        .param("language", "AR"))
                .andExpect(status().isNotFound());

        /*
         * Final explicit state.
         */
        assertThat(state(articleId))
                .isEqualTo("WAITING_APPROVAL");
    }

    private EditorialBriefDtos.CreateRequest briefRequest(
            MarketingStrategyContextRequest strategy) {

        return new EditorialBriefDtos.CreateRequest(
                "AR",
                "INFORMATIONAL",
                "???? ?????? ???? ??????? Never Publish",
                "??? ???? ????? ??? ??? ??????? ???????",
                strategy,
                List.of("?????? ??????? ??????"),
                List.of("RijVia verified theory source"),
                false,
                "never-publish-brief-topic-2");
    }

    private MarketingStrategyContextRequest strategy() {
        long uspId = jdbc.queryForObject("""
                SELECT id
                FROM marketing_usp
                WHERE title = 'RijVia learning platform'
                """, Long.class);

        long pillarId = jdbc.queryForObject("""
                SELECT id
                FROM marketing_content_pillars
                WHERE pillar_key = 'THEORY_EXAM'
                """, Long.class);

        long funnelId = jdbc.queryForObject("""
                SELECT id
                FROM marketing_funnel_stages
                WHERE stage_key = 'EDUCATION'
                """, Long.class);

        long goalId = jdbc.queryForObject("""
                SELECT id
                FROM marketing_conversion_goals
                WHERE goal_key = 'CONTINUE_TOPIC_LEARNING'
                """, Long.class);

        return new MarketingStrategyContextRequest(
                uspId,
                "ICP-AR-BEGINNER",
                pillarId,
                funnelId,
                goalId);
    }

    private void insertSupportedCoreClaim(long topicId) {
        long sourceId = jdbc.queryForObject("""
                INSERT INTO editorial_sources (
                    source_type,
                    location_type,
                    title,
                    publisher,
                    internal_reference,
                    language,
                    verification_status,
                    trust_status,
                    legal_review_required,
                    legal_review_status,
                    verified_at,
                    verified_by
                )
                VALUES (
                    'RIJVIA_CORE_DATA',
                    'INTERNAL',
                    'RijVia theory lesson',
                    'RijVia',
                    'LESSON:never-publish-e2e',
                    'AR',
                    'VERIFIED',
                    'CORE_TRUSTED',
                    FALSE,
                    'NOT_REQUIRED',
                    CURRENT_TIMESTAMP,
                    'never-publish-test'
                )
                RETURNING id
                """, Long.class);

        long claimId = jdbc.queryForObject("""
                INSERT INTO editorial_claims (
                    article_topic_id,
                    brief_reference,
                    claim_key,
                    claim_text,
                    claim_type,
                    language,
                    evidence_status,
                    legal_review_required
                )
                VALUES (
                    ?,
                    'never-publish-brief-topic-2',
                    'never-publish-supported-fact',
                    '???? RijVia ??????? ?????? ??????? ???????? ??????.',
                    'PRODUCT_FACT',
                    'AR',
                    'SUPPORTED',
                    FALSE
                )
                RETURNING id
                """, Long.class, topicId);

        jdbc.update("""
                INSERT INTO editorial_claim_sources (
                    claim_id,
                    source_id,
                    relationship_status,
                    evidence_purpose,
                    created_by
                )
                VALUES (
                    ?,
                    ?,
                    'SUPPORTS',
                    'PRODUCT',
                    'never-publish-test'
                )
                """, claimId, sourceId);
    }

    private List<String> currentLanguages(long articleId) {
        return jdbc.queryForList("""
                SELECT language
                FROM article_versions
                WHERE article_id = ?
                  AND is_current
                ORDER BY language
                """, String.class, articleId);
    }

    private String state(long articleId) {
        return jdbc.queryForObject("""
                SELECT lifecycle_state
                FROM articles
                WHERE id = ?
                """, String.class, articleId);
    }

    private static ClaimedTask claimed(AgentTask task) {
        return new ClaimedTask(
                task.getId(),
                task.getAgentType(),
                task.getTaskType(),
                task.getPayload(),
                task.getPayloadVersion(),
                task.getPriority(),
                1,
                task.getCorrelationId());
    }

    private static MockMultipartFile image(String seed)
            throws Exception {

        BufferedImage image =
                new BufferedImage(
                        2048,
                        1200,
                        BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();

        try {
            int accent =
                    Math.floorMod(seed.hashCode(), 180) + 40;

            graphics.setPaint(new GradientPaint(
                    0,
                    0,
                    new Color(accent, 90, 120),
                    2048,
                    1200,
                    new Color(30, 130, accent)));

            graphics.fillRect(0, 0, 2048, 1200);

            graphics.setColor(Color.WHITE);
            graphics.fillRect(900, 0, 240, 1200);
        } finally {
            graphics.dispose();
        }

        ByteArrayOutputStream bytes =
                new ByteArrayOutputStream();

        ImageIO.write(image, "jpeg", bytes);

        return new MockMultipartFile(
                "file",
                seed + ".jpg",
                "image/jpeg",
                bytes.toByteArray());
    }

    private static EditorialArticleImageDtos.UploadMetadata imageMetadata(
            String sourceAssetId) {

        return new EditorialArticleImageDtos.UploadMetadata(
                "rijvia-ar-" + sourceAssetId + "-hero",
                "RijVia owner upload",
                "https://rijvia.be/image-sources/" + sourceAssetId,
                "Owner-approved local file",
                null,
                "Usage rights and relevance verified by the administrator",
                true,
                "???? ????? ?????? ??????",
                "Een Belgische educatieve verkeerssituatie",
                "Une situation routi?re ?ducative belge",
                "A Belgian educational traffic scene",
                null,
                null,
                null,
                null,
                0.5,
                0.5);
    }

    private static String localizedBody(String language) {
        return ("Localized "
                + language
                + " educational Belgian driving theory content for RijVia. ")
                .repeat(30);
    }

    static final class TranslationStub
            implements EditorialTranslationClient {

        private final AtomicInteger calls =
                new AtomicInteger();

        private final List<ContentLocale> targets =
                new CopyOnWriteArrayList<>();

        @Override
        public AdaptedContent adapt(AdaptRequest request) {
            calls.incrementAndGet();
            targets.add(request.targetLocale());

            String target =
                    request.targetLocale().name();

            return new AdaptedContent(
                    request.sourceLocale().name(),
                    target,
                    request.sourceVersionId(),
                    target + " RijVia never publish article",
                    target.toLowerCase(Locale.ROOT)
                            + "-never-publish-article",
                    "Localized RijVia summary for " + target,
                    localizedBody(target),
                    target + " RijVia article | RijVia",
                    "Localized RijVia meta description for "
                            + target
                            + " driving theory article.",
                    "Continue learning with RijVia",
                    "never-publish-translation-test",
                    500,
                    800,
                    "SUCCEEDED");
        }

        int calls() {
            return calls.get();
        }

        List<ContentLocale> targets() {
            return List.copyOf(targets);
        }

        void reset() {
            calls.set(0);
            targets.clear();
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class E2EConfiguration {

        @Bean
        AtomicInteger neverPublishGenerationCalls() {
            return new AtomicInteger();
        }

        @Bean
        @Primary
        ContentGenerationClient neverPublishGenerationClient(
                AtomicInteger calls) {

            return request -> {
                calls.incrementAndGet();

                String body =
                        String.join(
                                " ",
                                Collections.nCopies(
                                        600,
                                        "word"));

                return new ContentGenerationClient.GeneratedContent(
                        request.locale().name(),
                        request.source().sourceReference(),
                        "???? ?????? ????",
                        "???? ?????? ???? ??? ?????? ??????.",
                        body,
                        "????? ??????? ???????? ??? RijVia",
                        "never-publish-generation-test",
                        500,
                        900,
                        "SUCCEEDED");
            };
        }

        @Bean
        TranslationStub neverPublishTranslationStub() {
            return new TranslationStub();
        }

        @Bean
        @Primary
        EditorialTranslationClient neverPublishTranslationClient(
                TranslationStub stub) {

            return stub;
        }
    }
}
