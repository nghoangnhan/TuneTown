package com.tunetown.repository;

import com.tunetown.model.ChatList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatListRepository extends JpaRepository<ChatList, Integer> {
    @Query("SELECT c FROM ChatList c WHERE c.userId = ?1")
    ChatList getChatListByUserId(int userId);
}
