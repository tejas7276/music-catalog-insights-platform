package com.musiccatalog.dto.response;

import java.time.LocalDate;

public record SongSearchResultResponse(
        Long appleCatalogId,
        String title,
        String artistName,
        String genre,
        LocalDate releaseDate,
        Integer durationMillis,
        String artworkUrl,
        String collectionName,
        Double trackPrice,
        String previewUrl
) {}
