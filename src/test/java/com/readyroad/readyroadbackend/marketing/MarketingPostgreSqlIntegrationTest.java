package com.readyroad.readyroadbackend.marketing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalService;
import com.readyroad.readyroadbackend.marketing.domain.AgentDefinition;
import com.readyroad.readyroadbackend.marketing.domain.AgentSchedule;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.domain.ApprovalDecision;
import com.readyroad.readyroadbackend.marketing.domain.TaskAttemptStatus;
import com.readyroad.readyroadbackend.marketing.domain.TaskPriority;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import com.readyroad.readyroadbackend.marketing.repository.AgentApprovalRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentDefinitionRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentExecutionLogRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentScheduleRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentSettingRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskAttemptRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.repository.MarketingAuditLogRepository;
import com.readyroad.readyroadbackend.marketing.schedule.MarketingScheduleService;
import com.readyroad.readyroadbackend.marketing.settings.AgentSettingsService;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskClaimService;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationResult;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import com.readyroad.readyroadbackend.marketing.task.TaskExecutionService;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("postgresql")
@Testcontainers
class MarketingPostgreSqlIntegrationTest {

    private static final Set<String> EXPECTED_TABLES = Set.of(
            "agent_definitions",
            "agent_tasks",
            "agent_task_attempts",
            "agent_execution_logs",
            "agent_approvals",
            "agent_settings",
            "agent_schedules",
            "audit_logs");

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add("readyroad.marketing.enabled", () -> "false");
        registry.add("jwt.secret-key",
                () -> "bWFya2V0aW5nLXRlc3Qtand0LXNlY3JldC1ub3QtZm9yLXByb2R1Y3Rpb24tMjAyNg==");
        registry.add("readyroad.admin.default-password", () -> "Marketing-Test-Only-2026!");
    }

    @Autowired AgentDefinitionRepository definitionRepository;
    @Autowired AgentTaskRepository taskRepository;
    @Autowired AgentTaskAttemptRepository attemptRepository;
    @Autowired AgentExecutionLogRepository executionLogRepository;
    @Autowired AgentApprovalRepository approvalRepository;
    @Autowired AgentSettingRepository settingRepository;
    @Autowired AgentScheduleRepository scheduleRepository;
    @Autowired MarketingAuditLogRepository auditRepository;
    @Autowired TaskCreationService creationService;
    @Autowired TaskClaimService claimService;
    @Autowired TaskExecutionService executionService;
    @Autowired ApprovalService approvalService;
    @Autowired MarketingScheduleService scheduleService;
    @Autowired AgentSettingsService settingsService;
    @Autowired ObjectMapper objectMapper;
    @Autowired DataSource dataSource;

    @BeforeEach
    void cleanMarketingTables() {
        executionLogRepository.deleteAll();
        auditRepository.deleteAll();
        approvalRepository.deleteAll();
        attemptRepository.deleteAll();
        taskRepository.deleteAll();
        settingRepository.deleteAll();
        scheduleRepository.deleteAll();
        definitionRepository.deleteAll();
        definitionRepository.save(new AgentDefinition("TEST_AGENT", "Test agent", true));
    }

    @Test
    void migrationCreatesOnlyTheEightApprovedInfrastructureTables() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        List<String> tables = jdbc.queryForList("""
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name IN (
                    'agent_definitions', 'agent_tasks', 'agent_task_attempts',
                    'agent_execution_logs', 'agent_approvals', 'agent_settings',
                    'agent_schedules', 'audit_logs'
                  )
                """, String.class);

        assertThat(new HashSet<>(tables)).isEqualTo(EXPECTED_TABLES);
    }

    @Test
    void duplicateIdempotencyKeyReturnsTheOriginalTaskWithoutCreatingAnotherTask() {
        CreateMarketingTaskCommand command = command("SYNC", "same-key", TaskPriority.NORMAL,
                ApprovalMetadata.standingOwnerAuthorization());

        TaskCreationResult first = creationService.create(command);
        TaskCreationResult duplicate = creationService.create(command);

        assertThat(first.created()).isTrue();
        assertThat(duplicate.created()).isFalse();
        assertThat(duplicate.task().getId()).isEqualTo(first.task().getId());
        assertThat(taskRepository.count()).isOne();
        assertThat(auditRepository.countByEventType("DUPLICATE_TASK_REJECTED")).isOne();
    }

    @Test
    void standingAuthorizationIsRecordedAgainstTheInitialPayloadVersion() {
        AgentTask task = creationService.create(command(
                "STANDING_APPROVAL", "standing-approval", TaskPriority.NORMAL,
                ApprovalMetadata.standingOwnerAuthorization())).task();

        assertThat(approvalRepository.findByTaskIdAndPayloadVersion(task.getId(), 1))
                .get()
                .satisfies(approval -> {
                    assertThat(approval.getDecision()).isEqualTo(ApprovalDecision.APPROVED);
                    assertThat(approval.getApprovedBy()).isEqualTo("SYSTEM");
                    assertThat(approval.getApprovalSource()).isEqualTo("MASTER_SPEC_V3");
                });
    }

    @Test
    void claimsAtMostTenTasksOrderedByPriorityThenCreationTime() {
        IntStream.range(0, 6).forEach(index -> creationService.create(
                command("LOW_" + index, "low-" + index, TaskPriority.LOW,
                        ApprovalMetadata.standingOwnerAuthorization())));
        IntStream.range(0, 6).forEach(index -> creationService.create(
                command("CRITICAL_" + index, "critical-" + index, TaskPriority.CRITICAL,
                        ApprovalMetadata.standingOwnerAuthorization())));

        List<ClaimedTask> claimed = claimService.claimNextBatch("worker-priority");

        assertThat(claimed).hasSize(10);
        assertThat(claimed.subList(0, 6)).allMatch(task -> task.priority() == TaskPriority.CRITICAL);
        assertThat(claimed.subList(6, 10)).allMatch(task -> task.priority() == TaskPriority.LOW);
    }

    @Test
    void twoWorkersCannotClaimTheSameTask() throws Exception {
        IntStream.range(0, 20).forEach(index -> creationService.create(
                command("CONCURRENT_" + index, "concurrent-" + index, TaskPriority.NORMAL,
                        ApprovalMetadata.standingOwnerAuthorization())));

        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            Callable<List<ClaimedTask>> firstClaim = () -> {
                start.await();
                return claimService.claimNextBatch("worker-one");
            };
            Callable<List<ClaimedTask>> secondClaim = () -> {
                start.await();
                return claimService.claimNextBatch("worker-two");
            };
            Future<List<ClaimedTask>> first = executor.submit(firstClaim);
            Future<List<ClaimedTask>> second = executor.submit(secondClaim);
            start.countDown();

            List<Long> firstIds = first.get().stream().map(ClaimedTask::taskId).toList();
            List<Long> secondIds = second.get().stream().map(ClaimedTask::taskId).toList();

            assertThat(firstIds).hasSize(10);
            assertThat(secondIds).hasSize(10);
            assertThat(firstIds).doesNotContainAnyElementsOf(secondIds);
            assertThat(taskRepository.countByStatus(TaskStatus.RUNNING)).isEqualTo(20);
        }
    }

    @Test
    void expiredWorkerLockInterruptsTheAttemptAndSchedulesTheApprovedRetry() {
        AgentTask task = creationService.create(command(
                "EXPIRE", "lock-expiry", TaskPriority.NORMAL,
                ApprovalMetadata.standingOwnerAuthorization())).task();
        claimService.claimNextBatch("expired-worker");
        AgentTask running = taskRepository.findById(task.getId()).orElseThrow();
        running.setLockExpiresAt(Instant.now().minusSeconds(1));
        taskRepository.saveAndFlush(running);

        assertThat(claimService.claimNextBatch("recovery-worker")).isEmpty();

        AgentTask recovered = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(recovered.getStatus()).isEqualTo(TaskStatus.RETRY_SCHEDULED);
        assertThat(recovered.getNextRetryAt()).isAfter(Instant.now().plusSeconds(4 * 60));
        assertThat(attemptRepository
                .findFirstByTaskIdAndStatusOrderByAttemptNumberDesc(task.getId(), TaskAttemptStatus.INTERRUPTED))
                .isPresent();
    }

    @Test
    void approvalIsBoundToTheCurrentPayloadSnapshot() {
        AgentTask task = creationService.create(command(
                "APPROVE", "human-approval", TaskPriority.HIGH,
                ApprovalMetadata.humanApproval("MASTER_SPEC_V3"))).task();

        task.setPayload(objectMapper.createObjectNode().put("changed", true));
        taskRepository.saveAndFlush(task);

        assertThatThrownBy(() -> approvalService.approve(task.getId(), "admin", "reviewed"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("stale");
    }

    @Test
    void retryableExecutionFailureUsesTheApprovedFiveMinuteDelay() {
        AgentTask task = creationService.create(command(
                "RETRY", "retryable", TaskPriority.NORMAL,
                ApprovalMetadata.standingOwnerAuthorization())).task();
        claimService.claimNextBatch("retry-worker");

        executionService.fail(task.getId(), "retry-worker",
                new MarketingTaskExecutionException("HTTP_503", "Temporary upstream outage"));

        AgentTask failed = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(failed.getStatus()).isEqualTo(TaskStatus.RETRY_SCHEDULED);
        assertThat(failed.getNextRetryAt()).isAfter(Instant.now().plusSeconds(4 * 60));
        assertThat(failed.getLockedBy()).isNull();
    }

    @Test
    void nonRetryableExecutionFailureFailsImmediately() {
        AgentTask task = creationService.create(command(
                "PERMANENT_FAILURE", "permanent-failure", TaskPriority.NORMAL,
                ApprovalMetadata.standingOwnerAuthorization())).task();
        claimService.claimNextBatch("permanent-worker");

        executionService.fail(task.getId(), "permanent-worker",
                new MarketingTaskExecutionException("INVALID_CREDENTIALS", "Credentials rejected"));

        AgentTask failed = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(failed.getStatus()).isEqualTo(TaskStatus.FAILED);
        assertThat(failed.getNextRetryAt()).isNull();
        assertThat(failed.getLockedBy()).isNull();
    }

    @Test
    void humanApprovalApprovesOnlyTheCurrentPayloadVersionAndWritesAudit() {
        AgentTask task = creationService.create(command(
                "APPROVAL_SUCCESS", "approval-success", TaskPriority.HIGH,
                ApprovalMetadata.humanApproval("MASTER_SPEC_V3"))).task();

        approvalService.approve(task.getId(), "admin", "Reviewed current payload");

        AgentTask approved = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(approved.getStatus()).isEqualTo(TaskStatus.APPROVED);
        assertThat(approved.getApprovedBy()).isEqualTo("admin");
        assertThat(approvalRepository.findByTaskIdAndPayloadVersion(task.getId(), 1))
                .get()
                .extracting(approval -> approval.getDecision())
                .isEqualTo(ApprovalDecision.APPROVED);
        assertThat(auditRepository.countByEventType("TASK_APPROVED")).isOne();
    }

    @Test
    void approvedFutureTaskRemainsScheduledUntilItsDueTime() {
        Instant future = Instant.now().plusSeconds(3600);
        CreateMarketingTaskCommand scheduled = new CreateMarketingTaskCommand(
                "TEST_AGENT",
                "FUTURE_APPROVAL",
                objectMapper.createObjectNode().put("safe", true),
                TaskPriority.NORMAL,
                future,
                "integration-test",
                "future-approval",
                "correlation-future-approval",
                null,
                "TEST",
                "future-approval",
                ApprovalMetadata.humanApproval("MASTER_SPEC_V3"));
        AgentTask task = creationService.create(scheduled).task();

        approvalService.approve(task.getId(), "admin", "Approved for future execution");

        assertThat(taskRepository.findById(task.getId()).orElseThrow().getStatus())
                .isEqualTo(TaskStatus.SCHEDULED);
        assertThat(claimService.claimNextBatch("early-worker")).isEmpty();
    }

    @Test
    void changedApprovedPayloadRequiresANewApprovalOutsideStandingAuthorization() {
        AgentTask task = creationService.create(command(
                "APPROVAL_RENEWAL", "approval-renewal", TaskPriority.HIGH,
                ApprovalMetadata.humanApproval("MASTER_SPEC_V3"))).task();
        approvalService.approve(task.getId(), "admin", "Initial approval");

        approvalService.replaceApprovedPayload(
                task.getId(),
                objectMapper.createObjectNode().put("safe", "updated"),
                false,
                "admin");

        AgentTask updated = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(updated.getPayloadVersion()).isEqualTo(2);
        assertThat(updated.getStatus()).isEqualTo(TaskStatus.WAITING_APPROVAL);
        assertThat(updated.getApprovedBy()).isNull();
        assertThat(approvalRepository.findByTaskIdAndPayloadVersion(task.getId(), 2))
                .get()
                .extracting(approval -> approval.getDecision())
                .isEqualTo(ApprovalDecision.PENDING);
    }

    @Test
    void schedulerCreatesATaskInsteadOfExecutingWorkDirectly() {
        AgentSchedule schedule = new AgentSchedule();
        schedule.setAgentType("TEST_AGENT");
        schedule.setScheduleKey("test-minute");
        schedule.setTaskType("SCHEDULED_TEST");
        schedule.setCronExpression("0 * * * * *");
        schedule.setPayload(objectMapper.createObjectNode().put("safe", true));
        schedule.setEnabled(true);
        schedule.setNextRunAt(Instant.now().minusSeconds(1));
        schedule = scheduleRepository.saveAndFlush(schedule);

        assertThat(scheduleService.enqueueDueSchedules()).isOne();

        assertThat(taskRepository.count()).isOne();
        AgentSchedule updated = scheduleRepository.findById(schedule.getId()).orElseThrow();
        assertThat(updated.getLastRunAt()).isNotNull();
        assertThat(updated.getNextRunAt()).isAfter(Instant.now());
        assertThat(taskRepository.findAll().getFirst().getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    void agentSettingsRejectSecretBearingKeys() {
        assertThatThrownBy(() -> settingsService.saveNonSecret(
                "TEST_AGENT",
                "google_oauth_refresh_token",
                objectMapper.createObjectNode().put("value", "must-not-be-stored"),
                "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Secrets");
        assertThat(settingRepository.count()).isZero();
    }

    @Test
    void agentSettingsRejectNestedSecretBearingFields() {
        assertThatThrownBy(() -> settingsService.saveNonSecret(
                "TEST_AGENT",
                "integration_config",
                objectMapper.createObjectNode()
                        .set("provider", objectMapper.createObjectNode().put("access_token", "must-not-be-stored")),
                "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Secrets");
        assertThat(settingRepository.count()).isZero();
    }

    private CreateMarketingTaskCommand command(
            String taskType,
            String idempotencyKey,
            TaskPriority priority,
            ApprovalMetadata approvalMetadata) {
        return new CreateMarketingTaskCommand(
                "TEST_AGENT",
                taskType,
                objectMapper.createObjectNode().put("safe", true),
                priority,
                null,
                "integration-test",
                idempotencyKey,
                "correlation-" + idempotencyKey,
                null,
                "TEST",
                idempotencyKey,
                approvalMetadata);
    }
}
