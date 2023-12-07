package com.tunetown.controller;

import com.tunetown.model.Song;
import com.tunetown.model.User;
import com.tunetown.model.UserHistory;
import com.tunetown.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Resource
    UserService userService;
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }

    @GetMapping
    public Map<String, Object> getUserDetails(@RequestParam(required = false, defaultValue = "0") int userId) {
        if(userId == 0)
            return Map.of("users", userService.getAllUsers());
        return Map.of("user", userService.getUserById(userId));
    }
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> modifyUserInformation(@RequestBody User user) {
        boolean isModified = userService.modifyUserInformation(user);
        if(isModified)
            return ResponseEntity.ok("Modify successfully");
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Modify unsuccessfully");
    }

    @PutMapping("/switchUserRole")
    public ResponseEntity<String> switchUserRole(@RequestBody User user) {
        try {
            userService.switchUserRole(user);
            return ResponseEntity.ok("Switched user role into: " + user.getRole());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(path = "/getUsers")
    public List<User> getUsersByEmail(@RequestParam String email) {
        return userService.getListUserByEmail(email);
    }

    @PostMapping(path = "/followArtist")
    public ResponseEntity<String> followArtist(@RequestParam("artistId") int artistId, @RequestParam("userId") int userId) {
        if (userService.followArtist(artistId, userId)) {
            return ResponseEntity.ok("Followed " + userService.getUserById(artistId).getUserName());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Follow unsuccessfully!");
        }
    }

    @PostMapping(path = "/unFollowArtist")
    public ResponseEntity<String> unFollowArtist(@RequestParam("artistId") int artistId, @RequestParam("userId") int userId) {
        if (userService.unFollowArtist(artistId, userId)) {
            return ResponseEntity.ok("Unfollow " + userService.getUserById(artistId).getUserName());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unfollow unsuccessfully!");
        }
    }

    @PostMapping(path = "/addToHistory")
    public ResponseEntity<String> addToHistory(@RequestParam("userId") int userId, @RequestParam("songId") int songId){
        if(userService.addToHistory(userId, songId)){
            return ResponseEntity.ok("Added to history!");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to add!");
        }
    }

    @PostMapping(path = "/getHistory")
    public List<UserHistory> getHistoryByUserId(@RequestParam("userId") int userId){
        List<UserHistory> userHistoryList = userService.getHistoryByUserId(userId);
        return userHistoryList;
    }

    @PostMapping(path = "/getArtistDetail")
    public Map<String, Object> getArtistDetail(@RequestParam("artistId") int artistId){
        Map<String, Object> artistDetail = userService.getArtistDetail(artistId);
        return artistDetail;
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(@RequestParam("userId") int userId, @RequestHeader("Authorization") String accessToken){
        if(accessToken.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Access token is missing!");
        }
        if(userService.deleteUser(userId, accessToken)){
            return ResponseEntity.ok("Delete user successfully!");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not the admin!");
    }
}
