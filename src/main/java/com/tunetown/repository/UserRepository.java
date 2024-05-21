package com.tunetown.repository;

import com.tunetown.model.Genre;
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

    @Query("SELECT a.id, a.userName, a.avatar FROM User a WHERE a.id = ?1")
    List<Object[]> getArtistDetail(int artistId);

    @Query("SELECT u FROM User u WHERE u.userName LIKE %:userName%")
    List<User> getListUserByName(String userName);

    @Query("SELECT u.genres FROM User u WHERE u.id = ?1")
    List<Genre> getUserFavouriteGenres(int userId);
}
