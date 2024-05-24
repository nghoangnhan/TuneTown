package com.tunetown.repository;

import com.tunetown.model.ChatDeleted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ChatDeletedRepository extends JpaRepository<ChatDeleted, Integer> {
    @Query("SELECT cd FROM ChatDeleted cd WHERE cd.userId = ?1 AND cd.sentUserId = ?2")
    ChatDeleted getChatDeleted(UUID userId, UUID sentUserId);
}
