package com.tunetown.repository;

import com.tunetown.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    @Query("SELECT n FROM Notification n")
    List<Notification> getAllNotification();

    @Query("SELECT n FROM Notification n WHERE n.receiveUserId = ?1 AND n.type = 0")
    Notification getNotificationByUserId(int userId);
}
