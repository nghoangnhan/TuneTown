package com.tunetown.controller;

import com.tunetown.model.User;
import com.tunetown.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Resource
    UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }
    @GetMapping(path = "/getUsers")
    public List<User> getUsersById(@RequestParam String email) {
        return userService.getListUserByEmail(email);
    }
}
