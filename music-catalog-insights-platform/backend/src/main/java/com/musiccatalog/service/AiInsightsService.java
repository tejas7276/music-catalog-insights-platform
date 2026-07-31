package com.musiccatalog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musiccatalog.client.GeminiClient;
import com.musiccatalog.dto.response.AiInsightsResponse;
import com.musiccatalog.entity.SavedSong;
import com.musiccatalog.exception.AiServiceException;
import com.musiccatalog.exception.ResourceNotFoundException;
import com.musiccatalog.repository.SavedSongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates "Music Taste Insights" by sending ONLY the authenticated user's saved
 * library to Gemini and asking for a strictly-structured JSON analysis. The prompt
 * explicitly instructs the model not to invent songs, artists, or facts that are not
 * present in the supplied library.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiInsightsService {

    private static final int MIN_LIBRARY_SIZE = 3;

    private final SavedSongRepository savedSongRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public AiInsightsResponse generateInsights(Long userId) {
        List<SavedSong> library = savedSongRepository.findByUserId(userId);

        if (library.size() < MIN_LIBRARY_SIZE) {
            throw new ResourceNotFoundException(
                    "Save at least " + MIN_LIBRARY_SIZE + " songs to your library before generating AI insights");
        }

        String prompt = buildPrompt(library);
        String rawJson = geminiClient.generateContent(prompt);

        try {
            AiInsightsResponse parsed = objectMapper.readValue(rawJson, AiInsightsResponse.class);
            return new AiInsightsResponse(
                    parsed.listeningSummary(),
                    parsed.favouriteGenres(),
                    parsed.favouriteArtists(),
                    parsed.moodAnalysis(),
                    parsed.releaseEraPreference(),
                    parsed.recommendations(),
                    parsed.personalizedSuggestions(),
                    library.size()
            );
        } catch (Exception ex) {
            log.error("Failed to parse Gemini response as structured insights: {}", ex.getMessage());
            throw new AiServiceException("Failed to parse AI insights response", ex);
        }
    }

    private String buildPrompt(List<SavedSong> library) {
        String libraryJson = library.stream()
                .map(song -> String.format(
                        "{\"title\":\"%s\",\"artist\":\"%s\",\"genre\":\"%s\",\"releaseDate\":\"%s\",\"rating\":%s}",
                        escape(song.getTitle()),
                        escape(song.getArtistName()),
                        escape(song.getGenre()),
                        song.getReleaseDate() != null ? song.getReleaseDate().toString() : "unknown",
                        song.getUserRating() != null ? song.getUserRating() : "null"
                ))
                .collect(Collectors.joining(",", "[", "]"));

        return """
                You are a music analyst. You are given a user's saved song library as a JSON array,
                where each entry has title, artist, genre, releaseDate, and an optional 1-5 rating.

                Library:
                %s

                Using ONLY the songs, artists, genres, and ratings present in this library, produce a
                strictly valid JSON object (no markdown fences, no commentary) matching EXACTLY this shape:

                {
                  "listeningSummary": string (2-3 sentences summarizing the user's taste),
                  "favouriteGenres": string[] (up to 5 genres, most prominent first, derived only from the library),
                  "favouriteArtists": string[] (up to 5 artists, most prominent first, derived only from the library),
                  "moodAnalysis": string (1-2 sentences inferring overall mood/energy from genres and ratings),
                  "releaseEraPreference": string (1 sentence describing which decades/years are best represented),
                  "recommendations": [
                    { "title": string, "artist": string, "reason": string }
                  ] (exactly 5 NEW song recommendations similar in style to the library; these should be plausible
                     real-world songs but must NOT already appear in the library),
                  "personalizedSuggestions": string (2-3 sentences of actionable suggestions for the user,
                     e.g. genres to explore next, based strictly on patterns in the library)
                }

                Do not invent facts about songs that are in the library - only summarize and analyze what is given.
                Return ONLY the JSON object.
                """.formatted(libraryJson);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
