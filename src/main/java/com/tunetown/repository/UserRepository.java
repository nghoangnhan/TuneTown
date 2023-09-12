package com.tunetown.repository;

import com.tunetown.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    public Optional<User> getUserByEmail(String email);
}
