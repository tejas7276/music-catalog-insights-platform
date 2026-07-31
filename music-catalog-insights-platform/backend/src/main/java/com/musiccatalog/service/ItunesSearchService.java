package com.musiccatalog.service;

import com.musiccatalog.client.ItunesClient;
import com.musiccatalog.dto.response.SearchResponse;
import com.musiccatalog.dto.response.SongSearchResultResponse;
import com.musiccatalog.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Proxies song searches to the iTunes Search API. The frontend never calls Apple directly;
 * every request flows through this backend service so responses can be validated, shaped,
 * and cached.
 */
@Service
@RequiredArgsConstructor
public class ItunesSearchService {

    private static final String SONG_ENTITY = "song";
    private static final int DEFAULT_LIMIT = 25;

    private final ItunesClient itunesClient;

    @Cacheable(value = CacheConfig.SEARCH_CACHE, key = "#query")
    public SearchResponse searchSongs(String query) {
        ItunesClient.ItunesSearchResponse itunesResponse = itunesClient.search(query, SONG_ENTITY, DEFAULT_LIMIT);

        List<SongSearchResultResponse> results = itunesResponse.results().stream()
                .filter(track -> track.trackId() != null && track.trackName() != null)
                .map(this::toSearchResult)
                .toList();

        return new SearchResponse(query, results.size(), results);
    }

    private SongSearchResultResponse toSearchResult(ItunesClient.ItunesTrack track) {
        return new SongSearchResultResponse(
                track.trackId(),
                track.trackName(),
                track.artistName(),
                track.primaryGenreName(),
                parseReleaseDate(track.releaseDate()),
                track.trackTimeMillis(),
                track.artworkUrl100(),
                track.collectionName(),
                track.trackPrice(),
                track.previewUrl()
        );
    }

    private LocalDate parseReleaseDate(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(rawDate.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
