package com.musiccatalog.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateSongRequest(

        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        Integer userRating,

        @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
        String userNotes
) {}
