package com.tunetown.repository;

import com.tunetown.model.ChatDeleted;
import com.tunetown.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ChatDeletedRepository extends JpaRepository<ChatDeleted, Integer> {
    @Query("SELECT cd FROM ChatDeleted cd WHERE cd.user = ?1 AND cd.sentUser = ?2")
    ChatDeleted getChatDeleted(User userId, User sentUserId);
}
