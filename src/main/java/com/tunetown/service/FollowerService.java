package com.tunetown.service;

import com.tunetown.model.Follower;
import com.tunetown.model.User;
import com.tunetown.repository.FollowerRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FollowerService {
    @Resource
    private FollowerRepository followerRepository;

    /**
     * Save information when a user follow other user
     */
    public void follow(Follower follower) {
        follower.setFollowedDate(LocalDate.now());
        followerRepository.save(follower);
    }
    public void unfollow(Follower follower) {
        followerRepository.delete(follower);
    }
    public int getNumberOfFollowers(UUID subjectId) {
        return followerRepository.getNumberOfFollowers(subjectId);
    }
    public int getNumberOfFollowing(UUID followerId) {
        return followerRepository.getNumberOfFollowing(followerId);
    }
    public List<User> getListOfRelatedUsers(UUID subjectId, UUID followerId) {
        return followerRepository.getListOfRelatedUsers(subjectId, followerId);
    }

    public Optional<Follower> getFollowInformation(UUID followerId, UUID subjectId) {
        return followerRepository.getFollowerObject(followerId, subjectId);
    }

    public List<Follower> getUserFollowing(UUID userId) {
        return followerRepository.getUserFollowing(userId);
    }

    public List<Follower> getUserFollowers(UUID userId) {
        return followerRepository.getUserFollowers(userId);
    }
}
