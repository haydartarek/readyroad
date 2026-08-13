package com.readyroad.readyroadbackend.marketing.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextRequest;
import java.time.Instant;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("postgresql")
@Testcontainers
@Import(MarketingContentPostgreSqlIntegrationTest.GenerationTestConfiguration.class)
class MarketingContentPostgreSqlIntegrationTest {

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
        registry.add("readyroad.marketing.content.api-key", () -> "integration-test-key");
        registry.add("jwt.secret-key",
                () -> "Y29udGVudC1pbnRlZ3JhdGlvbi10ZXN0LWtleS1ub3QtZm9yLXByb2R1Y3Rpb24=");
        registry.add("readyroad.admin.default-password", () -> "Content-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired ContentAgentService contentAgentService;
    @Autowired ContentAdminService adminService;
    @Autowired AtomicInteger generationCalls;

    private JdbcTemplate jdbc;
    private StrategyIds strategy;

    @BeforeEach
    void prepare() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.update("TRUNCATE content_items, youtube_videos RESTART IDENTITY CASCADE");
        jdbc.update("DELETE FROM marketing_conversion_goals WHERE approved_by = 'CONTENT_TEST'");
        jdbc.update("DELETE FROM marketing_usp WHERE approved_by = 'CONTENT_TEST'");
        jdbc.update("""
                INSERT INTO marketing_usp (
                    title, description, evidence_type, evidence_reference, active, priority, approved_by)
                VALUES ('Verified ReadyRoad USP', 'Verified feature', 'READYROAD_FEATURE',
                        'SUPPORTED_LANGUAGES', true, 3, 'CONTENT_TEST')
                """);
        long uspId = jdbc.queryForObject(
                "SELECT id FROM marketing_usp WHERE approved_by = 'CONTENT_TEST'", Long.class);
        long pillarId = jdbc.queryForObject(
                "SELECT id FROM marketing_content_pillars WHERE pillar_key = 'READYROAD_EDUCATIONAL_VIDEOS'",
                Long.class);
        long funnelId = jdbc.queryForObject(
                "SELECT id FROM marketing_funnel_stages WHERE stage_key = 'EDUCATION'", Long.class);
        jdbc.update("""
                INSERT INTO marketing_conversion_goals (
                    goal_key, name, primary_cta, funnel_stage_id, active, approved_by)
                VALUES ('CONTENT_TEST_CONTINUE', 'Continue learning', 'Continue on ReadyRoad', ?, true,
                        'CONTENT_TEST')
                """, funnelId);
        long goalId = jdbc.queryForObject(
                "SELECT id FROM marketing_conversion_goals WHERE approved_by = 'CONTENT_TEST'", Long.class);
        strategy = new StrategyIds(uspId, "ICP-AR-BEGINNER", pillarId, funnelId, goalId);
        generationCalls.set(0);
        insertYouTubeSource("content-video-1", "First verified video");
    }

