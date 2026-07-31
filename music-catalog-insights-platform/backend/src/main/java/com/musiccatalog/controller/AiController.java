package com.musiccatalog.controller;

import com.musiccatalog.dto.response.AiInsightsResponse;
import com.musiccatalog.security.CurrentUserId;
import com.musiccatalog.service.AiInsightsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Insights", description = "Generates Music Taste Insights from the user's saved library using Gemini")
public class AiController {

    private final AiInsightsService aiInsightsService;

    @PostMapping("/insights")
    public ResponseEntity<AiInsightsResponse> generateInsights(@CurrentUserId Long userId) {
        return ResponseEntity.ok(aiInsightsService.generateInsights(userId));
    }
}
