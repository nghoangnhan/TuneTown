package com.tunetown.service;

import com.tunetown.model.ChatList;
import com.tunetown.model.Message;
import com.tunetown.model.Notification;
import com.tunetown.model.User;
import com.tunetown.repository.ChatListRepository;
import com.tunetown.repository.MessageRepository;
import com.tunetown.repository.NotificationRepository;
import com.tunetown.repository.UserRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class MessageService {
    @Resource
    MessageRepository messageRepository;
    @Resource
    ChatListRepository chatListRepository;
    @Resource
    NotificationRepository notificationRepository;
    @Resource
    UserRepository userRepository;

    public boolean sendMessage(int sendUserId, int receiveUserId, String content){
        Optional<User> optionalUser = userRepository.findById(sendUserId);
        if (!optionalUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + sendUserId + " does not exists!");
        }

        // Check receiveUserId exists
        Optional<User> receiveUserIdCheck = userRepository.findById(receiveUserId);
        if (!receiveUserIdCheck.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + receiveUserId + " does not exists!");
        }

        try{
            // Update chat list user
            ChatList chatListSent = chatListRepository.getChatListByUserId(sendUserId);
            ChatList chatListReceived = chatListRepository.getChatListByUserId(receiveUserId);

            // Initialize if the chatlist is null
            if(chatListSent == null){
                log.info("Chat list sent null");
                chatListSent = new ChatList();
                chatListSent.setUserId(sendUserId);
            }

            List<Integer> sentUserList = chatListSent.getSentUser();
            if (sentUserList == null) {
                log.info("Sent User List null");
                sentUserList = new ArrayList<>(); // Initialize the list if it's null
            }
            log.info("Receive User: " + receiveUserId);
            if (!sentUserList.contains(receiveUserId)) {
                log.info("Not contain 1");
                sentUserList.add(0, receiveUserId);
            }
            chatListSent.setSentUser(sentUserList);


            if(chatListReceived == null){
                log.info("Chat list receive null");
                chatListReceived = new ChatList();
                chatListReceived.setUserId(receiveUserId);
            }
            List<Integer> receiveUserList = chatListReceived.getSentUser();


            // Re-order chat list of received user
            if (receiveUserList == null) {
                log.info("Receive null");
                receiveUserList = new ArrayList<>(); // Initialize the list if it's null
            }
            log.info("Sent User: " + sendUserId);

            if (!receiveUserList.contains(sendUserId)){
                log.info("Not contain 2");
                receiveUserList.add(0, sendUserId);
            }
            chatListReceived.setSentUser(receiveUserList);

            chatListRepository.saveAll(Arrays.asList(chatListSent, chatListReceived));

            Message message = new Message();
            message.setSendUserId(sendUserId);
            message.setReceiveUserId(receiveUserId);
            message.setContent(content);
            message.setMessageDate(LocalDateTime.now());
            message.setSeen(0);
            messageRepository.save(message);

            // Send notification
//            Notification messageNotification = new Notification();
//            messageNotification.setReceiveUserId(receiveUserId);
//            messageNotification.setType(0);
//            messageNotification.setStatus(0);
//            notificationRepository.save(messageNotification);

            return true;
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return false;
    }

    public Map<String, Object> loadMessage(int userId, int sentId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + userId + " does not exists!");
        }

        // Check receiveUserId exists
        Optional<User> optionalSentUser = userRepository.findById(sentId);
        if (!optionalSentUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + sentId + " does not exists!");
        }

        User user = optionalUser.get();
        User sentUser = optionalSentUser.get();
        Map<String, Object> messageUserInfo = new HashMap<>();

        List<Message> messageList = messageRepository.getMessageByUserId(userId, sentId);
        for(Message message: messageList){
            Map<String, Object> messageInfo = new HashMap<>();
            messageInfo.put("user", user);
            messageInfo.put("sentUser", sentUser);
            messageInfo.put("message", message);
            messageUserInfo.put(String.valueOf(message.getId()), messageInfo);
            message.setSeen(1);
            messageRepository.save(message);
        }
        return messageUserInfo;
    }

    public Map<String, Object> loadChatList(int userId){
        ChatList chatList = chatListRepository.getChatListByUserId(userId);
        List<Message> messageList = new ArrayList<>();
        Map<String, Object> chatListInfo = new LinkedHashMap<>(); // LinkedHashMap() to use ordered list as input

        // Check chatList null
        if (chatList == null){
            chatList = new ChatList();
        }

        // Check sentUser list null
        if(chatList.getSentUser() != null){
            for(int id: chatList.getSentUser()){
                User user = userRepository.findById(id).get();
                if(user != null) {
                    messageList = messageRepository.getMessageByUserId(userId, id);
                    if (!messageList.isEmpty()) {
                        Message lastMessage = messageList.get(messageList.size() - 1);
                        Map<String, Object> userChatInfo = new HashMap<>();
                        userChatInfo.put("user", user);
                        userChatInfo.put("lastMessage", lastMessage);
                        chatListInfo.put(String.valueOf(id), userChatInfo);
                    }
                }
            }
        }
        Comparator<Map.Entry<String, Object>> lastMessageDateComparator = (entry1, entry2) -> {
            Map<String, Object> userChatInfo1 = (Map<String, Object>) entry1.getValue();
            Map<String, Object> userChatInfo2 = (Map<String, Object>) entry2.getValue();

            Message lastMessage1 = (Message) userChatInfo1.get("lastMessage");
            Message lastMessage2 = (Message) userChatInfo2.get("lastMessage");

            return lastMessage2.getMessageDate().compareTo(lastMessage1.getMessageDate());
        };

        // Convert the chatListInfo map to a list for sorting
        List<Map.Entry<String, Object>> sortedChatList = new ArrayList<>(chatListInfo.entrySet());

        // Sort the list using the comparator
        Collections.sort(sortedChatList, lastMessageDateComparator);

        // Create a new LinkedHashMap to hold the sorted entries
        LinkedHashMap<String, Object> sortedChatListInfo = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : sortedChatList) {
            sortedChatListInfo.put(entry.getKey(), entry.getValue());
        }
        return sortedChatListInfo;
    }

    public List<Message> findMessageByContent(String content, int userId, int sentUserId){
        List<Message> messageFound = messageRepository.findMessageByContent(content,userId,sentUserId);
        Collections.reverse(messageFound); // Reverse the order of the list
        return messageFound;
    }
}
