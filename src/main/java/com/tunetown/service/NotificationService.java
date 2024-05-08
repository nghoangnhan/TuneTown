package com.tunetown.service;

import com.tunetown.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

//    @MessageMapping("/private-message")
//    public void receiveMessage(@Payload Message message) {
//        messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiveUserId()), "", message); // /chat/userId/private
//    }
}
