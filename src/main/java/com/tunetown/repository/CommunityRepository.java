package com.tunetown.repository;

import com.tunetown.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Integer> {
    @Query("SELECT c FROM Community c")
    List<Community> getAllCommunity();

    @Query("SELECT c FROM Community c WHERE c.communityId = ?1")
    Community getCommunityById(int hostId);

    @Query("SELECT c FROM Community c WHERE c.communityName LIKE %:communityName%")
    List<Community> searchCommunityByName(String communityName);
}
