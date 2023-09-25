package com.tunetown.repository;

import com.tunetown.model.PlaylistSongs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlaylistSongsRepository extends JpaRepository<PlaylistSongs, Integer> {
    @Query("SELECT COUNT(ps) FROM PlaylistSongs ps WHERE ps.playlist.id = ?1")
    int getNumberOfSongsInPlaylist(int playlistId);
}
