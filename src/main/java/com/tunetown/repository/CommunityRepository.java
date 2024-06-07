package com.tunetown.repository;

import com.tunetown.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommunityRepository extends JpaRepository<Community, UUID> {
    @Query("SELECT c FROM Community c")
    List<Community> getAllCommunity();

    @Query("SELECT c FROM Community c WHERE c.communityId = ?1")
    Community getCommunityById(UUID hostId);

    @Query("SELECT c FROM Community c WHERE c.communityName LIKE %:communityName%")
    List<Community> searchCommunityByName(String communityName);

    @Query("SELECT Count(c) FROM Community c Where c.communityId = ?1")
    int checkCommunityExist(UUID artistId);
}
