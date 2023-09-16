package com.tunetown.repository;

import com.tunetown.model.Song;
import com.tunetown.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Integer> {
    @Query("SELECT s FROM Song s WHERE s.id = ?1 AND s.status = 1")
    public Optional<Song> getSongById(int id);

    // Just get songs that have status = 1, 1: enable
    public List<Song> findByStatus(int status);

}
