package com.readyroad.readyroadbackend.marketing.analytics;

import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/marketing/analytics")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AnalyticsAdminController {

    private final AnalyticsAdminService service;

    @GetMapping("/status")
    public AnalyticsAdminDtos.Status status() {
        return service.status();
    }

    @GetMapping("/settings")
    public AnalyticsAdminDtos.SettingsView settings() {
        return service.settings();
    }

    @PutMapping("/settings")
    public ResponseEntity<MarketingTaskLifecycleResponse> updateSettings(
            @Valid @RequestBody AnalyticsAdminDtos.SettingsUpdateRequest request,
            Principal principal) {
        return ResponseEntity.accepted().body(service.requestSettingsUpdate(request, principal.getName()));
    }

    @PostMapping("/sync")
    public ResponseEntity<MarketingTaskLifecycleResponse> sync(
            @Valid @RequestBody AnalyticsAdminDtos.SyncRequest request,
            Principal principal) {
        return ResponseEntity.accepted().body(service.requestSync(request.idempotencyKey(), principal.getName()));
    }

    @GetMapping("/organic-discovery")
    public AnalyticsAdminDtos.Discovery discovery(@RequestParam(defaultValue = "50") int limit) {
        return service.discovery(limit);
    }

    @GetMapping("/reports")
    public List<Map<String, Object>> reports(@RequestParam(defaultValue = "20") int limit) {
        return service.reports(limit);
    }
}
