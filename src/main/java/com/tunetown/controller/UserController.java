package com.tunetown.controller;

import com.tunetown.model.Follower;
import com.tunetown.model.Genre;
import com.tunetown.model.User;
import com.tunetown.model.UserHistory;
import com.tunetown.service.FollowerService;
import com.tunetown.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Resource
    UserService userService;
    @Resource
    FollowerService followerService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }

    @GetMapping
    public Map<String, Object> getUserDetails(@RequestParam(required = false, defaultValue = "0") int userId) {
        if(userId == 0)
            return Map.of("users", userService.getAllUsers());
        else {
            User user = userService.getUserById(userId);
            List<Genre> favouriteGenres = userService.getUserFavouriteGenres(userId);
            user.setGenres(favouriteGenres);
            return Map.of("user", userService.getUserById(userId));
        }
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
        return userService.getHistoryByUserId(userId);
    }

    @PostMapping(path = "/getArtistDetail")
    public Map<String, Object> getArtistDetail(@RequestParam("artistId") int artistId){
        return userService.getArtistDetail(artistId);
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

    @PostMapping(path = "/follow")
    public ResponseEntity<String> followUser(@RequestBody Follower follower) {
        if(follower.getFollower() == null || follower.getSubject() == null )
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required arguments");

        try {
            followerService.follow(follower);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error on following a user");
        }
        return ResponseEntity.ok("Followed");
    }

    @DeleteMapping(path = "/unfollow")
    public ResponseEntity<String> unfollowUser(@RequestBody Follower follower) {
        if(follower.getFollower() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required arguments");
        followerService.unfollow(follower);
        return ResponseEntity.ok("Unfollowed");
    }

    @GetMapping(path = "/getListByName")
    public List<User> getListUserByName(@RequestParam String userName){
        return userService.getListUserByName(userName);
    }

    @PutMapping(path = "/modify-favorite-genres")
    public ResponseEntity<String> modifyFavoriteGenres(@RequestBody List<Genre> genres, Authentication authentication) {
        User user = userService.getActiveUserByEmail(authentication.getName());
        userService.modifyUserFavouriteGenres(user, genres);
        return ResponseEntity.ok("Modified");
    }
}
