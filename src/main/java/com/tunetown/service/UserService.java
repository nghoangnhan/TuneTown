package com.tunetown.service;

import com.tunetown.model.User;
import com.tunetown.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Resource
    UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public void addUser(User user) {
        userRepository.save(user);
    }
}
