package com.tunetown.repository;

import com.tunetown.model.ChatList;
import com.tunetown.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ChatListRepository extends JpaRepository<ChatList, Integer> {
    @Query("SELECT c FROM ChatList c WHERE c.user = ?1")
    ChatList getChatListByUser(User user);
}
