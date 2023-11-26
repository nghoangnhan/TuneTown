package com.tunetown.service;

import com.tunetown.model.User;
import com.tunetown.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Resource
    UserRepository userRepository;
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public void addUser(User user) {

        Optional<User> dbUser = userRepository.getUserByEmail(user.getEmail());
        if(dbUser.isPresent())
        {
            log.info("Email has already existed");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, user.getEmail() + " has already existed");
        }
        else
        {
            String encodedPassword = passwordEncoder().encode(user.getPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
        }
    }
    public User getUserById(int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent())
            return optionalUser.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No User found with ID: " + userId);
    }

    public User getActiveUserByEmail(String userEmail) {
        Optional<User> optionalUser = userRepository.getUserByEmail(userEmail);
        if(optionalUser.isPresent())
            return optionalUser.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with email " + userEmail);
    }

    public List<User> getListUserByEmail(String email) {
        return userRepository.getListUserByEmail(email);
    }
    @Transactional
    public boolean modifyUserInformation(User modifiedUser) {
        User dbUser = getUserById(modifiedUser.getId());
        try {
            dbUser.setUserName(modifiedUser.getUserName());
            dbUser.setBirthDate(modifiedUser.getBirthDate());
            dbUser.setAvatar(modifiedUser.getAvatar());
            dbUser.setUserBio(modifiedUser.getUserBio());
            return true;
        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    @Transactional
    public void switchUserRole(User user) {
        User dbUser = getUserById(user.getId());
        dbUser.setRole(user.getRole());
    }
}
