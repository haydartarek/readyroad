package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.service.TrafficSignService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/traffic-signs")
public class TrafficSignController {

    private final TrafficSignService trafficSignService;

    public TrafficSignController(TrafficSignService trafficSignService) {
        this.trafficSignService = trafficSignService;
    }

    @GetMapping
    public ResponseEntity<List<TrafficSignResponse>> getAllSigns() {
        return ResponseEntity.ok(trafficSignService.getAllActiveSigns());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TrafficSignResponse>> getSignsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(trafficSignService.getSignsByCategory(categoryId));
    }

    @GetMapping("/{signCode}")
    public ResponseEntity<TrafficSignResponse> getSignByCode(@PathVariable String signCode) {
        return ResponseEntity.ok(trafficSignService.getSignByCode(signCode));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrafficSignResponse>> searchTrafficSigns(@RequestParam("q") String query) {
        return ResponseEntity.ok(trafficSignService.searchTrafficSigns(query));
    }
}

