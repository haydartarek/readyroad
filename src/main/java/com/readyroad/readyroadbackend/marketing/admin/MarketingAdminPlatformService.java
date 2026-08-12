package com.readyroad.readyroadbackend.marketing.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.approval.ApprovalMetadata;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.domain.AgentDefinition;
import com.readyroad.readyroadbackend.marketing.domain.ExecutionLogLevel;
import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import com.readyroad.readyroadbackend.marketing.repository.AgentDefinitionRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentExecutionLogRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentScheduleRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentSettingRepository;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import com.readyroad.readyroadbackend.marketing.repository.MarketingAuditLogRepository;
import com.readyroad.readyroadbackend.marketing.task.CreateMarketingTaskCommand;
import com.readyroad.readyroadbackend.marketing.task.TaskCreationService;
import java.time.Instant;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class MarketingAdminPlatformService {

    public static final String ADMIN_AGENT_TYPE = "ADMIN_PLATFORM";
    public static final String AGENT_ENABLED_TASK = "AGENT_ENABLED_CHANGE";
    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 100;
    private static final ZoneId OPERATIONS_ZONE = ZoneId.of("Europe/Brussels");

    private final AgentDefinitionRepository definitionRepository;
    private final AgentTaskRepository taskRepository;
    private final AgentExecutionLogRepository executionLogRepository;
    private final MarketingAuditLogRepository auditRepository;
    private final AgentSettingRepository settingRepository;
    private final AgentScheduleRepository scheduleRepository;
    private final TaskCreationService taskCreationService;
    private final MarketingAuditService auditService;
    private final MarketingProperties properties;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public MarketingAdminDtos.Overview overview() {
        Map<String, Long> counts = taskCounts();
        var firstPage = PageRequest.of(0, 5);
        List<MarketingAdminDtos.AuditItem> recent = auditRepository
                .findAllByOrderByCreatedAtDesc(firstPage)
                .stream()
                .map(MarketingAdminDtos.AuditItem::from)
                .toList();
        List<MarketingAdminDtos.ErrorItem> alerts = executionLogRepository
                .findByLevelOrderByCreatedAtDesc(ExecutionLogLevel.ERROR, firstPage)
                .stream()
                .map(MarketingAdminDtos.ErrorItem::from)
                .toList();
        return new MarketingAdminDtos.Overview(
                properties.isEnabled(), counts, taskRepository.countByCreatedAtGreaterThanEqual(startOfToday()),
                definitionRepository.countByEnabledTrue(),
                taskRepository.countActiveWorkers(), recent, alerts, Instant.now());
    }

    @Transactional(readOnly = true)
    public List<MarketingAdminDtos.AgentStatus> agents() {
        return definitionRepository.findAllByOrderByAgentTypeAsc().stream()
                .map(this::agentStatus)
                .toList();
    }

    @Transactional
    public MarketingTaskLifecycleResponse requestAgentEnabledChange(
            String agentType, boolean enabled, String idempotencyKey, String actor) {
        if (ADMIN_AGENT_TYPE.equals(agentType)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin platform cannot disable itself");
        }
        AgentDefinition definition = definitionRepository.findByAgentType(agentType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marketing agent not found"));
        if (definition.isEnabled() == enabled) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Marketing agent already has the requested state");
        }
        var payload = objectMapper.createObjectNode()
                .put("agentType", agentType)
                .put("enabled", enabled);
        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                ADMIN_AGENT_TYPE,
                AGENT_ENABLED_TASK,
                payload,
                com.readyroad.readyroadbackend.marketing.domain.TaskPriority.HIGH,
                null,
                actor,
                idempotencyKey,
                null,
                null,
                "AGENT_DEFINITION",
                agentType,
                ApprovalMetadata.humanApproval("MASTER_SPEC_V3")));
        return MarketingTaskLifecycleResponse.from(result.task());
    }

    @Transactional(readOnly = true)
    public MarketingAdminDtos.TaskPage tasks(String status, Integer requestedLimit) {
        int limit = limit(requestedLimit);
        var pageRequest = PageRequest.of(0, limit);
        var page = status == null || status.isBlank()
                ? taskRepository.findAllByOrderByCreatedAtDesc(pageRequest)
                : taskRepository.findByStatusOrderByCreatedAtDesc(parseStatus(status), pageRequest);
        return new MarketingAdminDtos.TaskPage(
                page.stream().map(MarketingAdminDtos.TaskSummary::from).toList(),
                page.getTotalElements());
    }

    @Transactional
    public MarketingTaskLifecycleResponse retry(Long taskId, String actor) {
        var original = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marketing task not found"));
        if (original.getStatus() != TaskStatus.FAILED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only FAILED tasks can be retried manually");
        }
        ApprovalMetadata approval = original.isRequiresApproval()
                ? ApprovalMetadata.humanApproval(original.getApprovalSource())
                : ApprovalMetadata.standingOwnerAuthorization();
        String retryKey = "manual-retry-" + taskId + "-" + UUID.randomUUID();
        var result = taskCreationService.create(new CreateMarketingTaskCommand(
                original.getAgentType(),
                original.getTaskType(),
                original.getPayload(),
                original.getPriority(),
                null,
                actor,
                retryKey,
                original.getCorrelationId(),
                original.getId(),
                "MANUAL_RETRY",
                String.valueOf(original.getId()),
                approval));
        auditService.recordTaskEvent(
                result.task(), "MANUAL_RETRY_CREATED", actor, auditService.detail("originalTaskId", taskId));
        return MarketingTaskLifecycleResponse.from(result.task());
    }

    @Transactional(readOnly = true)
    public List<MarketingAdminDtos.ErrorItem> errors(Integer requestedLimit) {
        return executionLogRepository
                .findByLevelOrderByCreatedAtDesc(ExecutionLogLevel.ERROR, PageRequest.of(0, limit(requestedLimit)))
                .stream()
                .map(MarketingAdminDtos.ErrorItem::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MarketingAdminDtos.AuditItem> audit(Integer requestedLimit) {
        return auditRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit(requestedLimit))).stream()
                .map(MarketingAdminDtos.AuditItem::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MarketingAdminDtos.Settings settings() {
        return new MarketingAdminDtos.Settings(
                settingRepository.findAllByOrderByAgentTypeAscSettingKeyAsc().stream()
                        .map(MarketingAdminDtos.SettingItem::from)
                        .toList(),
                scheduleRepository.findAllByOrderByAgentTypeAscScheduleKeyAsc().stream()
                        .map(MarketingAdminDtos.ScheduleItem::from)
                        .toList());
    }

    @Transactional(readOnly = true)
    public MarketingAdminDtos.WorkerHealth workerHealth() {
        long running = taskRepository.countByStatus(TaskStatus.RUNNING);
        long expired = taskRepository.countByStatusAndLockExpiresAtBefore(TaskStatus.RUNNING, Instant.now());
        String status = !properties.isEnabled() ? "DISABLED" : expired > 0 ? "DEGRADED" : "HEALTHY";
        return new MarketingAdminDtos.WorkerHealth(
                status,
                properties.isEnabled(),
                taskRepository.countActiveWorkers(),
                running,
                expired,
                properties.getWorker().getPollIntervalMs(),
                properties.getWorker().getBatchSize(),
                properties.getWorker().getLockTtl().toSeconds(),
                Instant.now());
    }

    private MarketingAdminDtos.AgentStatus agentStatus(AgentDefinition definition) {
        String type = definition.getAgentType();
        long total = taskRepository.countByAgentType(type);
        long completed = taskRepository.countByAgentTypeAndStatus(type, TaskStatus.COMPLETED);
        long failed = taskRepository.countByAgentTypeAndStatus(type, TaskStatus.FAILED);
        long terminal = completed + failed;
        double successRate = terminal == 0 ? 0 : Math.round((completed * 1000.0) / terminal) / 10.0;
        return new MarketingAdminDtos.AgentStatus(
                type,
                definition.getDisplayName(),
                definition.getDescription(),
                definition.isEnabled(),
                taskRepository.findLastRunAt(type),
                taskRepository.findLastSuccessAt(type),
                taskRepository.findLastFailureAt(type),
                taskRepository.countByAgentTypeAndStatus(type, TaskStatus.RUNNING),
                taskRepository.countByAgentTypeAndCreatedAtGreaterThanEqual(type, startOfToday()),
                total,
                completed,
                failed,
                taskRepository.countByAgentTypeAndAttemptsGreaterThan(type, 1),
                successRate);
    }

    private Map<String, Long> taskCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            counts.put(status.name(), taskRepository.countByStatus(status));
        }
        return Map.copyOf(counts);
    }

    private static TaskStatus parseStatus(String value) {
        try {
            return TaskStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported marketing task status");
        }
    }

    private static int limit(Integer requested) {
        if (requested == null) {
            return DEFAULT_LIMIT;
        }
        return Math.max(1, Math.min(requested, MAX_LIMIT));
    }

    private static Instant startOfToday() {
        return Instant.now().atZone(OPERATIONS_ZONE).toLocalDate().atStartOfDay(OPERATIONS_ZONE).toInstant();
    }
}
