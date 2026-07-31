package com.musiccatalog.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SaveSongRequest(

        @NotNull(message = "Apple catalog id is required")
        Long appleCatalogId,

        @NotBlank(message = "Title is required")
        @Size(max = 255)
        String title,

        @NotBlank(message = "Artist name is required")
        @Size(max = 255)
        String artistName,

        @Size(max = 120)
        String genre,

        LocalDate releaseDate,

        Integer durationMillis,

        @Size(max = 500)
        String artworkUrl,

        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        Integer userRating,

        @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
        String userNotes
) {}
