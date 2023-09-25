package com.tunetown.repository;

import com.tunetown.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    @Query("SELECT p FROM Playlist p WHERE p.user.id = ?1")
    List<Playlist> getAllPlaylistsByUserId(int userId);
}
