package com.tunetown.controller;

import com.tunetown.model.Follower;
import com.tunetown.model.Genre;
import com.tunetown.model.User;
import com.tunetown.model.UserHistory;
import com.tunetown.service.FollowerService;
import com.tunetown.service.UserService;
import com.tunetown.service.jwt.JwtService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Resource
    UserService userService;
    @Resource
    JwtService jwtService;
    @Resource
    FollowerService followerService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }

    @GetMapping
    public Map<String, Object> getUserDetails(@RequestParam(required = false, defaultValue = "default") String userId) {
        if(userId.equals("default"))
            return Map.of("users", userService.getAllUsers());
        else {
            User user = userService.getUserById(UUID.fromString(userId));
            List<Genre> favouriteGenres = userService.getUserFavouriteGenres(UUID.fromString(userId));
            user.setGenres(favouriteGenres);
            return Map.of("user", user);
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
    public ResponseEntity<String> addToHistory(@RequestParam("userId") UUID userId, @RequestParam("songId") int songId){
        if(userService.addToHistory(userId, songId)){
            return ResponseEntity.ok("Added to history!");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to add!");
        }
    }

    @PostMapping(path = "/getHistory")
    public List<UserHistory> getHistoryByUserId(@RequestParam("userId") UUID userId){
        return userService.getHistoryByUserId(userId);
    }

    @PostMapping(path = "/getArtistDetail")
    public Map<String, Object> getArtistDetail(@RequestParam("artistId") UUID artistId, @RequestHeader("Authorization") String accessToken){
        String token = accessToken.substring(6);
        String email = jwtService.extractUserEmail(token);
        User user = userService.getActiveUserByEmail(email);
        return userService.getArtistDetail(artistId, user);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(@RequestParam("userId") UUID userId, @RequestHeader("Authorization") String accessToken){
        if(accessToken.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Access token is missing!");
        }
        if(userService.deleteUser(userId, accessToken)){
            return ResponseEntity.ok("Delete user successfully!");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not the admin!");
    }

    @GetMapping(path = "/follow")
    public ResponseEntity<String> followUser(@RequestParam UUID userId, @RequestHeader("Authorization") String accessToken) {
        if(userId == null )
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required arguments");

        String token = accessToken.substring(6);
        String email = jwtService.extractUserEmail(token);
        User user = userService.getActiveUserByEmail(email);

        Optional<Follower> optionalFollower = followerService.getFollowInformation(user.getId(), userId);
        if(optionalFollower.isEmpty()) {
            followerService.follow(new Follower(user.getId(), userId));
            return ResponseEntity.ok("Followed");
        }
        else {
            followerService.unfollow(optionalFollower.get());
            return ResponseEntity.ok("Unfollowed");
        }
    }

    @GetMapping(path="/following")
    public Map<String, Object> getUserFollowing(@RequestParam(defaultValue = "1") int pageNo, @RequestHeader("Authorization") String accessToken) {
        String token = accessToken.substring(6);
        String email = jwtService.extractUserEmail(token);
        User user = userService.getActiveUserByEmail(email);

        Page<Follower> followerPage = followerService.getUserFollowing(user.getId(), pageNo - 1);
        return Map.of(
                "pageNo", followerPage.getNumber() + 1,
                "following", followerPage.getContent()
        );
    }

    @GetMapping(path="/followers")
    public Map<String, Object> getUserFollowers(@RequestParam(defaultValue = "1") int pageNo, @RequestHeader("Authorization") String accessToken) {
        String token = accessToken.substring(6);
        String email = jwtService.extractUserEmail(token);
        User user = userService.getActiveUserByEmail(email);

        Page<Follower> followerPage = followerService.getUserFollowers(user.getId(), pageNo - 1);
        return Map.of(
                "pageNo", followerPage.getNumber() + 1,
                "followers", followerPage.getContent()
        );
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

    @PostMapping(path = "/checkCommunityExist")
    public int checkCommunityExist(@RequestParam("artistId") UUID artistId){
        return userService.checkCommunityExist(artistId);
    }
}
