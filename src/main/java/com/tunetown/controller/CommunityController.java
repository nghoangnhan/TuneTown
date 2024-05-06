package com.tunetown.controller;

import com.tunetown.model.ApproveRequest;
import com.tunetown.model.Community;
import com.tunetown.service.CommunityService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/community")
public class CommunityController {
    @Resource
    CommunityService communityService;

    @GetMapping("/getByHostId")
    public Community getCommunityById(@RequestParam("hostId") int hostId){
        Community community = communityService.getCommunityById(hostId);
        return community;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCommunity(@RequestBody Community community){
        communityService.createCommunity(community);
        return ResponseEntity.ok("Community created successfully");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCommunity(@RequestParam("hostId") int hostId){
        communityService.deleteCommunity(hostId);
        return ResponseEntity.ok("Community deleted");
    }

    @PostMapping("/approve")
    public ResponseEntity<String> approveRequest(@RequestBody ApproveRequest approveRequest){
        communityService.approveRequest(approveRequest);
        String approveResponse = "";
        if(approveRequest.getIsApprove() == 1){
            approveResponse = "Approve";
        }
        else{
            approveResponse = "Refuse";
        }
        return ResponseEntity.ok(approveResponse + " user " + approveRequest.getApproveUserId() + " joining community");
    }

    @PostMapping("/joinRequest")
    public boolean joinRequest(@RequestParam("userId") int userId, @RequestParam("communityId") int communityId){
        boolean isRequest = communityService.joinRequest(userId, communityId);
        return !isRequest;
    }

    @PostMapping("/outCommunity")
    public ResponseEntity<String> outCommunity(@RequestParam("userId") int userId, @RequestParam("communityId") int communityId){
        communityService.outCommunity(userId, communityId);
        return ResponseEntity.ok("Community left");
    }

    @GetMapping("/searchByName")
    public List<Community> searchCommunityByName(@RequestParam("communityName") String communityName){
        return communityService.searchCommunityByName(communityName);
    }
}
