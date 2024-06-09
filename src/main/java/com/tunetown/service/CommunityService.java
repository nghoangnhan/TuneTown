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
import java.util.UUID;

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

    public Community getCommunityById(UUID hostId){
        Optional<User> optionalHost = userRepository.findById(hostId);
        if (optionalHost.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Community with id = " + hostId + " does not exists!");
        }
        Community community = communityRepository.getCommunityById(hostId);
        return community;
    }

    public void createCommunity(Community community){
        User host = community.getHost();
        Optional<User> optionalHost = userRepository.findById(host.getId());
        if (optionalHost.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Host with id = " + host.getId() + " does not exists!");
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



            ChatList chatList = chatListRepository.getChatListByUser(optionalHost.get());
            if(chatList == null){
                chatList = new ChatList();
                chatList.setUser(host);
            }
            if(chatList.getSentCommunity() == null){
                chatList.setSentCommunity(new ArrayList<>());
            }
            chatList.getSentCommunity().add(community);
            chatListRepository.save(chatList);
        }
    }

    public void deleteCommunity(UUID hostId){
        Optional<User> optionalHost = userRepository.findById(hostId);
        if (optionalHost.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Host with id = " + hostId + " does not exists!");
        }
        Community community = communityRepository.getCommunityById(hostId);
        for(User user: community.getJoinUsers()){
            ChatList chatList = chatListRepository.getChatListByUser(user);
            chatList.getSentCommunity().remove(community.getCommunityId());
        }
        communityRepository.delete(community);
    }

    public boolean approveRequest(ApproveRequest approveRequest){
        Community community = communityRepository.getCommunityById(approveRequest.getHost().getId());
        Optional<User> optionalApproveUser = userRepository.findById(approveRequest.getApproveUser().getId());
        User approveUser = optionalApproveUser.get();
        community.getApproveRequests().remove(approveUser);
        if(approveRequest.getIsApprove() == 1){
            community.getJoinUsers().add(approveUser);
            Message message = new Message();
            message.setContent("Welcome " + approveUser.getUserName() + " to community!");
            message.setSendUser(approveUser);
            message.setReceiveUserId(community.getId());
            message.setMessageDate(LocalDateTime.now());
            message.setSeen(1);
            message.setType(2);
            messageRepository.save(message);
            community.getCommunityMessages().add(message);

            ChatList chatList = chatListRepository.getChatListByUser(approveUser);
            if(chatList == null){
                chatList = new ChatList();
                chatList.setUser(approveUser);
            }
            if(chatList.getSentCommunity() == null){
                chatList.setSentCommunity(new ArrayList<>());
            }
            chatList.getSentCommunity().add(community);
            chatListRepository.save(chatList);
            return true;
        }
        communityRepository.save(community);
        return false;
    }

    public boolean joinRequest(UUID userId, UUID communityId){
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

    public void outCommunity(UUID userId, UUID communityId){
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
        ChatList chatList = chatListRepository.getChatListByUser(user);
        log.info("TESTTT: " + community.getId());
        chatList.getSentCommunity().remove(community);
        chatListRepository.save(chatList);
        communityRepository.save(community);
    }

    public List<Community> searchCommunityByName(String communityName){
        List<Community> listCommunity = communityRepository.searchCommunityByName(communityName);
        return listCommunity;
    }
}
