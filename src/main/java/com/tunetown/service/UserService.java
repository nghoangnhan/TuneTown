package com.tunetown.service;

import com.tunetown.model.*;
import com.tunetown.repository.*;
import com.tunetown.service.jwt.JwtService;
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
    public Map<String, Object> getArtistDetail(int artistId){
        Optional<User> optionalArtist = userRepository.findById(artistId);
        if(!optionalArtist.isPresent() || !optionalArtist.get().getRole().equals("ARTIST")){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No artist found with id " + artistId);
        }

        List<Object[]> artistDetailList = userRepository.getArtistDetail(artistId);
        List<Song> songList = songRepository.songListByArtist(artistId);
        Object[] artistDetails = artistDetailList.get(0);

        Map<String, Object> artistInfo = new HashMap<>();
        artistInfo.put("id", artistDetails[0]);
        artistInfo.put("name", artistDetails[1]);
        artistInfo.put("avatar", artistDetails[2]);
        artistInfo.put("songs", songList);

        return artistInfo;
    }

    public boolean deleteUser(int userId, String accessToken){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + userId + " does not exists!");
        }

        try{
            String token = accessToken.substring(6, accessToken.length());
            String userEmail = jwtService.extractUserEmail(token.toString());
            User currentUser = getActiveUserByEmail(userEmail);

            if(!currentUser.getRole().equals("ADMIN")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not the admin!");
            }

            // Delete all the song of user relevant
            List<Song> songList = songRepository.songListByArtist(userId);
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
                        for (PlaylistSongs playlistSong : playlistSongsList
                             ) {
                            playlistSongsRepository.delete(playlistSong);
                        }
                    }
                    playlistRepository.delete(playlist);
                }
            }

            // Delete all user listen history
            List<UserHistory> userHistoryList = userHistoryRepository.getHistoryByUserId(userId);
            if(!userHistoryList.isEmpty()){
                for (UserHistory userHistory : userHistoryList
                     ) {
                    userHistoryRepository.delete(userHistory);
                }
            }

            // Remove from list followedBy of artist
            List<User> userList = userRepository.findAll();
            for (User user : userList
                 ) {
                if(!user.getFollowedBy().isEmpty()){
                    user.getFollowedBy().removeIf(artistId -> artistId.equals(userId));
                    user.setFollowedBy(user.getFollowedBy());
                    userRepository.save(user);
                }
            }

            userRepository.delete(optionalUser.get());
            return true;
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return false;
    }
}
