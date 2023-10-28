package com.tunetown.controller;

import com.tunetown.model.User;
import com.tunetown.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @PutMapping
    public ResponseEntity<String> modifyUserInformation(@RequestBody User user) {
        boolean isModified = userService.modifyUserInformation(user);
        if(isModified)
            return ResponseEntity.ok("Modify successfully");
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Modify unsuccessfully");
    }

    @GetMapping(path = "/getUsers")
    public List<User> getUsersById(@RequestParam String email) {
        return userService.getListUserByEmail(email);
    }
}
