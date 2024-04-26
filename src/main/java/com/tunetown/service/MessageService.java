package com.tunetown.service;

import com.tunetown.model.*;
import com.tunetown.repository.*;
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
//    @Resource
//    NotificationRepository notificationRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    CommunityService communityService;
    @Resource
    CommunityRepository communityRepository;

    public boolean sendMessage(int sendUserId, int receiveUserId, String content){
        Optional<Community> optionalCommunity = Optional.ofNullable(new Community());
        Optional<User> optionalUser = userRepository.findById(sendUserId);
        if (!optionalUser.isPresent()){
            optionalCommunity = communityRepository.findById(sendUserId);
            if(!optionalCommunity.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + sendUserId + " does not exists!");
            }
        }

        // Check receiveUserId exists
        Optional<User> receiveUserIdCheck = userRepository.findById(receiveUserId);
        if (!receiveUserIdCheck.isPresent()){
            optionalCommunity = communityRepository.findById(receiveUserId);
            if(!optionalCommunity.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + receiveUserId + " does not exists!");
            }
        }

        try{
            if(optionalUser.isPresent() && receiveUserIdCheck.isPresent()){
                // Update chat list user
                ChatList chatListSent = chatListRepository.getChatListByUserId(sendUserId);
                ChatList chatListReceived = chatListRepository.getChatListByUserId(receiveUserId);

                // Initialize if the chatlist is null
                if(chatListSent == null){
                    chatListSent = new ChatList();
                    chatListSent.setUserId(sendUserId);
                }

                List<Integer> sentUserList = chatListSent.getSentUser();
                if (sentUserList == null) {
                    sentUserList = new ArrayList<>(); // Initialize the list if it's null
                }
                if (!sentUserList.contains(receiveUserId)) {
                    sentUserList.add(0, receiveUserId);
                }
                chatListSent.setSentUser(sentUserList);


                if(chatListReceived == null){
                    chatListReceived = new ChatList();
                    chatListReceived.setUserId(receiveUserId);
                }
                List<Integer> receiveUserList = chatListReceived.getSentUser();


                // Re-order chat list of received user
                if (receiveUserList == null) {
                    receiveUserList = new ArrayList<>(); // Initialize the list if it's null
                }

                if (!receiveUserList.contains(sendUserId)){
                    receiveUserList.add(0, sendUserId);
                }
                chatListReceived.setSentUser(receiveUserList);

                chatListRepository.saveAll(Arrays.asList(chatListSent, chatListReceived));

                Message message = new Message();
                message.setSendUser(optionalUser.get());
                message.setReceiveUserId(receiveUserId);
                message.setContent(content);
                message.setMessageDate(LocalDateTime.now());
                message.setSeen(0);
                message.setType(0);
                messageRepository.save(message);
            }
            else{
                Message message = new Message();
                message.setSendUser(optionalUser.get());
                message.setReceiveUserId(optionalCommunity.get().getId());
                message.setContent(content);
                message.setMessageDate(LocalDateTime.now());
                message.setSeen(1);
                message.setType(1);
                messageRepository.save(message);
                optionalCommunity.get().getCommunityMessages().add(message);
                communityRepository.save(optionalCommunity.get());
            }

            return true;
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return false;
    }

    public Map<String, Object> loadMessage(int userId, int sentId){
        Optional<Community> optionalCommunity = Optional.ofNullable(new Community());
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()){
            optionalCommunity = communityRepository.findById(userId);
            if(!optionalCommunity.isPresent())
            {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + userId + " does not exists!");
            }
        }

        // Check receiveUserId exists
        Optional<User> optionalSentUser = userRepository.findById(sentId);
        if (!optionalSentUser.isPresent()){
            optionalCommunity = communityRepository.findById(sentId);
            if(!optionalCommunity.isPresent())
            {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + sentId + " does not exists!");
            }
        }
        Map<String, Object> messageUserInfo = new HashMap<>();

        if(optionalUser.isPresent() && optionalSentUser.isPresent()){
            User sentUser = optionalSentUser.get();

            List<Message> messageList = messageRepository.getMessageByUserId(userId, sentId);
            for(Message message: messageList){
                Map<String, Object> messageInfo = new HashMap<>();
                messageInfo.put("sentUser", sentUser);
                messageInfo.put("message", message);
                messageUserInfo.put(String.valueOf(message.getId()), messageInfo);
                messageRepository.save(message);
            }
            List<Message> messageListBySentUser = messageRepository.messageListByAuthor(sentId);
            for(Message message: messageListBySentUser){
                message.setSeen(1);
                messageRepository.save(message);
            }
        }
        else{
            Community community = optionalCommunity.get();
            User user = optionalUser.get();

            List<Message> messageList = messageRepository.getMessageByUserId(userId, community.getId());
            for(Message message: messageList){
                Map<String, Object> messageInfo = new HashMap<>();
                messageInfo.put("user", user);
                messageInfo.put("community", community);
                messageInfo.put("message", message);
                messageUserInfo.put(String.valueOf(community.getId()), messageInfo);
                messageRepository.save(message);
            }
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
        if(chatList.getSentCommunity() != null){
            for(int hostId: chatList.getSentCommunity()){
                Community community = communityService.getCommunityById(hostId);
                if(community != null) {
                    messageList = messageRepository.getMessageByUserId(community.getId(), userId);
                    if (!messageList.isEmpty()) {
                        Message lastMessage = community.getCommunityMessages().get(community.getCommunityMessages().size() - 1);
                        Map<String, Object> communityChatInfo = new HashMap<>();
                        communityChatInfo.put("community", community);
                        communityChatInfo.put("lastMessage", lastMessage);
                        chatListInfo.put("Community " + community.getId(), communityChatInfo);
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
