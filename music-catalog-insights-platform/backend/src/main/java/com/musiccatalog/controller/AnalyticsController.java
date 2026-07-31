package com.musiccatalog.controller;

import com.musiccatalog.dto.response.AnalyticsResponse;
import com.musiccatalog.security.CurrentUserId;
import com.musiccatalog.service.AnalyticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Aggregated statistics over the authenticated user's saved library")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<AnalyticsResponse> getAnalytics(@CurrentUserId Long userId) {
        return ResponseEntity.ok(analyticsService.getAnalytics(userId));
    }
}
