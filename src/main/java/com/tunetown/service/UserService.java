package com.tunetown.service;

import com.tunetown.model.Song;
import com.tunetown.model.User;
import com.tunetown.model.UserHistory;
import com.tunetown.repository.SongRepository;
import com.tunetown.repository.UserHistoryRepository;
import com.tunetown.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Resource
    UserRepository userRepository;
    @Resource
    UserHistoryRepository userHistoryRepository;
    @Resource
    SongRepository songRepository;

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

    public boolean followArtist(int artistId, int userId){
        Optional<User> optionalArtist = userRepository.findById(artistId);
        if(!optionalArtist.isPresent() || !optionalArtist.get().getRole().equals("ARTIST")){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No artist found with id " + artistId);
        }

        User artist = optionalArtist.get();
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.get();

        try{
            // Add to list following of user
            List<Integer> listFollowingArtist = user.getFollowingArtists();
            listFollowingArtist.add(artistId);
            user.setFollowingArtists(listFollowingArtist);
            userRepository.save(user);

            // Add to list followedBy of artist
            List<Integer> listFollowedBy = artist.getFollowedBy();
            listFollowedBy.add(userId);
            artist.setFollowedBy(listFollowedBy);
            userRepository.save(artist);
            return true;
        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean unFollowArtist(int artistId, int userId){
        Optional<User> optionalArtist = userRepository.findById(artistId);
        if(!optionalArtist.isPresent() || !optionalArtist.get().getRole().equals("ARTIST")){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No artist found with id " + artistId);
        }

        User artist = optionalArtist.get();
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.get();

        try{
            // Remove from list following of user
            List<Integer> listFollowingArtist = user.getFollowingArtists();
            listFollowingArtist.remove(artistId);
            user.setFollowingArtists(listFollowingArtist);
            userRepository.save(user);

            // Remove from list followedBy of artist
            List<Integer> listFollowedBy = artist.getFollowedBy();
            listFollowedBy.remove(userId);
            artist.setFollowedBy(listFollowedBy);
            userRepository.save(artist);
            return true;
        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean addToHistory(int userId, int songId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with id " + userId);
        }

        Optional<Song> optionalSong = songRepository.findById(songId);
        if(!optionalSong.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No song found with id " + songId);
        }

        try{
            User user = optionalUser.get();
            Song song = optionalSong.get();

            UserHistory userHistory = new UserHistory();
            userHistory.setUser(user);
            userHistory.setSong(song);
            userHistory.setDateListen(LocalDateTime.now());
            userHistoryRepository.save(userHistory);
            return true;
        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    public List<UserHistory> getHistoryByUserId(int userId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with id " + userId);
        }

        List<UserHistory> userHistoryList = userHistoryRepository.getHistoryByUserId(userId);
        return userHistoryList;
    }

    /**
     * Get artist id, name, avatar and add the songId list of that artist to Object
     * @param artistId
     * @return Object with information
     */
    public Object[] getArtistDetail(int artistId){
        Optional<User> optionalArtist = userRepository.findById(artistId);
        if(!optionalArtist.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No artist found with id " + artistId);
        }

        List<Object[]> artistDetailList = userRepository.getArtistDetail(artistId);
        List<Integer> songIdList = songRepository.songListByArtist(artistId);
        Object[] artistDetails = artistDetailList.get(0);
        artistDetails = Arrays.copyOf(artistDetails, artistDetails.length + 1);

        artistDetails[artistDetails.length - 1] = songIdList;

        return artistDetails;
    }
}
