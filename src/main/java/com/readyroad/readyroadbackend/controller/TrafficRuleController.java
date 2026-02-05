package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.TrafficRule;
import com.readyroad.readyroadbackend.domain.repository.TrafficRuleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Traffic Rules Controller
 * Provides access to traffic rules and regulations
 */
@RestController
@RequestMapping("/api/traffic-rules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Traffic Rules", description = "Traffic rules and regulations API")
public class TrafficRuleController {

    private final TrafficRuleRepository trafficRuleRepository;

    @GetMapping
    @Operation(
        summary = "Get all traffic rules",
        description = "Returns a list of all traffic rules"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved traffic rules",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrafficRule.class)
            )
        )
    })
    public ResponseEntity<List<TrafficRule>> getAllTrafficRules() {
        log.info("GET /api/traffic-rules - Fetching all traffic rules");
        List<TrafficRule> rules = trafficRuleRepository.findAll();
        log.info("Found {} traffic rules", rules.size());
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get traffic rule by ID",
        description = "Returns a specific traffic rule by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved traffic rule",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrafficRule.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Traffic rule not found",
            content = @Content
        )
    })
    public ResponseEntity<TrafficRule> getTrafficRuleById(@PathVariable Long id) {
        log.info("GET /api/traffic-rules/{} - Fetching traffic rule", id);
        TrafficRule rule = trafficRuleRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Traffic rule not found: {}", id);
                return new RuntimeException("Traffic rule not found");
            });
        log.info("Traffic rule found: {}", rule.getRuleCode());
        return ResponseEntity.ok(rule);
    }

    @GetMapping("/category/{category}")
    @Operation(
        summary = "Get traffic rules by category",
        description = "Returns traffic rules for a specific category"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved traffic rules",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrafficRule.class)
            )
        )
    })
    public ResponseEntity<List<TrafficRule>> getTrafficRulesByCategory(@PathVariable String category) {
        log.info("GET /api/traffic-rules/category/{} - Fetching rules by category", category);
        List<TrafficRule> rules = trafficRuleRepository.findAllByCategoryAndIsActiveTrue(category);
        log.info("Found {} traffic rules for category {}", rules.size(), category);
        return ResponseEntity.ok(rules);
    }
}
