package com.tunetown.service;

import com.google.api.services.storage.Storage;
import com.google.cloud.storage.BlobInfo;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.StorageClient;
import com.tunetown.config.FirebaseConfig;
import com.tunetown.model.Genre;
import com.tunetown.model.Song;
import com.tunetown.repository.SongRepository;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Part;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SongService {

    @Resource
    SongRepository songRepository;

    @Resource
    FirebaseConfig firebaseConfig;


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
    public void deleteSong(int id){
        Song deletedSong = getActiveSongById(id);
        deletedSong.setStatus(0);
        songRepository.save(deletedSong);
    }

    /**
     * - Update song consists of checkValidData steps
     * - Check the equality between old and new values before update
     * @param song: Get from JSON_VALUE
     */
    @Transactional
    public void updateSong(Song song){
        Optional<Song> optionalSong = songRepository.findById(song.getId());
        Song songUpdate = optionalSong.get();

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
}