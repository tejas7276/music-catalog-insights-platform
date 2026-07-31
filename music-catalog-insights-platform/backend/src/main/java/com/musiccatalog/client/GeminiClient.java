package com.musiccatalog.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.musiccatalog.exception.AiServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Client for Google's Gemini generative language API. Used exclusively to turn a user's
 * saved song library into structured "Music Taste Insights".
 */
@Slf4j
@Component
public class GeminiClient {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            @Qualifier("geminiWebClient") WebClient webClient,
            @Value("${ai.gemini.api-key}") String apiKey,
            @Value("${ai.gemini.model}") String model
    ) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.model = model;
    }

    public String generateContent(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiServiceException("Gemini API key is not configured");
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.4,
                        "responseMimeType", "application/json"
                )
        );

        try {
            GeminiResponse response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/{model}:generateContent")
                            .queryParam("key", apiKey)
                            .build(model))
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block();

            if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
                throw new AiServiceException("Gemini returned an empty response");
            }

            return response.candidates().get(0).content().parts().get(0).text();
        } catch (AiServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Gemini API call failed: {}", ex.getMessage());
            throw new AiServiceException("Failed to generate AI insights", ex);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GeminiResponse(List<Candidate> candidates) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidate(Content content) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Content(List<Part> parts) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Part(String text) {}
}
