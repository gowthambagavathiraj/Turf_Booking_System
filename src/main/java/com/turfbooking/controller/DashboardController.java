package com.turfbooking.controller;

import com.turfbooking.dto.StatsResponse;
import com.turfbooking.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/public-stats")
    public ResponseEntity<StatsResponse> getPublicStats() {
        return ResponseEntity.ok(dashboardService.getPublicStats());
    }

    @GetMapping("/owner-stats")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<StatsResponse> getOwnerStats() {
        return ResponseEntity.ok(dashboardService.getOwnerStats());
    }
}
