package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.readyroad.readyroadbackend.marketing.content.ContentGenerationClient;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextRequest;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import java.util.Collections;
import java.util.List;
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
@Import(EditorialBriefDraftPostgreSqlIntegrationTest.GenerationConfiguration.class)
class EditorialBriefDraftPostgreSqlIntegrationTest {

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
        registry.add("readyroad.marketing.content.api-key", () -> "editorial-integration-test-key");
        registry.add("jwt.secret-key",
                () -> "ZWRpdG9yaWFsLWJyaWVmLWRyYWZ0LXRlc3Qta2V5LW5vdC1mb3ItcHJvZHVjdGlvbg==");
        registry.add("readyroad.admin.default-password", () -> "Editorial-Brief-Test-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired EditorialBriefService briefService;
    @Autowired EditorialBriefTaskHandler briefHandler;
    @Autowired EditorialDraftService draftService;
    @Autowired EditorialDraftTaskHandler draftHandler;
    @Autowired EditorialBacklogService backlogService;
    @Autowired EditorialEditorService editorService;
    @Autowired AgentTaskRepository taskRepository;
    @Autowired AtomicInteger generationCalls;

    private JdbcTemplate jdbc;

    @BeforeEach
    void reset() {
        jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("""
                TRUNCATE article_refresh_recommendations, article_performance_snapshots,
                         article_publications, article_image_licenses, article_image_localizations,
                         article_image_variants, article_image_assets, article_versions, article_briefs,
                         articles, article_keyword_clusters
                RESTART IDENTITY
                """);
        jdbc.update("DELETE FROM editorial_claim_sources");
        jdbc.update("DELETE FROM editorial_claims");
        jdbc.update("DELETE FROM editorial_source_versions");
        jdbc.update("DELETE FROM editorial_sources");
        jdbc.update("DELETE FROM audit_logs WHERE event_type IN ("
                + "'EDITORIAL_ARTICLE_BRIEF_CREATED', 'EDITORIAL_ARTICLE_DRAFT_CREATED')");
        jdbc.update("DELETE FROM agent_tasks WHERE task_type IN ('ARTICLE_BRIEF_CREATE', 'ARTICLE_DRAFT_CREATE')");
        jdbc.update("""
                UPDATE article_topics
                SET status = 'PLANNED', primary_language = NULL,
                    usp_id = NULL, icp_id = NULL, content_pillar_id = NULL,
                    funnel_stage_id = NULL, conversion_goal_id = NULL,
                    target_queries = '[]'::jsonb
                """);
        generationCalls.set(0);
    }

    @Test
    void createsAnIdempotentStrategyBoundBriefWithoutRequiringSearchConsoleEvidence() {
        EditorialBriefDtos.CreateRequest request = briefRequest(strategy("EDUCATION", "CONTINUE_TOPIC_LEARNING"));

        var first = briefService.request(2, request, "editorial-admin");
        var duplicate = briefService.request(2, request, "editorial-admin");
        assertThat(first.id()).isEqualTo(duplicate.id());
        assertThat(first.taskType()).isEqualTo("ARTICLE_BRIEF_CREATE");
        assertThat(first.requiresApproval()).isFalse();
        assertThat(first.approvalMode()).isEqualTo("STANDING_OWNER_AUTHORIZATION");

        var task = taskRepository.findById(first.id()).orElseThrow();
        ClaimedTask claimed = claimed(task);
        briefHandler.execute(claimed);
        briefHandler.execute(claimed);

        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_briefs WHERE source_task_id = ?", Integer.class, first.id()))
                .isOne();
        assertThat(jdbc.queryForObject(
                "SELECT lifecycle_state FROM articles WHERE article_topic_id = 2", String.class))
                .isEqualTo("BRIEF_READY");
        assertThat(jdbc.queryForObject(
                "SELECT primary_cta FROM article_briefs WHERE article_topic_id = 2", String.class))
                .isEqualTo("تعلّم القاعدة بالتفصيل على RijVia");
        assertThat(jdbc.queryForObject(
                "SELECT source_opportunity_id FROM article_topics WHERE id = 2", Long.class))
                .isNull();
        assertThat(backlogService.backlog().topics().stream()
                .filter(topic -> topic.id() == 2)
                .findFirst().orElseThrow().strategyContextResolved()).isTrue();
    }

    @Test
    void exposesTheNextSafeAuthoringActionFromBriefThroughVerifiedEvidence() {
        var initial = editorService.authoringStatus(2);
        assertThat(initial.canCreateBrief()).isTrue();
        assertThat(initial.canCollectSources()).isFalse();
        assertThat(initial.canCreateDraft()).isFalse();

        var response = briefService.request(
                2, briefRequest(strategy("EDUCATION", "CONTINUE_TOPIC_LEARNING")), "editorial-admin");
        var queued = editorService.authoringStatus(2);
        assertThat(queued.latestBriefTaskStatus()).isEqualTo("PENDING");
        assertThat(queued.canCreateBrief()).isFalse();

        briefHandler.execute(claimed(taskRepository.findById(response.id()).orElseThrow()));
        var briefReady = editorService.authoringStatus(2);
        assertThat(briefReady.briefReference()).startsWith("ARTICLE_BRIEF:");
        assertThat(briefReady.canCollectSources()).isTrue();
        assertThat(briefReady.canCreateDraft()).isFalse();

        insertSupportedCoreClaim(2);
        var evidenceReady = editorService.authoringStatus(2);
        assertThat(evidenceReady.claimsTotal()).isOne();
        assertThat(evidenceReady.claimsSupported()).isOne();
        assertThat(evidenceReady.canCreateDraft()).isTrue();
    }

    @Test
    void blocksBriefCreationWhenTheSelectedGoalDoesNotBelongToTheFunnelStage() {
        EditorialBriefDtos.CreateRequest request = briefRequest(strategy("EDUCATION", "START_PRACTICE"));
        var response = briefService.request(2, request, "editorial-admin");
        var task = taskRepository.findById(response.id()).orElseThrow();

        assertThatThrownBy(() -> briefHandler.execute(claimed(task)))
                .isInstanceOf(MarketingTaskExecutionException.class)
                .extracting(error -> ((MarketingTaskExecutionException) error).errorCode())
                .isEqualTo("BLOCKED_STRATEGY_CONTEXT");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_briefs", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM articles", Integer.class)).isZero();
    }

    @Test
    void blocksDraftCreationUntilEveryClaimHasCurrentVerifiedEvidence() {
        long articleId = createBrief();
        var response = draftService.request(
                articleId, new EditorialDraftDtos.CreateRequest("draft-without-sources"), "editorial-admin");
        var task = taskRepository.findById(response.id()).orElseThrow();

        assertThatThrownBy(() -> draftHandler.execute(claimed(task)))
                .isInstanceOf(MarketingTaskExecutionException.class)
                .extracting(error -> ((MarketingTaskExecutionException) error).errorCode())
                .isEqualTo("BLOCKED_CONTENT_SOURCE");
        assertThat(jdbc.queryForObject(
                "SELECT lifecycle_state FROM articles WHERE id = ?", String.class, articleId))
                .isEqualTo("BRIEF_READY");
        assertThat(generationCalls).hasValue(0);
    }

    @Test
    void blocksLegalClaimsWithoutAVerifiedOfficialLegalSource() {
        long articleId = createBrief();
        insertSupportedCoreClaim(2);
        jdbc.update("""
                UPDATE editorial_claims
                SET claim_type = 'LEGAL', legal_review_required = TRUE
                WHERE article_topic_id = 2
                """);
        var response = draftService.request(
                articleId, new EditorialDraftDtos.CreateRequest("legal-source-required"), "editorial-admin");
        var task = taskRepository.findById(response.id()).orElseThrow();

        assertThatThrownBy(() -> draftHandler.execute(claimed(task)))
                .isInstanceOf(MarketingTaskExecutionException.class)
                .extracting(error -> ((MarketingTaskExecutionException) error).errorCode())
                .isEqualTo("BLOCKED_CONTENT_SOURCE");
        assertThat(jdbc.queryForObject(
                "SELECT lifecycle_state FROM articles WHERE id = ?", String.class, articleId))
                .isEqualTo("BRIEF_READY");
        assertThat(generationCalls).hasValue(0);
    }

    @Test
    void createsOneSourceGroundedDraftAndKeepsTaskTraceOnRepeatedExecution() {
        long articleId = createBrief();
        insertSupportedCoreClaim(2);
        var first = draftService.request(
                articleId, new EditorialDraftDtos.CreateRequest("verified-draft"), "editorial-admin");
        var duplicate = draftService.request(
                articleId, new EditorialDraftDtos.CreateRequest("verified-draft"), "editorial-admin");
        assertThat(duplicate.id()).isEqualTo(first.id());
        var task = taskRepository.findById(first.id()).orElseThrow();
        ClaimedTask claimed = claimed(task);

        draftHandler.execute(claimed);
        draftHandler.execute(claimed);

        assertThat(generationCalls).hasValue(1);
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_versions WHERE generated_by_task_id = ?",
                Integer.class, first.id())).isOne();
        assertThat(jdbc.queryForObject(
                "SELECT lifecycle_state FROM articles WHERE id = ?", String.class, articleId))
                .isEqualTo("DRAFT_READY");
        assertThat(jdbc.queryForObject("""
                SELECT generation_metadata ->> 'sourceReference'
                FROM article_versions WHERE generated_by_task_id = ?
                """, String.class, first.id())).startsWith("ARTICLE_BRIEF:");
        assertThat(jdbc.queryForObject("""
                SELECT (generation_metadata ->> 'wordCount')::int
                FROM article_versions WHERE generated_by_task_id = ?
                """, Integer.class, first.id())).isEqualTo(600);
    }

    private long createBrief() {
        var response = briefService.request(
                2, briefRequest(strategy("EDUCATION", "CONTINUE_TOPIC_LEARNING")), "editorial-admin");
        var task = taskRepository.findById(response.id()).orElseThrow();
        briefHandler.execute(claimed(task));
        return jdbc.queryForObject(
                "SELECT id FROM articles WHERE article_topic_id = 2", Long.class);
    }

    private EditorialBriefDtos.CreateRequest briefRequest(MarketingStrategyContextRequest strategy) {
        return new EditorialBriefDtos.CreateRequest(
                "AR",
                "INFORMATIONAL",
                "دليل تعليمي موثق",
                "شرح الموضوع اعتمادًا على مصادر موثقة فقط",
                strategy,
                List.of("امتحان السياقة النظري"),
                List.of("RijVia core learning source"),
                false,
                "brief-topic-2");
    }

    private MarketingStrategyContextRequest strategy(String funnelKey, String goalKey) {
        long uspId = jdbc.queryForObject(
                "SELECT id FROM marketing_usp WHERE title = 'RijVia learning platform'", Long.class);
        String icpId = "ICP-AR-BEGINNER";
        long pillarId = jdbc.queryForObject(
                "SELECT id FROM marketing_content_pillars WHERE pillar_key = 'THEORY_EXAM'", Long.class);
        long funnelId = jdbc.queryForObject(
                "SELECT id FROM marketing_funnel_stages WHERE stage_key = ?", Long.class, funnelKey);
        long goalId = jdbc.queryForObject(
                "SELECT id FROM marketing_conversion_goals WHERE goal_key = ?", Long.class, goalKey);
        return new MarketingStrategyContextRequest(uspId, icpId, pillarId, funnelId, goalId);
    }

    private void insertSupportedCoreClaim(long topicId) {
        long sourceId = jdbc.queryForObject("""
                INSERT INTO editorial_sources (
                    source_type, location_type, title, publisher, internal_reference,
                    language, verification_status, trust_status, legal_review_required,
                    legal_review_status, verified_at, verified_by
                ) VALUES (
                    'RIJVIA_CORE_DATA', 'INTERNAL', 'RijVia theory lesson', 'RijVia',
                    'LESSON:les-1', 'AR', 'VERIFIED', 'CORE_TRUSTED', FALSE,
                    'NOT_REQUIRED', CURRENT_TIMESTAMP, 'integration-test'
                ) RETURNING id
                """, Long.class);
        long claimId = jdbc.queryForObject("""
                INSERT INTO editorial_claims (
                    article_topic_id, brief_reference, claim_key, claim_text, claim_type,
                    language, evidence_status, legal_review_required
                ) VALUES (?, 'brief-topic-2', 'verified-product-fact',
                          'يتيح RijVia تدريبًا منظمًا على أسئلة الامتحان النظري.',
                          'PRODUCT_FACT', 'AR', 'SUPPORTED', FALSE)
                RETURNING id
                """, Long.class, topicId);
        jdbc.update("""
                INSERT INTO editorial_claim_sources (
                    claim_id, source_id, relationship_status, evidence_purpose, created_by
                ) VALUES (?, ?, 'SUPPORTS', 'PRODUCT', 'integration-test')
                """, claimId, sourceId);
    }

    private static ClaimedTask claimed(com.readyroad.readyroadbackend.marketing.domain.AgentTask task) {
        return new ClaimedTask(
                task.getId(), task.getAgentType(), task.getTaskType(), task.getPayload(),
                task.getPayloadVersion(), task.getPriority(), 1, task.getCorrelationId());
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class GenerationConfiguration {
        @Bean
        AtomicInteger editorialGenerationCalls() {
            return new AtomicInteger();
        }

        @Bean
        @Primary
        ContentGenerationClient editorialGenerationClient(AtomicInteger calls) {
            return request -> {
                calls.incrementAndGet();
                String body = String.join(" ", Collections.nCopies(600, "معلومة"));
                return new ContentGenerationClient.GeneratedContent(
                        request.locale().name(),
                        request.source().sourceReference(),
                        "دليل تعليمي موثق",
                        "ملخص تعليمي مبني على المصدر المسجل.",
                        body,
                        "تعلّم القاعدة بالتفصيل على RijVia",
                        "gpt-5.6-terra",
                        500,
                        900,
                        "SUCCEEDED");
            };
        }
    }
}
