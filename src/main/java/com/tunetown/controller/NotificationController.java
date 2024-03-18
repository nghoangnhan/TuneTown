package com.tunetown.controller;

import com.tunetown.model.Notification;
import com.tunetown.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/message/send")
    public ResponseEntity<String> sendNotification(@RequestBody Notification notification) {
        try {
            String notificationContent = "";
            if(notification.getType() == 0){
                notificationContent = "You have a new message!";
            }
            notificationService.sendNotification(notification.getReceiveUserId(), notificationContent);
            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send notification");
        }
    }
}