    @Test
    void migrationAddsOnlyTheContentAgentSchemaExtension() {
        Set<String> columns = new HashSet<>(jdbc.queryForList("""
                SELECT column_name FROM information_schema.columns
                WHERE table_schema = 'public' AND table_name = 'content_items'
                  AND column_name IN ('source_hash', 'content_fingerprint')
                """, String.class));
        assertThat(columns).containsExactlyInAnyOrder("source_hash", "content_fingerprint");
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM agent_definitions WHERE agent_type = 'CONTENT' AND enabled",
                Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("""
                SELECT setting_value->>'primaryModel' FROM agent_settings
                WHERE agent_type = 'CONTENT' AND setting_key = 'content.generation'
                """, String.class)).isEqualTo("gpt-5.6-terra");
        String sourceContextIndex = jdbc.queryForObject("""
                SELECT indexdef FROM pg_indexes
                WHERE schemaname = 'public' AND indexname = 'uq_content_variant_source_context'
                """, String.class);
        assertThat(sourceContextIndex)
                .contains("usp_id", "icp_id", "content_pillar_id", "funnel_stage_id", "conversion_goal_id")
                .contains("positioningId");
    }

    @Test
    void createsFourStrategyBoundVariantsAtomicallyAndReusesTheExistingPackage() {
        var payload = new ContentTaskPayload(ContentSourceType.YOUTUBE, "content-video-1", null);

        ContentPackageResult first = contentAgentService.generate(payload, null);
        ContentPackageResult second = contentAgentService.generate(payload, null);

        assertThat(first.existing()).isFalse();
        assertThat(first.variants()).isEqualTo(4);
        assertThat(second.packageId()).isEqualTo(first.packageId());
        assertThat(second.existing()).isTrue();
        assertThat(generationCalls).hasValue(4);
        assertThat(jdbc.queryForList("""
                SELECT language FROM content_items
                WHERE item_type = 'CONTENT_VARIANT' AND parent_item_id = ?
                """, String.class, first.packageId()))
                .containsExactlyInAnyOrder("AR", "NL", "EN", "FR");
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM content_items
                WHERE item_type = 'CONTENT_VARIANT'
                  AND usp_id = ? AND icp_id = ? AND content_pillar_id = ?
                  AND funnel_stage_id = ? AND conversion_goal_id = ?
                  AND metadata->>'provider' = 'OPENAI'
                  AND metadata->>'model' = 'gpt-5.6-terra'
                  AND (metadata->>'inputTokens')::int = 100
                  AND (metadata->>'outputTokens')::int = 50
                """, Integer.class, strategy.uspId(), strategy.icpId(), strategy.pillarId(),
                strategy.funnelId(), strategy.goalId())).isEqualTo(4);
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM content_items
                WHERE metadata::text ILIKE '%integration-test-key%'
                """, Integer.class)).isZero();
    }

    @Test
    void rejectsDuplicateGeneratedDraftAcrossDifferentSources() {
        contentAgentService.generate(new ContentTaskPayload(ContentSourceType.YOUTUBE, "content-video-1", null), null);
        insertYouTubeSource("content-video-2", "Second verified video");

        assertThatThrownBy(() -> contentAgentService.generate(
                new ContentTaskPayload(ContentSourceType.YOUTUBE, "content-video-2", null), null))
                .isInstanceOf(ContentValidationException.class)
                .extracting(error -> ((ContentValidationException) error).errorCode())
                .isEqualTo("DUPLICATE_CONTENT");
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM content_items WHERE item_type = 'CONTENT_PACKAGE'", Integer.class))
                .isEqualTo(1);
    }

    @Test
    void adminTaskCreationUsesUnifiedIdempotencyAndStandingAuthorization() {
        var request = new ContentAdminDtos.GenerateRequest(
                ContentSourceType.YOUTUBE, "content-video-1", null, "content-integration-idempotency");

        var first = adminService.requestGeneration(request, "integration-admin");
        var second = adminService.requestGeneration(request, "integration-admin");

        assertThat(second.id()).isEqualTo(first.id());
        assertThat(first.agentType()).isEqualTo("CONTENT");
        assertThat(first.taskType()).isEqualTo("CONTENT_PACKAGE_GENERATE");
        assertThat(first.requiresApproval()).isFalse();
    }

    private void insertYouTubeSource(String videoId, String title) {
        jdbc.update("""
                INSERT INTO youtube_videos (
                    video_id, channel_id, channel_title, title, description, published_at,
                    thumbnail_url, watch_url, embed_url, source_language, source_hash)
                VALUES (?, 'readyroad-channel', 'ReadyRoad', ?, 'Verified educational source', ?,
                        'https://example.test/thumb.jpg', 'https://example.test/watch',
                        'https://example.test/embed', 'EN', ?)
                """, videoId, title, Timestamp.from(Instant.parse("2026-08-13T10:00:00Z")), "hash-" + videoId);
        jdbc.update("""
                INSERT INTO content_items (
                    item_key, item_type, source_type, source_id, language, status, title, body,
                    usp_id, icp_id, content_pillar_id, funnel_stage_id, conversion_goal_id,
                    primary_cta, strategy_context, metadata)
                VALUES (?, 'YOUTUBE_CONTENT_PACKAGE', 'YOUTUBE_VIDEO', ?, 'EN',
                        'READY_FOR_CONTENT_AGENT', ?, 'Verified educational source', ?, ?, ?, ?, ?,
                        'Continue on ReadyRoad', '{}'::jsonb, '{}'::jsonb)
                """, "youtube-package:" + videoId, videoId, title, strategy.uspId(), strategy.icpId(),
                strategy.pillarId(), strategy.funnelId(), strategy.goalId());
    }

    record StrategyIds(long uspId, String icpId, long pillarId, long funnelId, long goalId) {}

    @TestConfiguration(proxyBeanMethods = false)
    static class GenerationTestConfiguration {
        @Bean
        AtomicInteger generationCalls() {
            return new AtomicInteger();
        }

        @Bean
        @Primary
        ContentGenerationClient deterministicGenerationClient(AtomicInteger calls) {
            return request -> {
                calls.incrementAndGet();
                String locale = request.locale().name();
                return new ContentGenerationClient.GeneratedContent(
                        locale,
                        request.source().sourceReference(),
                        "Verified title " + locale,
                        "Verified summary " + locale,
                        "Verified educational body " + locale,
                        "Continue learning " + locale,
                        "gpt-5.6-terra",
                        100,
                        50,
                        "SUCCEEDED");
            };
        }
    }
}
