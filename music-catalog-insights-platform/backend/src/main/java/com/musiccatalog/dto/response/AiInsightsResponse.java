package com.musiccatalog.dto.response;

import java.util.List;

public record AiInsightsResponse(
        String listeningSummary,
        List<String> favouriteGenres,
        List<String> favouriteArtists,
        String moodAnalysis,
        String releaseEraPreference,
        List<SongRecommendation> recommendations,
        String personalizedSuggestions,
        int librarySizeAnalyzed
) {
    public record SongRecommendation(
            String title,
            String artist,
            String reason
    ) {}
}
