package com.tunetown.repository;

import com.tunetown.model.Post;
import com.tunetown.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p FROM Post p")
    Page<Post> getAllPosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.author.id = ?1")
    Page<Post> getPostByAuthorId(UUID authorId, Pageable pageable);
}
