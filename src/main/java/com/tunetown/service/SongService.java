package com.tunetown.service;

import com.tunetown.config.FirebaseConfig;
import com.tunetown.model.Song;
import com.tunetown.model.User;
import com.tunetown.model.UserHistory;
import com.tunetown.repository.SongRepository;
import com.tunetown.repository.UserHistoryRepository;
import com.tunetown.service.jwt.JwtService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SongService {

    @Resource
    SongRepository songRepository;
    @Resource
    UserService userService;
    @Resource
    JwtService jwtService;
    @Resource
    UserHistoryRepository userHistoryRepository;

    /**
     * Get all songs that status = 1 (Enabled) by Paging Technique
     * @return
     */
    public Page<Song> getAllSongs(Pageable pageable){
        return songRepository.getByStatus(1, pageable);
    }


    /**
     * Add a new song
     * @param song: Get from JSON_VALUE
     */
    public void addSong(Song song){
        songRepository.save(song);
    }


    /**
     * Use Soft Delete to set deletedSong status to 0 (Disabled) instead of delete song
     * @param id
     */
    public boolean deleteSong(int id, String accessToken){
        Optional<Song> optionalSong = songRepository.findById(id);
        if (optionalSong.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song with id = " + optionalSong.get().getId() + " does not exists!");
        }

        Song deletedSong = getActiveSongById(id);
        String token = accessToken.substring(6, accessToken.length());
        String userEmail = jwtService.extractUserEmail(token.toString());
        User currentUser = userService.getActiveUserByEmail(userEmail);

        boolean isArtist = false;

        for (User user: deletedSong.getArtists()
        ) {
            if(userEmail.equals(user.getEmail()) || currentUser.getRole().toUpperCase().equals("ADMIN")){
                isArtist = true;
                break;
            }
        }
        if (isArtist){
            deletedSong.setStatus(0);
            songRepository.save(deletedSong);
            return true;
        }
        return false;
    }

    /**
     * - Update song consists of checkValidData steps
     * - Check the equality between old and new values before update
     * @param song: Get from JSON_VALUE
     */
    @Transactional
    public boolean updateSong(Song song, String accessToken){
        Optional<Song> optionalSong = songRepository.findById(song.getId());
        if (optionalSong.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song with id = " + song.getId() + " does not exists!");
        }

        Song songUpdate = optionalSong.get();
        String token = accessToken.substring(6, accessToken.length());
        String userEmail = jwtService.extractUserEmail(token.toString());
        User currentUser = userService.getActiveUserByEmail(userEmail);

        boolean isArtist = false;

        for (User user: songUpdate.getArtists()) {
            if(userEmail.equals(user.getEmail()) || currentUser.getRole().toUpperCase().equals("ADMIN")){
                isArtist = true;
                break;
            }
        }
        if (isArtist){
            String name = song.getSongName();
            String poster = song.getPoster();
            String data = song.getSongData();
            Integer listens = song.getListens();
            Integer likes = song.getLikes();
            Integer status = song.getStatus();


            // Check valid before update
            if(name != null && name.length() > 0 && !Objects.equals(songUpdate.getSongName(), name)) // Check the new name != current name
            {
                songUpdate.setSongName(name);
            }

            if(poster != null && poster.length() > 0 && !Objects.equals(songUpdate.getPoster(), poster)) // Check the new poster != current poster
            {
                songUpdate.setPoster(poster);
            }

            if(data != null && data.length() > 0 && !Objects.equals(songUpdate.getSongData(), data)) // Check the new data != current data
            {
                songUpdate.setSongData(data);
            }

            if(listens != null && !Objects.equals(songUpdate.getListens(), listens)) // Check the new listens != current listens
            {
                songUpdate.setListens(listens);
            }

            if(likes != null && !Objects.equals(songUpdate.getLikes(), likes)) // Check the new likes != current likes
            {
                songUpdate.setLikes(likes);
            }

            if(status != null && !Objects.equals(songUpdate.getStatus(), status)) // Check the new likes != current likes
            {
                songUpdate.setStatus(status);
            }
            songRepository.save(songUpdate);
            return true;
        }
        return false;
    }


    /**
     * Get song that active (status = 1)
     * @param id
     * @return
     */
    public Song getActiveSongById(int id){
        Optional<Song> optionalSong = songRepository.getSongById(id);
        if(optionalSong.isPresent()){
            return optionalSong.get();
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song with id = " + id + " does not exists!");
        }
    }


    /**
     * Find by songName or artistName, just find active songs
     * @param name Use songName or artistName. It can be split into two parts if user input both songName and artistName
     * @return: list of songs found
     */
    public List<Song> findSongByNameOrArtist(String name){
        String[] parts = name.split(" "); // Split the name parameter into parts by space
        String songName = parts[0]; // First part is treated as the song name
        String artistName = parts.length > 1 ? parts[1] : ""; // Second part is treated as the artistName
        List<Song> listSong = songRepository.findSongByNameOrArtist(songName, artistName);
        if (!listSong.isEmpty()){
            return listSong;
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No result found!");
        }
    }

    /**
     * Get the list of songs between a time period listened
     * @param startTime
     * @param endTime
     * @return
     */
    public List<Song> getTopSongByPeriod(LocalDateTime startTime, LocalDateTime endTime){
        List<Song> topSongList = new ArrayList<>();

        List<UserHistory> userHistoryList = userHistoryRepository.getTopSongByPeriod(startTime, endTime);

        for (UserHistory userHistory: userHistoryList
        ) {
            topSongList.add(userHistory.getSong());
        }

        return topSongList;
    }
}
