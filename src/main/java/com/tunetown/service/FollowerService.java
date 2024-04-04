package com.tunetown.service;

import com.tunetown.model.Follower;
import com.tunetown.model.User;
import com.tunetown.repository.FollowerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        Optional<Follower> optionalFollower = followerRepository.getFollowerObject(follower.getFollower().getId(), follower.getSubject().getId());
        if (optionalFollower.isPresent())
            followerRepository.delete(optionalFollower.get());
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error on unfollowing a user");
    }
    public int getNumberOfFollowers(int subjectId) {
        return followerRepository.getNumberOfFollowers(subjectId);
    }
    public int getNumberOfFollowing(int followerId) {
        return followerRepository.getNumberOfFollowing(followerId);
    }
    public List<User> getListOfRelatedUsers(int subjectId, int followerId) {
        return followerRepository.getListOfRelatedUsers(subjectId, followerId);
    }
}