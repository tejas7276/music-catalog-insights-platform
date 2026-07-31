package com.musiccatalog.service;

import com.musiccatalog.config.CacheConfig;
import com.musiccatalog.dto.response.AnalyticsResponse;
import com.musiccatalog.dto.response.SavedSongResponse;
import com.musiccatalog.entity.SavedSong;
import com.musiccatalog.mapper.SongMapper;
import com.musiccatalog.repository.SavedSongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aggregates a user's saved library into the data points required by the
 * Recharts-powered analytics dashboard on the frontend.
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final SavedSongRepository savedSongRepository;
    private final SongMapper songMapper;

    @Cacheable(value = CacheConfig.ANALYTICS_CACHE, key = "#userId")
    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics(Long userId) {
        List<SavedSong> songs = savedSongRepository.findByUserId(userId);

        return new AnalyticsResponse(
                buildSummaryCards(songs),
                buildGenreDistribution(songs),
                buildTopArtists(songs),
                buildSongsAddedOverTime(songs),
                buildRatingsDistribution(songs),
                buildReleaseYearDistribution(songs),
                buildAverageRatingByGenre(songs)
        );
    }

    private AnalyticsResponse.SummaryCards buildSummaryCards(List<SavedSong> songs) {
        long totalSongs = songs.size();

        java.util.OptionalDouble averageOpt = songs.stream()
                .map(SavedSong::getUserRating)
                .filter(rating -> rating != null)
                .mapToInt(Integer::intValue)
                .average();
        Double averageRating = averageOpt.isPresent()
                ? Math.round(averageOpt.getAsDouble() * 100.0) / 100.0
                : null;

        long uniqueArtists = songs.stream()
                .map(SavedSong::getArtistName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .count();

        long uniqueGenres = songs.stream()
                .map(SavedSong::getGenre)
                .filter(genre -> genre != null && !genre.isBlank())
                .distinct()
                .count();

        SavedSongResponse highestRated = songs.stream()
                .filter(song -> song.getUserRating() != null)
                .max(Comparator.comparingInt(SavedSong::getUserRating))
                .map(songMapper::toResponse)
                .orElse(null);

        SavedSongResponse latestAdded = songs.stream()
                .max(Comparator.comparing(SavedSong::getCreatedAt))
                .map(songMapper::toResponse)
                .orElse(null);

        return new AnalyticsResponse.SummaryCards(
                totalSongs, averageRating, uniqueArtists, uniqueGenres, highestRated, latestAdded
        );
    }

    private List<AnalyticsResponse.GenreCount> buildGenreDistribution(List<SavedSong> songs) {
        return songs.stream()
                .map(song -> song.getGenre() == null || song.getGenre().isBlank() ? "Unknown" : song.getGenre())
                .collect(Collectors.groupingBy(genre -> genre, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new AnalyticsResponse.GenreCount(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(AnalyticsResponse.GenreCount::count).reversed())
                .toList();
    }

    private List<AnalyticsResponse.ArtistCount> buildTopArtists(List<SavedSong> songs) {
        return songs.stream()
                .collect(Collectors.groupingBy(SavedSong::getArtistName, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new AnalyticsResponse.ArtistCount(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(AnalyticsResponse.ArtistCount::count).reversed())
                .limit(10)
                .toList();
    }

    private List<AnalyticsResponse.TimeSeriesPoint> buildSongsAddedOverTime(List<SavedSong> songs) {
        Map<String, Long> byDay = songs.stream()
                .collect(Collectors.groupingBy(
                        song -> DAY_FORMATTER.format(song.getCreatedAt().atZone(java.time.ZoneOffset.UTC)),
                        Collectors.counting()
                ));

        return byDay.entrySet().stream()
                .map(entry -> new AnalyticsResponse.TimeSeriesPoint(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(AnalyticsResponse.TimeSeriesPoint::date))
                .toList();
    }

    private List<AnalyticsResponse.RatingCount> buildRatingsDistribution(List<SavedSong> songs) {
        Map<Integer, Long> byRating = songs.stream()
                .filter(song -> song.getUserRating() != null)
                .collect(Collectors.groupingBy(SavedSong::getUserRating, Collectors.counting()));

        return java.util.stream.IntStream.rangeClosed(1, 5)
                .mapToObj(rating -> new AnalyticsResponse.RatingCount(rating, byRating.getOrDefault(rating, 0L)))
                .toList();
    }

    private List<AnalyticsResponse.YearCount> buildReleaseYearDistribution(List<SavedSong> songs) {
        return songs.stream()
                .filter(song -> song.getReleaseDate() != null)
                .collect(Collectors.groupingBy(
                        song -> String.valueOf(song.getReleaseDate().getYear()),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new AnalyticsResponse.YearCount(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(AnalyticsResponse.YearCount::year))
                .toList();
    }

    private List<AnalyticsResponse.GenreAverageRating> buildAverageRatingByGenre(List<SavedSong> songs) {
        Map<String, List<SavedSong>> byGenre = songs.stream()
                .filter(song -> song.getUserRating() != null)
                .collect(Collectors.groupingBy(
                        song -> song.getGenre() == null || song.getGenre().isBlank() ? "Unknown" : song.getGenre()
                ));

        return byGenre.entrySet().stream()
                .map(entry -> {
                    double avg = entry.getValue().stream()
                            .mapToInt(SavedSong::getUserRating)
                            .average()
                            .orElse(0.0);
                    return new AnalyticsResponse.GenreAverageRating(entry.getKey(), Math.round(avg * 100.0) / 100.0);
                })
                .sorted(Comparator.comparingDouble(AnalyticsResponse.GenreAverageRating::averageRating).reversed())
                .toList();
    }
}
