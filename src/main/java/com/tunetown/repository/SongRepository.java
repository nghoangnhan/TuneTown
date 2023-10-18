package com.tunetown.repository;

import com.tunetown.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Integer> {
    @Query("SELECT s FROM Song s WHERE s.id = ?1")
    Optional<Song> getSongById(int id);

    @Query("SELECT s FROM Song s WHERE s.status = ?1")
    Page<Song> getByStatus(int status, Pageable pageable);

    // Just get songs that have status = 1, 1: enable
    @Query("SELECT s FROM Song s WHERE s.status = ?1")
    Page<List<Song>> findByStatus(int status, Pageable pageable);


    @Query("SELECT s FROM Song s JOIN s.artists a WHERE s.status = 0 AND ((s.songName LIKE %:songName% OR a.userName LIKE %:songName%)" +
            " OR (s.songName LIKE %:songName% AND a.userName LIKE %:artistName%) " +
            "OR (CONCAT(s.songName, '', a.userName) LIKE %:songName%) " +
            "OR (CONCAT(a.userName, '', s.songName) LIKE %:songName%))")
    List<Song> findSongByNameOrArtist(String songName, String artistName);

}
