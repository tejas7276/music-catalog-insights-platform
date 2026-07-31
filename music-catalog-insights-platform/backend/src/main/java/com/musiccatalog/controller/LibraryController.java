package com.musiccatalog.controller;

import com.musiccatalog.dto.request.SaveSongRequest;
import com.musiccatalog.dto.request.UpdateSongRequest;
import com.musiccatalog.dto.response.PageResponse;
import com.musiccatalog.dto.response.SavedSongResponse;
import com.musiccatalog.security.CurrentUserId;
import com.musiccatalog.service.LibraryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@Tag(name = "Library", description = "Manage the authenticated user's saved song library")
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping
    public ResponseEntity<PageResponse<SavedSongResponse>> getLibrary(
            @CurrentUserId Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(libraryService.getLibrary(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavedSongResponse> getSong(@CurrentUserId Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(libraryService.getSong(userId, id));
    }

    @PostMapping
    public ResponseEntity<SavedSongResponse> saveSong(
            @CurrentUserId Long userId,
            @Valid @RequestBody SaveSongRequest request
    ) {
        SavedSongResponse response = libraryService.saveSong(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedSongResponse> updateSong(
            @CurrentUserId Long userId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateSongRequest request
    ) {
        return ResponseEntity.ok(libraryService.updateSong(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@CurrentUserId Long userId, @PathVariable Long id) {
        libraryService.deleteSong(userId, id);
        return ResponseEntity.noContent().build();
    }
}
