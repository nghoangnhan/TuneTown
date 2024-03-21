package com.tunetown.repository;

import com.tunetown.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("SELECT m FROM Message m")
    List<Message> getAllMessages();

    @Query("SELECT m FROM Message m WHERE (m.sendUserId = ?1 AND m.receiveUserId = ?2) OR (m.sendUserId = ?2 AND m.receiveUserId = ?1)")
    List<Message> getMessageByUserId(int sendUserId, int receiveUserId);

    @Query("SELECT m FROM Message m WHERE CONCAT(m.content) LIKE %?1% AND ((m.sendUserId = ?2 AND m.receiveUserId = ?3) " +
            "OR (m.sendUserId = ?3 AND m.receiveUserId = ?2))")
    List<Message> findMessageByContent(String content, int userId, int sentUserId);
}