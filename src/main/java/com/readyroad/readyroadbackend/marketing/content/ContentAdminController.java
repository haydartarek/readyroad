package com.readyroad.readyroadbackend.marketing.content;

import com.readyroad.readyroadbackend.marketing.admin.MarketingTaskLifecycleResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/marketing/content")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ContentAdminController {

    private final ContentAdminService service;

    @GetMapping("/status")
    public ContentAdminDtos.Status status() {
        return service.status();
    }

    @GetMapping("/packages")
    public List<ContentAdminDtos.PackageItem> packages(@RequestParam(required = false) Integer limit) {
        return service.packages(limit);
    }

    @PostMapping("/generate")
    public ResponseEntity<MarketingTaskLifecycleResponse> generate(
            @Valid @RequestBody ContentAdminDtos.GenerateRequest request,
            Principal principal) {
        return ResponseEntity.accepted().body(service.requestGeneration(request, principal.getName()));
    }
}
