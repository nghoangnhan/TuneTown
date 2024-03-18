package com.tunetown.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Integer userId, String message) {
        messagingTemplate.convertAndSendToUser(userId.toString(), "/topic/notifications", message);
    }
}
