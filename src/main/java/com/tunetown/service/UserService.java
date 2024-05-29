package com.tunetown.service;

import com.tunetown.model.*;
import com.tunetown.repository.*;
import com.tunetown.service.jwt.JwtService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class UserService {
    @Resource
    UserRepository userRepository;
    @Resource
    UserHistoryRepository userHistoryRepository;
    @Resource
    SongRepository songRepository;
    @Resource
    JwtService jwtService;
    @Resource
    PlaylistSongsRepository playlistSongsRepository;
    @Resource
    PlaylistRepository playlistRepository;
    @Resource
    FollowerService followerService;
    @Resource
    CommunityRepository communityRepository;

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
    public User getUserById(UUID userId) {
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

    public boolean addToHistory(UUID userId, int songId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with id " + userId);
        }

        Optional<Song> optionalSong = songRepository.findById(songId);
        if(optionalSong.isEmpty()){
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

            song.setListens(song.getListens() + 1);
            songRepository.save(song);
            return true;
        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    public List<UserHistory> getHistoryByUserId(UUID userId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with id " + userId);
        }

        return userHistoryRepository.getHistoryByUserId(userId);
    }

    /**
     * Get artist id, name, avatar and add the songId list of that artist to Object
     * @return Object with information
     */
    public Map<String, Object> getArtistDetail(UUID artistId, UUID userId) {
        Optional<User> optionalArtist = userRepository.findById(artistId);
        if(optionalArtist.isEmpty() || !optionalArtist.get().getRole().equals("ARTIST")){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No artist found with id " + artistId);
        }

        List<Object[]> artistDetailList = userRepository.getArtistDetail(artistId);
        Object[] artistDetails = artistDetailList.get(0);

        List<Song> topTracks = songRepository.getTopSongsOfArtist(artistId, Pageable.ofSize(10));

        int numberOfFollowers = followerService.getNumberOfFollowers(artistId);
        int numberOfFollowing = followerService.getNumberOfFollowing(artistId);

        Map<String, Object> artistInfo = new HashMap<>();
        artistInfo.put("id", artistDetails[0]);
        artistInfo.put("name", artistDetails[1]);
        artistInfo.put("avatar", artistDetails[2]);
        artistInfo.put("songs", topTracks);

        Optional<Follower> optionalFollower = followerService.getFollowInformation(userId, artistId);
        if(optionalFollower.isPresent()) {
            artistInfo.put("isFollowed", true);
            artistInfo.put("followedSince", optionalFollower.get().getFollowedDate());
        }
        else {
            artistInfo.put("isFollowed", false);
        }
        artistInfo.put("followers", numberOfFollowers);
        artistInfo.put("following", numberOfFollowing);

        return artistInfo;
    }

    public boolean deleteUser(UUID userId, String accessToken){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + userId + " does not exists!");
        }

        try{
            String token = accessToken.substring(6);
            String userEmail = jwtService.extractUserEmail(token);
            User currentUser = getActiveUserByEmail(userEmail);

            if(!currentUser.getRole().equals("ADMIN")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not the admin!");
            }

            // Delete all the song of user relevant
            List<Song> songList = songRepository.getAllSongsOfArtist(userId);
            if(!songList.isEmpty()){
                for (Song song : songList
                     ) {
                    song.setStatus(0);
                    songRepository.save(song);
                }
            }

            // Delete all the playlist of user relevant
            List<Playlist> playlistList = playlistRepository.getAllPlaylistsByUserId(userId);
            if(!playlistList.isEmpty()){
                for (Playlist playlist : playlistList
                     ) {
                    List<PlaylistSongs> playlistSongsList = playlistSongsRepository.getPlaylistSongsById(playlist.getId());
                    if(!playlistSongsList.isEmpty()){
                        playlistSongsRepository.deleteAll(playlistSongsList);
                    }
                    playlistRepository.delete(playlist);
                }
            }

            // Delete all user listen history
            List<UserHistory> userHistoryList = userHistoryRepository.getHistoryByUserId(userId);
            if(!userHistoryList.isEmpty()){
                userHistoryRepository.deleteAll(userHistoryList);
            }

            userRepository.delete(optionalUser.get());
            return true;
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return false;
    }

    public List<User> getListUserByName(String userName){
        return userRepository.getListUserByName(userName);
    }

    /**
     * Modify List of favorite Genres of User
     */
    public void modifyUserFavouriteGenres(User user, List<Genre> genres) {
        user.setGenres(genres);
        userRepository.save(user);
    }
    public List<Genre> getUserFavouriteGenres(UUID userId) {
        return userRepository.getUserFavouriteGenres(userId);
    }

    public int checkCommunityExist(UUID artistId){
        return communityRepository.checkCommunityExist(artistId);
    }
}
