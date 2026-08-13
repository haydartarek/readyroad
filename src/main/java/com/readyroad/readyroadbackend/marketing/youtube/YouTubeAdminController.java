package com.readyroad.readyroadbackend.marketing.youtube;

import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/marketing/youtube")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class YouTubeAdminController {

    private final YouTubeAdminService service;

    @GetMapping("/status")
    public YouTubeDtos.Status status() {
        return service.status();
    }

    @PostMapping("/sync")
    public ResponseEntity<MarketingTaskLifecycleResponse> sync(
            @Valid @RequestBody YouTubeDtos.SyncRequest request,
            Principal principal) {
        return ResponseEntity.accepted().body(service.requestSync(request.idempotencyKey(), principal.getName()));
    }
}
