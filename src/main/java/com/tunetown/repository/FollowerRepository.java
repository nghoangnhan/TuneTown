package com.tunetown.repository;

import com.tunetown.model.Follower;
import com.tunetown.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowerRepository extends JpaRepository<Follower, Integer> {
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.subject.id = ?1")
    int getNumberOfFollowers(UUID subjectId);

    @Query("SELECT COUNT(f) FROM Follower f WHERE f.follower.id = ?1")
    int getNumberOfFollowing(UUID followerId);

    @Query("SELECT f.follower FROM Follower f " +
            "WHERE f.subject.id = ?1 " +
            "AND f.follower.id IN (SELECT f1.follower.id FROM Follower f1 WHERE f1.subject.id = ?2)")
    List<User> getListOfRelatedUsers(UUID subject, UUID followerId);

    @Query("SELECT f FROM Follower f WHERE f.follower.id = ?1 AND f.subject.id = ?2")
    Optional<Follower> getFollowerObject(UUID followerId, UUID subjectId);
}
