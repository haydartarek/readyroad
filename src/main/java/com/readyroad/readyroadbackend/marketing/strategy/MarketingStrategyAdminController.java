package com.readyroad.readyroadbackend.marketing.strategy;

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
@RequestMapping("/api/admin/marketing/strategy")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class MarketingStrategyAdminController {

    private final MarketingStrategyReadService readService;
    private final MarketingStrategyContextService contextService;
    private final MarketingStrategyChangeService changeService;

    @GetMapping
    public MarketingStrategySnapshot snapshot() {
        return readService.snapshot();
    }

    @PostMapping("/context/resolve")
    public MarketingStrategyContext resolve(@Valid @RequestBody MarketingStrategyContextRequest request) {
        return contextService.resolve(request);
    }

    @PostMapping("/change-requests")
    public ResponseEntity<MarketingTaskLifecycleResponse> requestChange(
            @Valid @RequestBody StrategyChangeRequest request,
            Principal principal) {
        var result = changeService.requestChange(request, principal.getName());
        return ResponseEntity.accepted().body(MarketingTaskLifecycleResponse.from(result.task()));
    }
}
