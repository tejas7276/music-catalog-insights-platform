package com.musiccatalog.service;

import com.musiccatalog.dto.request.SaveSongRequest;
import com.musiccatalog.dto.request.UpdateSongRequest;
import com.musiccatalog.dto.response.PageResponse;
import com.musiccatalog.dto.response.SavedSongResponse;
import com.musiccatalog.entity.SavedSong;
import com.musiccatalog.entity.User;
import com.musiccatalog.exception.DuplicateResourceException;
import com.musiccatalog.exception.ResourceNotFoundException;
import com.musiccatalog.mapper.SongMapper;
import com.musiccatalog.repository.SavedSongRepository;
import com.musiccatalog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryService {

    private final SavedSongRepository savedSongRepository;
    private final UserRepository userRepository;
    private final SongMapper songMapper;

    @Transactional(readOnly = true)
    public PageResponse<SavedSongResponse> getLibrary(Long userId, Pageable pageable) {
        Page<SavedSongResponse> page = savedSongRepository.findByUserId(userId, pageable)
                .map(songMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public SavedSongResponse getSong(Long userId, Long songId) {
        SavedSong song = findOwnedSong(userId, songId);
        return songMapper.toResponse(song);
    }

    @Transactional
    public SavedSongResponse saveSong(Long userId, SaveSongRequest request) {
        if (savedSongRepository.existsByUserIdAndAppleCatalogId(userId, request.appleCatalogId())) {
            throw new DuplicateResourceException("This song is already saved in your library");
        }

        User user = userRepository.getReferenceById(userId);
        SavedSong song = songMapper.toEntity(request, user);
        SavedSong saved = savedSongRepository.save(song);

        log.info("Song saved to library: userId={}, songId={}", userId, saved.getId());
        return songMapper.toResponse(saved);
    }

    @Transactional
    public SavedSongResponse updateSong(Long userId, Long songId, UpdateSongRequest request) {
        SavedSong song = findOwnedSong(userId, songId);

        if (request.userRating() != null) {
            song.setUserRating(request.userRating());
        }
        if (request.userNotes() != null) {
            song.setUserNotes(request.userNotes());
        }

        SavedSong updated = savedSongRepository.save(song);
        log.info("Song updated: userId={}, songId={}", userId, songId);
        return songMapper.toResponse(updated);
    }

    @Transactional
    public void deleteSong(Long userId, Long songId) {
        findOwnedSong(userId, songId);
        savedSongRepository.deleteByIdAndUserId(songId, userId);
        log.info("Song deleted: userId={}, songId={}", userId, songId);
    }

    private SavedSong findOwnedSong(Long userId, Long songId) {
        return savedSongRepository.findByIdAndUserId(songId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found in your library: id=" + songId));
    }
}
