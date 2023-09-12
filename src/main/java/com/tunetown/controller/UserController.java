package com.tunetown.controller;

import com.tunetown.model.User;
import com.tunetown.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Resource
    UserService userService;

    @GetMapping(path = "/user/getAll")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    @PostMapping(path = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }
}
