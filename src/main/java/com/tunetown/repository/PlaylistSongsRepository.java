package com.tunetown.repository;

import com.tunetown.model.PlaylistSongs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlaylistSongsRepository extends JpaRepository<PlaylistSongs, Integer> {
    @Query("SELECT COUNT(ps) FROM PlaylistSongs ps WHERE ps.playlist.id = ?1")
    int getNumberOfSongsInPlaylist(int playlistId);

    @Query("SELECT ps FROM PlaylistSongs  ps where ps.playlist.id = ?1")
    List<PlaylistSongs> getPlaylistSongsById(int playlistId);

    @Query("SELECT ps FROM PlaylistSongs  ps WHERE ps.song.id = ?1 AND ps.playlist.id = ?2")
    Optional<PlaylistSongs> getPlaylistSong(int songId, int playlistId);
}
