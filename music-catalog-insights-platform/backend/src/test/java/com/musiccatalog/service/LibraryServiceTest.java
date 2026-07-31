package com.musiccatalog.service;

import com.musiccatalog.dto.request.SaveSongRequest;
import com.musiccatalog.dto.response.SavedSongResponse;
import com.musiccatalog.entity.SavedSong;
import com.musiccatalog.entity.User;
import com.musiccatalog.exception.DuplicateResourceException;
import com.musiccatalog.exception.ResourceNotFoundException;
import com.musiccatalog.mapper.SongMapper;
import com.musiccatalog.repository.SavedSongRepository;
import com.musiccatalog.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private SavedSongRepository savedSongRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SongMapper songMapper;

    @InjectMocks
    private LibraryService libraryService;

    @Test
    void saveSong_shouldThrowDuplicate_whenSongAlreadySaved() {
        SaveSongRequest request = new SaveSongRequest(
                123L, "Shape of You", "Ed Sheeran", "Pop", LocalDate.of(2017, 1, 6), 240000, "http://art.jpg", null, null);

        when(savedSongRepository.existsByUserIdAndAppleCatalogId(1L, 123L)).thenReturn(true);

        assertThatThrownBy(() -> libraryService.saveSong(1L, request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(savedSongRepository, never()).save(any());
    }

    @Test
    void saveSong_shouldPersist_whenSongIsNew() {
        SaveSongRequest request = new SaveSongRequest(
                123L, "Shape of You", "Ed Sheeran", "Pop", LocalDate.of(2017, 1, 6), 240000, "http://art.jpg", null, null);

        User user = User.builder().id(1L).build();
        SavedSong entity = SavedSong.builder().id(10L).appleCatalogId(123L).user(user).build();
        SavedSongResponse expectedResponse = new SavedSongResponse(
                10L, 123L, "Shape of You", "Ed Sheeran", "Pop", LocalDate.of(2017, 1, 6),
                240000, "http://art.jpg", null, null, null, null);

        when(savedSongRepository.existsByUserIdAndAppleCatalogId(1L, 123L)).thenReturn(false);
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(songMapper.toEntity(request, user)).thenReturn(entity);
        when(savedSongRepository.save(entity)).thenReturn(entity);
        when(songMapper.toResponse(entity)).thenReturn(expectedResponse);

        SavedSongResponse response = libraryService.saveSong(1L, request);

        assertThat(response.title()).isEqualTo("Shape of You");
        verify(savedSongRepository).save(entity);
    }

    @Test
    void deleteSong_shouldThrow_whenSongNotOwnedByUser() {
        when(savedSongRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> libraryService.deleteSong(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(savedSongRepository, never()).deleteByIdAndUserId(anyLong(), anyLong());
    }
}
