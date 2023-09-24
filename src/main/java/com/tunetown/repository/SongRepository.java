package com.tunetown.repository;

import com.tunetown.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Integer> {
    @Query("SELECT s FROM Song s WHERE s.id = ?1")
    public Optional<Song> getSongById(int id);

    @Query("SELECT s FROM Song s WHERE s.status = ?1")
    Page<Song> getByStatus(int status, Pageable pageable);

}