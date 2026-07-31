package com.musiccatalog.mapper;

import com.musiccatalog.dto.request.SaveSongRequest;
import com.musiccatalog.dto.response.SavedSongResponse;
import com.musiccatalog.dto.response.SongSearchResultResponse;
import com.musiccatalog.entity.SavedSong;
import com.musiccatalog.entity.User;
import org.springframework.stereotype.Component;

@Component
public class SongMapper {

    public SavedSong toEntity(SaveSongRequest request, User user) {
        return SavedSong.builder()
                .appleCatalogId(request.appleCatalogId())
                .title(request.title())
                .artistName(request.artistName())
                .genre(request.genre())
                .releaseDate(request.releaseDate())
                .durationMillis(request.durationMillis())
                .artworkUrl(request.artworkUrl())
                .userRating(request.userRating())
                .userNotes(request.userNotes())
                .user(user)
                .build();
    }

    public SavedSongResponse toResponse(SavedSong song) {
        return new SavedSongResponse(
                song.getId(),
                song.getAppleCatalogId(),
                song.getTitle(),
                song.getArtistName(),
                song.getGenre(),
                song.getReleaseDate(),
                song.getDurationMillis(),
                song.getArtworkUrl(),
                song.getUserRating(),
                song.getUserNotes(),
                song.getCreatedAt(),
                song.getUpdatedAt()
        );
    }

    public SaveSongRequest fromSearchResult(SongSearchResultResponse result, Integer userRating, String userNotes) {
        return new SaveSongRequest(
                result.appleCatalogId(),
                result.title(),
                result.artistName(),
                result.genre(),
                result.releaseDate(),
                result.durationMillis(),
                result.artworkUrl(),
                userRating,
                userNotes
        );
    }
}
