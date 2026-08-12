package com.readyroad.readyroadbackend.marketing.admin;

import com.readyroad.readyroadbackend.marketing.approval.ApprovalService;
import com.readyroad.readyroadbackend.marketing.domain.AgentTask;
import com.readyroad.readyroadbackend.marketing.repository.AgentTaskRepository;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/admin/marketing")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class MarketingAdminController {

    private final MarketingInfrastructureService infrastructureService;
    private final AgentTaskRepository taskRepository;
    private final ApprovalService approvalService;
    private final MarketingAdminPlatformService platformService;

    @GetMapping("/infrastructure")
    public MarketingInfrastructureResponse infrastructure() {
        return infrastructureService.status();
    }

    @GetMapping("/overview")
    public MarketingAdminDtos.Overview overview() {
        return platformService.overview();
    }

    @GetMapping("/agents")
    public java.util.List<MarketingAdminDtos.AgentStatus> agents() {
        return platformService.agents();
    }

    @PutMapping("/agents/{agentType}/enabled")
    public ResponseEntity<MarketingTaskLifecycleResponse> setAgentEnabled(
            @PathVariable String agentType,
            @Valid @RequestBody MarketingAdminDtos.EnabledRequest request,
            Principal principal) {
        return ResponseEntity.accepted().body(
                platformService.requestAgentEnabledChange(
                        agentType, request.enabled(), request.idempotencyKey(), principal.getName()));
    }

    @GetMapping("/tasks")
    public MarketingAdminDtos.TaskPage tasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer limit) {
        return platformService.tasks(status, limit);
    }

    @PostMapping("/tasks/{taskId}/retry")
    public ResponseEntity<MarketingTaskLifecycleResponse> retry(
            @PathVariable Long taskId, Principal principal) {
        return ResponseEntity.accepted().body(platformService.retry(taskId, principal.getName()));
    }

    @GetMapping("/errors")
    public java.util.List<MarketingAdminDtos.ErrorItem> errors(
            @RequestParam(required = false) Integer limit) {
        return platformService.errors(limit);
    }

    @GetMapping("/audit")
    public java.util.List<MarketingAdminDtos.AuditItem> audit(
            @RequestParam(required = false) Integer limit) {
        return platformService.audit(limit);
    }

    @GetMapping("/settings")
    public MarketingAdminDtos.Settings settings() {
        return platformService.settings();
    }

    @GetMapping("/worker-health")
    public MarketingAdminDtos.WorkerHealth workerHealth() {
        return platformService.workerHealth();
    }

    @GetMapping("/tasks/{taskId}")
    public MarketingTaskLifecycleResponse task(@PathVariable Long taskId) {
        return MarketingTaskLifecycleResponse.from(findTask(taskId));
    }

    @PostMapping("/tasks/{taskId}/approve")
    public ResponseEntity<MarketingTaskLifecycleResponse> approve(
            @PathVariable Long taskId,
            @Valid @RequestBody(required = false) ApprovalDecisionRequest request,
            Principal principal) {
        AgentTask task = approvalService.approve(taskId, principal.getName(), reason(request));
        return ResponseEntity.ok(MarketingTaskLifecycleResponse.from(task));
    }

    @PostMapping("/tasks/{taskId}/reject")
    public ResponseEntity<MarketingTaskLifecycleResponse> reject(
            @PathVariable Long taskId,
            @Valid @RequestBody(required = false) ApprovalDecisionRequest request,
            Principal principal) {
        AgentTask task = approvalService.reject(taskId, principal.getName(), reason(request));
        return ResponseEntity.ok(MarketingTaskLifecycleResponse.from(task));
    }

    private AgentTask findTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marketing task not found"));
    }

    private static String reason(ApprovalDecisionRequest request) {
        return request == null ? null : request.reason();
    }
}
