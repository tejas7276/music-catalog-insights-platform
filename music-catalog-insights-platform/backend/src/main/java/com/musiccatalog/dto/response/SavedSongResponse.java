package com.musiccatalog.dto.response;

import java.time.Instant;
import java.time.LocalDate;

public record SavedSongResponse(
        Long id,
        Long appleCatalogId,
        String title,
        String artistName,
        String genre,
        LocalDate releaseDate,
        Integer durationMillis,
        String artworkUrl,
        Integer userRating,
        String userNotes,
        Instant createdAt,
        Instant updatedAt
) {}
