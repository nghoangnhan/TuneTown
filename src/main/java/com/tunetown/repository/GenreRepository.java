package com.tunetown.repository;

import com.tunetown.model.Genre;
import com.tunetown.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
    @Query("SELECT g FROM Genre g")
    List<Genre> getAllGenres();
}
