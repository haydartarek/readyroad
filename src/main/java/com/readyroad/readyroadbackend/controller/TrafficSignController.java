package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.service.TrafficSignService;
import com.readyroad.readyroadbackend.util.RouteCodeNormalizer;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/traffic-signs")
@Tag(name = "Traffic Signs", description = "Traffic signs and road markings reference")
public class TrafficSignController {

    private final TrafficSignService trafficSignService;

    public TrafficSignController(TrafficSignService trafficSignService) {
        this.trafficSignService = trafficSignService;
    }

    @GetMapping
    public ResponseEntity<List<TrafficSignResponse>> getAllSigns(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "q", required = false) String query) {
        return ResponseEntity.ok(trafficSignService.getFilteredPublicSigns(categoryId, query));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TrafficSignResponse>> getSignsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(trafficSignService.getFilteredPublicSigns(categoryId, null));
    }

    @GetMapping("/{signCode}")
    public ResponseEntity<TrafficSignResponse> getSignByCode(@PathVariable String signCode) {
        if (RouteCodeNormalizer.isLegacyCodeWithoutDirectReplacement(signCode)) {
            return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                    .location(URI.create("/api/traffic-signs"))
                    .build();
        }
        return ResponseEntity.ok(trafficSignService.getSignByCode(signCode));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrafficSignResponse>> searchTrafficSigns(@RequestParam("q") String query) {
        return ResponseEntity.ok(trafficSignService.getFilteredPublicSigns(null, query));
    }
}
