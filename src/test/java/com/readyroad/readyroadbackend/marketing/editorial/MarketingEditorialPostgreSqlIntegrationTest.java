package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalService;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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
class MarketingEditorialPostgreSqlIntegrationTest {

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
        registry.add("jwt.secret-key",
                () -> "ZWRpdG9yaWFsLWludGVncmF0aW9uLXRlc3Qta2V5LW5vdC1mb3ItcHJvZHVjdGlvbg==");
        registry.add("readyroad.admin.default-password", () -> "Editorial-Test-Only-2026!");
    }

    @Autowired DataSource dataSource;
    @Autowired EditorialBacklogService service;
    @Autowired EditorialPriorityService priorityService;
    @Autowired EditorialPrioritySettingsService prioritySettingsService;
    @Autowired EditorialPriorityTaskService priorityTaskService;
    @Autowired EditorialOpportunityDiscoveryService opportunityDiscoveryService;
    @Autowired EditorialOpportunityTaskHandler opportunityTaskHandler;
    @Autowired EditorialSourceCollectionService sourceCollectionService;
    @Autowired EditorialSourceCollectionTaskHandler sourceCollectionTaskHandler;
    @Autowired AgentTaskRepository taskRepository;
    @Autowired ApprovalService approvalService;
    @Autowired ObjectMapper objectMapper;

    @BeforeEach
    void resetPriorityTestData() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("""
                TRUNCATE article_publications, article_versions, article_briefs, articles, article_keyword_clusters
                RESTART IDENTITY
                """);
        jdbc.update("DELETE FROM editorial_claim_sources");
        jdbc.update("DELETE FROM editorial_claims");
        jdbc.update("DELETE FROM editorial_source_versions");
        jdbc.update("DELETE FROM editorial_sources");
        jdbc.update("DELETE FROM article_priorities");
        jdbc.update("DELETE FROM article_topics WHERE source_type = 'SEARCH_CONSOLE_OPPORTUNITY'");
        jdbc.update("""
                UPDATE article_topics
                SET article_priority = NULL, priority_reason = NULL,
                    source_opportunity_id = NULL, content_pillar_id = NULL,
                    funnel_stage_id = NULL, conversion_goal_id = NULL,
                    supporting_pages = '[]'::jsonb, internal_link_targets = '[]'::jsonb
                """);
        jdbc.update("DELETE FROM seo_content_gaps WHERE gap_key = 'editorial-priority-gap'");
        jdbc.update("DELETE FROM seo_opportunities WHERE opportunity_key = 'editorial-priority-test'");
        jdbc.update("DELETE FROM seo_content_gaps WHERE gap_key LIKE 'editorial-discovery-%'");
        jdbc.update("DELETE FROM seo_opportunities WHERE opportunity_key LIKE 'editorial-discovery-%'");
        jdbc.update("DELETE FROM audit_logs WHERE event_type = 'EDITORIAL_PRIORITIES_RECALCULATED'");
        jdbc.update("DELETE FROM audit_logs WHERE event_type = 'EDITORIAL_SOURCE_COLLECTION_COMPLETED'");
        jdbc.update("DELETE FROM agent_tasks WHERE agent_type = 'EDITORIAL'");
        jdbc.update("DELETE FROM marketing_conversion_goals WHERE goal_key = 'EDITORIAL_PRIORITY_TEST'");
    }

    @Test
    void seedsTheExactOfficialBacklogWithoutInventingFutureStrategyDecisions() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_topics", Integer.class)).isEqualTo(40);
        assertThat(jdbc.queryForObject(
                "SELECT count(DISTINCT official_backlog_order) FROM article_topics", Integer.class))
                .isEqualTo(40);
        assertThat(jdbc.queryForObject(
                "SELECT min(official_backlog_order) FROM article_topics", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject(
                "SELECT max(official_backlog_order) FROM article_topics", Integer.class)).isEqualTo(40);
        assertThat(jdbc.queryForList(
                "SELECT official_backlog_order FROM article_topics WHERE pillar ORDER BY official_backlog_order",
                Integer.class)).containsExactly(1, 9, 15, 21, 28, 35);
        assertThat(jdbc.queryForList("""
                SELECT count(*) FROM article_topics
                GROUP BY cluster_order
                ORDER BY cluster_order
                """, Integer.class)).containsExactly(8, 6, 6, 7, 7, 6);
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM article_topics
                WHERE article_priority IS NOT NULL
                   OR priority_reason IS NOT NULL
                   OR source_opportunity_id IS NOT NULL
                   OR content_pillar_id IS NOT NULL
                   OR funnel_stage_id IS NOT NULL
                   OR conversion_goal_id IS NOT NULL
                   OR target_queries <> '[]'::jsonb
                   OR supporting_pages <> '[]'::jsonb
                   OR internal_link_targets <> '[]'::jsonb
                """, Integer.class)).isZero();
    }

    @Test
    void exposesTheBacklogInOfficialOrderWithAccurateSummaryCounts() {
        EditorialDtos.Backlog response = service.backlog();

        assertThat(response.total()).isEqualTo(40);
        assertThat(response.pillars()).isEqualTo(6);
        assertThat(response.unresolvedStrategyContext()).isEqualTo(40);
        assertThat(response.topics()).hasSize(40);
        assertThat(response.topics()).extracting(EditorialDtos.Topic::officialOrder)
                .containsExactlyElementsOf(java.util.stream.IntStream.rangeClosed(1, 40).boxed().toList());
        assertThat(response.topics()).extracting(EditorialDtos.Topic::title)
                .doesNotHaveDuplicates()
                .allSatisfy(title -> assertThat(title).isNotBlank());
        assertThat(response.topics()).extracting(EditorialDtos.Topic::primaryLanguage)
                .containsOnlyNulls();
    }

    @Test
    void seedsTheOwnerApprovedConfigAndRejectsInvalidWeightTotals() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        var config = prioritySettingsService.current();

        assertThat(config.weights().values().stream()
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add))
                .isEqualByComparingTo("100");
        assertThat(config.p0()).isEqualByComparingTo("80");
        assertThat(config.p1()).isEqualByComparingTo("60");
        assertThat(config.p2()).isEqualByComparingTo("40");
        assertThat(config.p3()).isZero();
        assertThat(config.missingSearchConsolePercent()).isEqualByComparingTo("50");
        assertThat(jdbc.queryForObject("""
                SELECT interval_days FROM agent_schedules
                WHERE agent_type = 'ANALYTICS' AND schedule_key = 'analytics-full-sync'
                """, Integer.class)).isEqualTo(3);
        assertThat(jdbc.queryForObject("""
                SELECT zone_id FROM agent_schedules
                WHERE agent_type = 'ANALYTICS' AND schedule_key = 'analytics-full-sync'
                """, String.class)).isEqualTo("Europe/Brussels");
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM agent_definitions WHERE agent_type = 'EDITORIAL' AND enabled",
                Integer.class)).isOne();
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM audit_logs
                WHERE event_type = 'EDITORIAL_PRIORITY_SETTINGS_SEEDED'
                """, Integer.class)).isOne();

        var invalid = prioritySettingsService.raw().deepCopy();
        ((com.fasterxml.jackson.databind.node.ObjectNode) invalid.path("weights"))
                .put("searchDemand", 21);
        assertThatThrownBy(() -> prioritySettingsService.parse(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("total 100");
    }

    @Test
    void recalculationIsIdempotentAndMissingEvidenceKeepsTheOfficialOrderDeterministic() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        assertThat(priorityService.recalculate(null, "TEST", "integration-test")).isEqualTo(40);
        assertThat(priorityService.recalculate(null, "TEST", "integration-test")).isEqualTo(40);

        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_priorities", Integer.class))
                .isEqualTo(40);
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_topics WHERE article_priority = 'P3'",
                Integer.class)).isEqualTo(40);
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_priorities WHERE final_score = 10.000",
                Integer.class)).isEqualTo(40);
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM article_priorities
                WHERE evidence_states->>'searchConsoleOpportunity' = 'MISSING'
                """, Integer.class)).isEqualTo(40);
        assertThat(priorityService.priorities())
                .extracting(EditorialDtos.Priority::officialOrder)
                .containsExactlyElementsOf(java.util.stream.IntStream.rangeClosed(1, 40).boxed().toList());
    }

    @Test
    void realLinkedEvidenceRaisesPriorityWithoutFabricatingUnavailableFactors() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Long pillarId = jdbc.queryForObject(
                "SELECT id FROM marketing_content_pillars WHERE active ORDER BY priority DESC, id LIMIT 1",
                Long.class);
        Long funnelId = jdbc.queryForObject(
                "SELECT id FROM marketing_funnel_stages WHERE active ORDER BY sequence_number, id LIMIT 1",
                Long.class);
        Long conversionId = jdbc.queryForObject("""
                INSERT INTO marketing_conversion_goals (
                    goal_key, name, primary_cta, funnel_stage_id, active, approved_by
                ) VALUES (
                    'EDITORIAL_PRIORITY_TEST', 'Editorial priority test goal',
                    'Start practice', ?, TRUE, 'INTEGRATION_TEST'
                ) RETURNING id
                """, Long.class, funnelId);
        Long opportunityId = jdbc.queryForObject("""
                INSERT INTO seo_opportunities (
                    opportunity_key, query, page, language, state, previous_state,
                    brand_classification, long_tail, search_intent, relevance,
                    cannibalization, impressions, clicks, ctr, average_position,
                    trend, evidence, first_seen_at, last_seen_at
                ) VALUES (
                    'editorial-priority-test', 'امتحان السياقة النظري',
                    'https://readyroad.be/ar', 'AR', 'OPPORTUNITY', 'EMERGING',
                    'NON_BRAND', TRUE, 'INFORMATIONAL', TRUE,
                    FALSE, 50, 5, 0.10, 8,
                    'IMPROVING', '{"hasHistoricalComparison":true}'::jsonb,
                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                ) RETURNING id
                """, Long.class);
        jdbc.update("""
                INSERT INTO seo_content_gaps (
                    gap_key, query, language, search_intent, status,
                    evidence, first_seen_at, last_seen_at
                ) VALUES (
                    'editorial-priority-gap', 'امتحان السياقة النظري', 'AR',
                    'INFORMATIONAL', 'DISCOVERED', '{}'::jsonb,
                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                )
                """);
        jdbc.update("""
                UPDATE article_topics
                SET source_opportunity_id = ?, content_pillar_id = ?, funnel_stage_id = ?,
                    conversion_goal_id = ?, supporting_pages = '["/ar/lessons"]'::jsonb,
                    internal_link_targets = '["/ar/exam"]'::jsonb
                WHERE official_backlog_order = 1
                """, opportunityId, pillarId, funnelId, conversionId);

        priorityService.recalculate(null, "TEST_EVIDENCE", "integration-test");

        EditorialDtos.Priority first = priorityService.priorities().getFirst();
        assertThat(first.officialOrder()).isEqualTo(1);
        assertThat(first.finalScore()).isEqualByComparingTo("90.000");
        assertThat(first.priority()).isEqualTo("P0");
        assertThat(first.searchConsoleScore()).isEqualByComparingTo("100");
        assertThat(first.searchDemandScore()).isEqualByComparingTo("100");
        assertThat(first.businessRelevanceScore()).isEqualByComparingTo("100");
        assertThat(first.evidenceStates()).contains(
                "\"multilingualOpportunity\": \"MISSING\"",
                "\"contentFreshnessNeed\": \"MISSING\"");
    }

    @Test
    void priorityTaskCreationUsesTheUnifiedIdempotencyScope() {
        var first = priorityTaskService.enqueue("MANUAL", "same-trigger", "integration-test");
        var duplicate = priorityTaskService.enqueue("MANUAL", "same-trigger", "integration-test");

        assertThat(first.created()).isTrue();
        assertThat(duplicate.created()).isFalse();
        assertThat(duplicate.task().getId()).isEqualTo(first.task().getId());
    }

    @Test
    void discoversOnlyEvidenceBackedTopicsAndCreatesThemAfterHumanApproval() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Long opportunityId = insertDiscoveryOpportunity(jdbc, "eligible", false, true);

        assertThat(opportunityDiscoveryService.enqueueCandidates(null)).isOne();
        assertThat(opportunityDiscoveryService.enqueueCandidates(null)).isZero();

        AgentTask task = taskRepository
                .findByAgentTypeAndTaskTypeAndIdempotencyKey(
                        "EDITORIAL", "ARTICLE_OPPORTUNITY_DISCOVERY",
                        "article-opportunity:" + opportunityId)
                .orElseThrow();
        assertThat(task.getStatus().name()).isEqualTo("WAITING_APPROVAL");
        assertThat(task.isRequiresApproval()).isTrue();
        assertThat(task.getApprovalMode().name()).isEqualTo("HUMAN_APPROVAL");
        assertThat(task.getPayload().path("queryEvidencePresent").asBoolean()).isTrue();
        assertThat(task.getPayload().path("legalCheckRequired").asBoolean()).isTrue();
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_topics WHERE source_opportunity_id = ?",
                Integer.class,
                opportunityId)).isZero();

        approvalService.approve(task.getId(), "owner-test", "Legal and human review passed");
        AgentTask approved = taskRepository.findById(task.getId()).orElseThrow();
        opportunityTaskHandler.execute(new ClaimedTask(
                approved.getId(), approved.getAgentType(), approved.getTaskType(),
                approved.getPayload(), approved.getPayloadVersion(), approved.getPriority(),
                1, approved.getCorrelationId()));
        opportunityTaskHandler.execute(new ClaimedTask(
                approved.getId(), approved.getAgentType(), approved.getTaskType(),
                approved.getPayload(), approved.getPayloadVersion(), approved.getPriority(),
                2, approved.getCorrelationId()));

        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_topics WHERE source_opportunity_id = ?",
                Integer.class,
                opportunityId)).isOne();
        assertThat(jdbc.queryForObject(
                "SELECT official_backlog_order FROM article_topics WHERE source_opportunity_id = ?",
                Integer.class,
                opportunityId)).isEqualTo(41);
        assertThat(jdbc.queryForObject(
                "SELECT source_type FROM article_topics WHERE source_opportunity_id = ?",
                String.class,
                opportunityId)).isEqualTo("SEARCH_CONSOLE_OPPORTUNITY");
        assertThat(jdbc.queryForObject(
                "SELECT status FROM article_topics WHERE source_opportunity_id = ?",
                String.class,
                opportunityId)).isEqualTo("PLANNED");
        assertThat(service.backlog().total()).isEqualTo(40);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_topics", Integer.class)).isEqualTo(41);
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM agent_tasks
                WHERE agent_type = 'EDITORIAL'
                  AND task_type = 'EDITORIAL_PRIORITY_RECALCULATE'
                  AND idempotency_key LIKE 'priority:ARTICLE_TOPIC_ADDED:%'
                """, Integer.class)).isOne();
    }

    @Test
    void rejectsCannibalizedMissingGapAndDuplicateTitleCandidates() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        insertDiscoveryOpportunity(jdbc, "cannibalized", true, true);
        insertDiscoveryOpportunity(jdbc, "missing-gap", false, false);
        insertDiscoveryOpportunity(jdbc, "duplicate", false, true);
        jdbc.update("""
                UPDATE seo_opportunities
                SET query = (SELECT working_title FROM article_topics WHERE official_backlog_order = 1)
                WHERE opportunity_key = 'editorial-discovery-duplicate'
                """);

        assertThat(opportunityDiscoveryService.enqueueCandidates(null)).isZero();
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM agent_tasks
                WHERE task_type = 'ARTICLE_OPPORTUNITY_DISCOVERY'
                """, Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_topics", Integer.class)).isEqualTo(40);
    }

    @Test
    void collectsExplicitClaimsOnlyAfterHumanApprovalAndRemainsIdempotent() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        long topicId = jdbc.queryForObject(
                "SELECT id FROM article_topics WHERE official_backlog_order = 1", Long.class);
        var request = sourceCollectionRequest(topicId, "source-collection-1", "fingerprint-v1");

        var first = sourceCollectionService.request(request, "admin-owner");
        var duplicate = sourceCollectionService.request(request, "admin-owner");

        assertThat(first.id()).isEqualTo(duplicate.id());
        AgentTask waiting = taskRepository.findById(first.id()).orElseThrow();
        assertThat(waiting.getStatus().name()).isEqualTo("WAITING_APPROVAL");
        assertThat(waiting.getApprovalMode().name()).isEqualTo("HUMAN_APPROVAL");
        assertThat(waiting.getApprovalSource()).isEqualTo("MASTER_SPEC_V3_PART_06_SOURCE_REGISTRY");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM editorial_sources", Integer.class)).isZero();
        assertThatThrownBy(() -> sourceCollectionTaskHandler.execute(claimed(waiting)))
                .isInstanceOf(com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException.class)
                .hasMessageContaining("explicit human approval");

        approvalService.approve(waiting.getId(), "owner-test", "Sources and claims reviewed");
        AgentTask approved = taskRepository.findById(waiting.getId()).orElseThrow();
        ClaimedTask claimed = claimed(approved);
        sourceCollectionTaskHandler.execute(claimed);
        sourceCollectionTaskHandler.execute(claimed);

        assertThat(jdbc.queryForObject("SELECT count(*) FROM editorial_sources", Integer.class)).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM editorial_source_versions", Integer.class))
                .isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM editorial_claims", Integer.class)).isEqualTo(3);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM editorial_claim_sources", Integer.class))
                .isEqualTo(2);
        assertThat(jdbc.queryForList("""
                SELECT evidence_status FROM editorial_claims ORDER BY claim_key
                """, String.class)).containsExactly("SUPPORTED", "MISSING", "SUPPORTED");
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM audit_logs
                WHERE task_id = ? AND event_type = 'EDITORIAL_SOURCE_COLLECTION_COMPLETED'
                """, Integer.class, waiting.getId())).isOne();
        assertThat(sourceCollectionService.sources(topicId)).hasSize(2)
                .allSatisfy(source -> assertThat(source.claimCount()).isOne());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_topics", Integer.class)).isEqualTo(40);
    }

    @Test
    void blocksNonOfficialLegalEvidenceAndMarksChangedVerifiedSourcesStale() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        long topicId = jdbc.queryForObject(
                "SELECT id FROM article_topics WHERE official_backlog_order = 2", Long.class);
        var referenceRequest = new EditorialSourceDtos.SourceCollectionRequest(
                topicId,
                "BRIEF-OFFICIAL-002",
                List.of(new EditorialSourceDtos.ClaimInput(
                        "legal-reference-only",
                        "A legal claim cannot rely on a reference source",
                        "LEGAL",
                        "EN",
                        true,
                        List.of(new EditorialSourceDtos.SourceInput(
                                "APPROVED_REFERENCE_SOURCE", "EXTERNAL", "Reference", "Reference publisher",
                                "https://reference.example/legal", null, "BE", "EN", "VERIFIED",
                                "APPROVED_REFERENCE", true, "VERIFIED", "reference-v1", null, null)))),
                "reference-legal");
        executeApproved(referenceRequest, "reference-legal-owner");

        assertThat(jdbc.queryForObject("""
                SELECT evidence_status FROM editorial_claims WHERE claim_key = 'legal-reference-only'
                """, String.class)).isEqualTo("REQUIRES_REVIEW");

        var original = sourceCollectionRequest(topicId, "official-original", "official-v1");
        executeApproved(original, "official-owner");
        var changed = sourceCollectionRequest(topicId, "official-changed", "official-v2");
        executeApproved(changed, "official-owner");

        assertThat(jdbc.queryForObject("""
                SELECT verification_status FROM editorial_sources
                WHERE canonical_url = 'https://mobilit.belgium.be/rules'
                """, String.class)).isEqualTo("STALE");
        assertThat(jdbc.queryForObject("""
                SELECT legal_review_status FROM editorial_sources
                WHERE canonical_url = 'https://mobilit.belgium.be/rules'
                """, String.class)).isEqualTo("STALE");
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM editorial_source_versions version
                JOIN editorial_sources source ON source.id = version.source_id
                WHERE source.canonical_url = 'https://mobilit.belgium.be/rules'
                """, Integer.class)).isEqualTo(2);
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM editorial_sources
                WHERE canonical_url = 'https://mobilit.belgium.be/rules'
                """, Integer.class)).isOne();
        assertThat(jdbc.queryForObject("""
                SELECT evidence_status FROM editorial_claims WHERE claim_key = 'legal-claim'
                """, String.class)).isEqualTo("REQUIRES_REVIEW");
    }

    @Test
    void createsOnlyTheApprovedEditorialCoreTablesAndPreservesExistingData() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        assertThat(jdbc.queryForList("""
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = current_schema()
                  AND table_name IN (
                      'article_keyword_clusters', 'article_briefs', 'articles', 'article_versions'
                  )
                ORDER BY table_name
                """, String.class)).containsExactly(
                        "article_briefs", "article_keyword_clusters", "article_versions", "articles");
        assertThat(jdbc.queryForObject("""
                SELECT count(*)
                FROM information_schema.tables
                WHERE table_schema = current_schema() AND table_name = 'article_sources'
                """, Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_topics", Integer.class)).isEqualTo(40);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_keyword_clusters", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_briefs", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM articles", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM article_versions", Integer.class)).isZero();
    }

    @Test
    void persistsStrategyBoundBriefArticleAndLocalizedVersionHistory() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Long topicId = topicId(jdbc, 1);
        String icpId = jdbc.queryForObject(
                "SELECT id FROM marketing_icp WHERE active ORDER BY id LIMIT 1", String.class);
        Long pillarId = jdbc.queryForObject(
                "SELECT id FROM marketing_content_pillars WHERE active ORDER BY id LIMIT 1", Long.class);
        Long funnelStageId = jdbc.queryForObject(
                "SELECT id FROM marketing_funnel_stages WHERE active ORDER BY sequence_number LIMIT 1", Long.class);
        Long conversionGoalId = null;

        Long clusterId = jdbc.queryForObject("""
                INSERT INTO article_keyword_clusters (
                    cluster_key, primary_query, search_intent, primary_language,
                    content_pillar_id, funnel_stage_id, status
                ) VALUES ('task-7-cluster', 'Belgian driving theory', 'INFORMATIONAL', 'EN', ?, ?, 'ACTIVE')
                RETURNING id
                """, Long.class, pillarId, funnelStageId);
        Long briefId = jdbc.queryForObject("""
                INSERT INTO article_briefs (
                    article_topic_id, keyword_cluster_id, target_language, search_intent,
                    working_title, purpose, icp_id, content_pillar_id, funnel_stage_id,
                    conversion_goal_id, primary_cta, target_queries, source_requirements,
                    legal_review_required, status
                ) VALUES (?, ?, 'AR', 'INFORMATIONAL', 'Belgian theory guide',
                          'Prepare a factual localized article', ?, ?, ?, ?, 'Start learning',
                          '["belgian driving theory"]'::jsonb,
                          '["official government source"]'::jsonb, TRUE, 'APPROVED')
                RETURNING id
                """, Long.class, topicId, clusterId, icpId, pillarId, funnelStageId, conversionGoalId);
        Long articleId = jdbc.queryForObject("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language,
                    icp_id, content_pillar_id, funnel_stage_id, conversion_goal_id
                ) VALUES (?, 'task-7-article', 'BRIEF_READY', 'AR', ?, ?, ?, ?)
                RETURNING id
                """, Long.class, topicId, icpId, pillarId, funnelStageId, conversionGoalId);

        jdbc.update("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, slug, summary, body,
                    metadata, generation_metadata, status, is_current, created_by
                ) VALUES (?, 1, 'AR', 'First Arabic draft', 'arabic-guide', 'Initial summary',
                          'Initial content', '{"legalReview":true}'::jsonb,
                          '{"source":"CONTENT_AGENT"}'::jsonb, 'DRAFT', FALSE, 'editor@example.test')
                """, articleId);
        jdbc.update("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, slug, body,
                    status, is_current, created_by
                ) VALUES (?, 2, 'AR', 'Current Arabic draft', 'arabic-guide', 'Revised content',
                          'DRAFT_READY', TRUE, 'editor@example.test')
                """, articleId);
        for (String language : List.of("NL", "EN", "FR")) {
            jdbc.update("""
                    INSERT INTO article_versions (
                        article_id, version_number, language, title, slug, body,
                        status, is_current, created_by
                    ) VALUES (?, 1, ?, ?, ?, ?, 'DRAFT_READY', TRUE, 'editor@example.test')
                    """, articleId, language, language + " localized draft",
                    "localized-guide-" + language.toLowerCase(), language + " localized content");
        }

        assertThat(briefId).isPositive();
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_versions WHERE article_id = ?", Integer.class, articleId))
                .isEqualTo(5);
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM article_versions
                WHERE article_id = ? AND is_current
                """, Integer.class, articleId)).isEqualTo(4);
        assertThat(jdbc.queryForList("""
                SELECT language FROM article_versions
                WHERE article_id = ? AND is_current
                ORDER BY language
                """, String.class, articleId)).containsExactly("AR", "EN", "FR", "NL");
        assertThat(jdbc.queryForList("""
                SELECT version_number FROM article_versions
                WHERE article_id = ? AND language = 'AR'
                ORDER BY version_number
                """, Integer.class, articleId)).containsExactly(1, 2);
    }

    @Test
    void enforcesEditorialIdentityForeignKeysAndCurrentSlugUniqueness() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Long firstArticleId = insertArticle(jdbc, 1, "task-7-first-article");
        jdbc.update("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, slug, body, status, is_current
                ) VALUES (?, 1, 'EN', 'First article', 'belgian-theory', 'Content', 'DRAFT', TRUE)
                """, firstArticleId);

        assertThatThrownBy(() -> insertArticle(jdbc, 1, "duplicate-topic-article"))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, body, status, is_current
                ) VALUES (?, 1, 'EN', 'Duplicate version', 'Content', 'DRAFT', FALSE)
                """, firstArticleId)).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, body, status, is_current
                ) VALUES (?, 2, 'DE', 'Unsupported locale', 'Content', 'DRAFT', FALSE)
                """, firstArticleId)).isInstanceOf(DataIntegrityViolationException.class);

        Long secondArticleId = insertArticle(jdbc, 2, "task-7-second-article");
        assertThatThrownBy(() -> jdbc.update("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, slug, body, status, is_current
                ) VALUES (?, 1, 'EN', 'Second article', 'BELGIAN-THEORY', 'Content', 'DRAFT', TRUE)
                """, secondArticleId)).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language
                ) VALUES (9223372036854775807, 'missing-topic', 'IDEA', 'EN')
                """)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void preventsDestructiveDeletionOfArticleVersionHistory() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Long topicId = topicId(jdbc, 1);
        Long articleId = insertArticle(jdbc, 1, "task-7-history-article");
        jdbc.update("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, body, status, is_current
                ) VALUES (?, 1, 'EN', 'Historical article', 'Historical content', 'DRAFT', TRUE)
                """, articleId);

        assertThatThrownBy(() -> jdbc.update("DELETE FROM articles WHERE id = ?", articleId))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.update("DELETE FROM article_topics WHERE id = ?", topicId))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM article_versions WHERE article_id = ?", Integer.class, articleId))
                .isOne();
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM articles WHERE id = ?", Integer.class, articleId)).isOne();
    }

    private void executeApproved(
            EditorialSourceDtos.SourceCollectionRequest request,
            String actor) {
        var response = sourceCollectionService.request(request, actor);
        approvalService.approve(response.id(), actor, "Owner approved source collection");
        AgentTask task = taskRepository.findById(response.id()).orElseThrow();
        sourceCollectionTaskHandler.execute(claimed(task));
    }

    private static ClaimedTask claimed(AgentTask task) {
        return new ClaimedTask(
                task.getId(), task.getAgentType(), task.getTaskType(), task.getPayload(),
                task.getPayloadVersion(), task.getPriority(), 1, task.getCorrelationId());
    }

    private static Long insertArticle(JdbcTemplate jdbc, int backlogOrder, String canonicalKey) {
        return jdbc.queryForObject("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language
                ) VALUES (?, ?, 'IDEA', 'EN')
                RETURNING id
                """, Long.class, topicId(jdbc, backlogOrder), canonicalKey);
    }

    private static Long topicId(JdbcTemplate jdbc, int backlogOrder) {
        return jdbc.queryForObject("""
                SELECT id FROM article_topics WHERE official_backlog_order = ?
                """, Long.class, backlogOrder);
    }

    private static EditorialSourceDtos.SourceCollectionRequest sourceCollectionRequest(
            long topicId,
            String idempotencyKey,
            String officialFingerprint) {
        var official = new EditorialSourceDtos.SourceInput(
                "OFFICIAL_GOVERNMENT_SOURCE", "EXTERNAL", "Belgian mobility rules",
                "Belgian mobility authority", "https://Mobilit.Belgium.be/rules/", null,
                "BE", "EN", "VERIFIED", "OFFICIAL", true, "VERIFIED",
                officialFingerprint, "official-etag", "Wed, 13 Aug 2026 10:00:00 GMT");
        var core = new EditorialSourceDtos.SourceInput(
                "READYROAD_CORE_DATA", "INTERNAL", "ReadyRoad exam configuration", "ReadyRoad",
                null, "core:exam-configuration", "BE", "EN", "VERIFIED",
                "CORE_TRUSTED", false, "NOT_REQUIRED", "core-v1", null, null);
        return new EditorialSourceDtos.SourceCollectionRequest(
                topicId,
                "BRIEF-" + topicId,
                List.of(
                        new EditorialSourceDtos.ClaimInput(
                                "legal-claim", "A reviewed legal statement", "LEGAL", "EN", true,
                                List.of(official)),
                        new EditorialSourceDtos.ClaimInput(
                                "missing-claim", "A claim with explicitly missing evidence", "FACTUAL", "EN", false,
                                List.of()),
                        new EditorialSourceDtos.ClaimInput(
                                "product-claim", "A ReadyRoad product fact", "PRODUCT_FACT", "EN", false,
                                List.of(core))),
                idempotencyKey);
    }

    private static Long insertDiscoveryOpportunity(
            JdbcTemplate jdbc, String suffix, boolean cannibalization, boolean contentGap) {
        String key = "editorial-discovery-" + suffix;
        String query = "Belgian driving theory discovery " + suffix;
        Long id = jdbc.queryForObject("""
                INSERT INTO seo_opportunities (
                    opportunity_key, query, page, language, state, previous_state,
                    brand_classification, long_tail, search_intent, relevance,
                    cannibalization, impressions, clicks, ctr, average_position,
                    trend, evidence, first_seen_at, last_seen_at
                ) VALUES (?, ?, 'https://readyroad.be/lessons', 'EN', 'OPPORTUNITY', 'EMERGING',
                          'NON_BRAND', TRUE, 'INFORMATIONAL', TRUE, ?,
                          120, 8, 0.066, 9, 'IMPROVING',
                          '{"hasHistoricalComparison":true}'::jsonb,
                          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                RETURNING id
                """, Long.class, key, query, cannibalization);
        if (contentGap) {
            jdbc.update("""
                    INSERT INTO seo_content_gaps (
                        gap_key, query, language, search_intent, status,
                        evidence, first_seen_at, last_seen_at
                    ) VALUES (?, ?, 'EN', 'INFORMATIONAL', 'DISCOVERED', '{}'::jsonb,
                              CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                    """, key, query);
        }
        return id;
    }
}
