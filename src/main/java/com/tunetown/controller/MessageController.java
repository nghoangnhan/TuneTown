package com.tunetown.controller;

import com.tunetown.model.ChatList;
import com.tunetown.model.Message;
import com.tunetown.service.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
@Slf4j
public class MessageController {
    @Resource
    MessageService messageService;
    @PostMapping(path = "/sendMessage")
    public ResponseEntity<String> sendMessage(@RequestBody Message message){
        if(messageService.sendMessage(message.getSendUser().getId(), message.getReceiveUserId(), message.getContent())){
            return ResponseEntity.ok("Message sent! Content: " + message.getContent());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to send message!");
        }
    }

    @PostMapping(path = "/loadMessage")
    public Map<String, Object> loadMessage(@RequestBody Message message){
        Map<String, Object> messageList = messageService.loadMessage(message.getSendUser().getId(), message.getReceiveUserId());
        return messageList;
    }

    @GetMapping (path = "/loadChatList")
    public Map<String, Object> loadChatList(@RequestParam("userId") int userId){
        Map<String, Object> chatListInfo = messageService.loadChatList(userId);
        return chatListInfo;
    }

    @PostMapping(path = "/findMessage")
    public List<Message> findMessageByContent(@RequestBody Message message){
        List<Message> messageFound = messageService.findMessageByContent(message.getContent(), message.getSendUser().getId(), message.getReceiveUserId());
        return messageFound;
    }

    @DeleteMapping
    public ResponseEntity<String> deleteChat(@RequestBody ChatList chatList){
        messageService.deleteChat(chatList);
        return ResponseEntity.ok("Chat deleted!");
    }
}
