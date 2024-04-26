package com.tunetown.service;

import com.tunetown.model.*;
import com.tunetown.repository.ChatListRepository;
import com.tunetown.repository.CommunityRepository;
import com.tunetown.repository.MessageRepository;
import com.tunetown.repository.UserRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CommunityService {
    @Resource
    CommunityRepository communityRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    ChatListRepository chatListRepository;
    @Resource
    MessageRepository messageRepository;

    public Community getCommunityById(int hostId){
        Optional<User> optionalHost = userRepository.findById(hostId);
        if (optionalHost.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Community with id = " + hostId + " does not exists!");
        }
        Community community = communityRepository.getCommunityById(hostId);
        return community;
    }

    public void createCommunity(Community community){
        int hostId = community.getHosts().get(0).getId();
        Optional<User> optionalHost = userRepository.findById(hostId);
        if (optionalHost.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Host with id = " + hostId + " does not exists!");
        }
        communityRepository.save(community);
        if(community.getCommunityMessages() == null){
            Message message = new Message();
            message.setContent("Community Created!");
            List<Message> messageList = new ArrayList<>();
            messageList.add(message);
            message.setSendUser(optionalHost.get());
            message.setReceiveUserId(community.getId());
            message.setMessageDate(LocalDateTime.now());
            message.setSeen(1);
            message.setType(2);
            messageRepository.save(message);
            community.setCommunityMessages(messageList);

            ChatList chatList = chatListRepository.getChatListByUserId(community.getCommunityId());
            if(chatList == null){
                chatList = new ChatList();
                chatList.setUserId(hostId);
            }
            if(chatList.getSentCommunity() == null){
                chatList.setSentCommunity(new ArrayList<>());
            }
            chatList.getSentCommunity().add(community.getCommunityId());
            chatListRepository.save(chatList);
        }
    }

    public void deleteCommunity(int hostId){
        Optional<User> optionalHost = userRepository.findById(hostId);
        if (optionalHost.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Host with id = " + hostId + " does not exists!");
        }
        Community community = communityRepository.getCommunityById(hostId);
        for(User user: community.getJoinUsers()){
            ChatList chatList = chatListRepository.getChatListByUserId(user.getId());
            chatList.getSentCommunity().remove(Integer.valueOf(community.getCommunityId()));
        }
        communityRepository.delete(community);
    }

    public User approveRequest(ApproveRequest approveRequest){
        Community community = communityRepository.getCommunityById(approveRequest.getHostId());
        Optional<User> optionalApproveUser = userRepository.findById(approveRequest.getApproveUserId());
        User approveUser = optionalApproveUser.get();
        community.getApproveRequests().remove(approveUser);
        if(approveRequest.getIsApprove() == 1){
            community.getJoinUsers().add(approveUser);
            Message message = new Message();
            message.setContent("Welcome " + approveRequest.getApproveUserId() + " to community!");
            message.setSendUser(approveUser);
            message.setReceiveUserId(community.getId());
            message.setMessageDate(LocalDateTime.now());
            message.setSeen(1);
            message.setType(2);
            messageRepository.save(message);
            community.getCommunityMessages().add(message);

            ChatList chatList = chatListRepository.getChatListByUserId(approveUser.getId());
            if(chatList == null){
                chatList = new ChatList();
                chatList.setUserId(approveUser.getId());
            }
            if(chatList.getSentCommunity() == null){
                chatList.setSentCommunity(new ArrayList<>());
            }
            chatList.getSentCommunity().add(community.getCommunityId());
            chatListRepository.save(chatList);
        }
        communityRepository.save(community);
        return approveUser;
    }

    public boolean joinRequest(int userId, int communityId){
        Optional<Community> optionalCommunity = communityRepository.findById(communityId);
        if (optionalCommunity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Community with id = " + communityId + " does not exists!");
        }
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + userId + " does not exists!");
        }

        Community community = optionalCommunity.get();
        if(community.getApproveRequests() == null){
            community.setApproveRequests(new ArrayList<>());
        }
        boolean isRequest = false;
        for(User userApprove: community.getApproveRequests()){
            if(userApprove.getId() == userId){
                isRequest = true;
            }
        }

        if(!isRequest){
            community.getApproveRequests().add(optionalUser.get());
        }
        else{
            community.getApproveRequests().remove(optionalUser.get());
        }
        communityRepository.save(community);
        return isRequest;
    }

    public void outCommunity(int userId, int communityId){
        Optional<Community> optionalCommunity = communityRepository.findById(communityId);
        if (optionalCommunity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Community with id = " + communityId + " does not exists!");
        }
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + userId + " does not exists!");
        }

        Community community = optionalCommunity.get();
        User user = optionalUser.get();

        community.getJoinUsers().remove(user);
        ChatList chatList = chatListRepository.getChatListByUserId(userId);
        chatList.getSentCommunity().remove(Integer.valueOf(community.getCommunityId()));
        chatListRepository.save(chatList);
        communityRepository.save(community);
    }
}
