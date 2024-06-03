package com.tunetown.repository;

import com.tunetown.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    @Query("SELECT p FROM Playlist p WHERE p.user.id = ?1")
    List<Playlist> getAllPlaylistsByUserId(UUID userId);

    @Query("SELECT p FROM Playlist p " +
            "WHERE p.user.id = ?1 AND p.playlistType = 'Recommended' " +
            "ORDER BY p.playlistName ASC")
    List<Playlist> getUserRecommendedPlaylist(UUID userId);

    @Query("SELECT p FROM Playlist p WHERE p.user.id = ?1 AND p.playlistType='Public'")
    List<Playlist> getPublicPlaylist(UUID userId);
}
