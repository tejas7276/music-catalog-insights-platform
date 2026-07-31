package com.musiccatalog.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.musiccatalog.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Thin client around the public iTunes Search API. This is the ONLY place in the
 * application that talks to Apple's servers - the frontend never calls iTunes directly.
 */
@Slf4j
@Component
public class ItunesClient {

    private final WebClient webClient;

    public ItunesClient(@Qualifier("itunesWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public ItunesSearchResponse search(String term, String entity, int limit) {
        String uri = UriComponentsBuilder.fromPath("/search")
                .queryParam("term", term)
                .queryParam("entity", entity)
                .queryParam("limit", limit)
                .queryParam("media", "music")
                .build()
                .toUriString();

        try {
            ItunesSearchResponse response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(ItunesSearchResponse.class)
                    .block();
            return response != null ? response : new ItunesSearchResponse(0, List.of());
        } catch (Exception ex) {
            log.error("Failed to call iTunes Search API for term '{}': {}", term, ex.getMessage());
            throw new ExternalApiException("Unable to reach the iTunes Search API", ex);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ItunesSearchResponse(int resultCount, List<ItunesTrack> results) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ItunesTrack(
            Long trackId,
            Long collectionId,
            String trackName,
            String collectionName,
            String artistName,
            String primaryGenreName,
            String releaseDate,
            Integer trackTimeMillis,
            String artworkUrl100,
            Double trackPrice,
            String previewUrl
    ) {}
}
