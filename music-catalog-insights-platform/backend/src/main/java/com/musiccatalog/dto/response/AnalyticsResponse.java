package com.musiccatalog.dto.response;

import java.util.List;
import java.util.Map;

public record AnalyticsResponse(
        SummaryCards summaryCards,
        List<GenreCount> genreDistribution,
        List<ArtistCount> topArtists,
        List<TimeSeriesPoint> songsAddedOverTime,
        List<RatingCount> ratingsDistribution,
        List<YearCount> releaseYearDistribution,
        List<GenreAverageRating> averageRatingByGenre
) {
    public record SummaryCards(
            long totalSongs,
            Double averageRating,
            long uniqueArtists,
            long uniqueGenres,
            SavedSongResponse highestRatedSong,
            SavedSongResponse latestAddedSong
    ) {}

    public record GenreCount(String genre, long count) {}

    public record ArtistCount(String artist, long count) {}

    public record TimeSeriesPoint(String date, long count) {}

    public record RatingCount(int rating, long count) {}

    public record YearCount(String year, long count) {}

    public record GenreAverageRating(String genre, double averageRating) {}
}
