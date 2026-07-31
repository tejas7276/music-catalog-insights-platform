package com.musiccatalog.repository;

import com.musiccatalog.entity.SavedSong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedSongRepository extends JpaRepository<SavedSong, Long> {

    Page<SavedSong> findByUserId(Long userId, Pageable pageable);

    List<SavedSong> findByUserId(Long userId);

    Optional<SavedSong> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndAppleCatalogId(Long userId, Long appleCatalogId);

    void deleteByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);
}
