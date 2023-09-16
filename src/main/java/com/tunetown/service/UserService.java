package com.tunetown.service;

import com.tunetown.model.User;
import com.tunetown.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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

    public User getActiveUserByEmail(String userEmail) {
        Optional<User> optionalUser = userRepository.getUserByEmail(userEmail);
        if(optionalUser.isPresent())
            return optionalUser.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with email " + userEmail);
    }
}
