package com.musiccatalog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
    name = "saved_songs",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_apple_catalog_id",
        columnNames = {"user_id", "apple_catalog_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "apple_catalog_id", nullable = false)
    private Long appleCatalogId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "artist_name", nullable = false, length = 255)
    private String artistName;

    @Column(length = 120)
    private String genre;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    /** Duration of the track, in milliseconds, as returned by iTunes (trackTimeMillis). */
    @Column(name = "duration_millis")
    private Integer durationMillis;

    @Column(name = "artwork_url", length = 500)
    private String artworkUrl;

    @Column(name = "user_rating")
    private Integer userRating;

    @Column(name = "user_notes", length = 2000)
    private String userNotes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
