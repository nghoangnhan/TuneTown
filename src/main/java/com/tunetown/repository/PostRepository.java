package com.tunetown.repository;

import com.tunetown.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p FROM Post p " +
            "WHERE p.author.id IN (SELECT f.subject.id FROM Follower f WHERE f.follower.id = ?1) " +
            "ORDER BY p.postTime DESC")
    Page<Post> getAllPosts(UUID userId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.author.id = ?1 ORDER BY p.postTime DESC")
    Page<Post> getPostByAuthorId(UUID authorId, Pageable pageable);
}
