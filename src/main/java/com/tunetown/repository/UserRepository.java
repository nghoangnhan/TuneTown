package com.tunetown.repository;

import com.tunetown.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> getUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email LIKE CONCAT('%', ?1, '%')")
    List<User> getListUserByEmail(String email);
}
