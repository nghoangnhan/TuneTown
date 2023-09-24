package com.tunetown.controller;

import com.tunetown.model.Song;
import com.tunetown.repository.SongRepository;
import com.tunetown.service.FirebaseStorageService;
import com.tunetown.service.SongService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class SongController {
    @Resource
    SongService songService;

    @Resource
    SongRepository songRepository;

    @Resource
    FirebaseStorageService firebaseStorageService;

    // TODO: Get filePath from FileChooser on Front-End
    String filePath = "";


    /**
     * Get songs by numbers in each page using Paging Technique
     * @param page: Page number 1 for default
     * @param size: size of items each page
     * @return
     */
    @GetMapping(path = "/song")
    public Page<Song> getAllSongs(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        return songService.getAllSongs(pageRequest);
    }

    @PostMapping(path = "/addSong", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addSong(@RequestBody Song song ){
        // TODO: Call these 2 functions to save data on firebase
//        uploadImage();
//        uploadMp3();
        songService.addSong(song);
        return ResponseEntity.ok("Song added successfully");
    }


    @DeleteMapping(path = "/deleteSong/songId={songId}")
    public ResponseEntity<String> deleteSong(@PathVariable("songId") int id){
        songService.deleteSong(id);
        return ResponseEntity.ok("Song deleted successfully");
    }

    @PutMapping(path = "/updateSong/songId={songId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSong(@PathVariable("songId") int id, @RequestBody Song song){
        Optional<Song> optionalSong = songRepository.getSongById(id);
        if (!optionalSong.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song with id = " + id + " does not exists!");
        }
        Song oldSong = optionalSong.get();
        // Check if Poster and Data updated
        if (!Objects.equals(oldSong.getPoster(), song.getPoster()) || !Objects.equals(oldSong.getSongData(), song.getSongData())){
            // TODO: Call these 2 functions to save data on firebase
//            uploadImage();
//            uploadMp3();
        }
        songService.updateSong(song);
        return ResponseEntity.ok("Song updated successfully");
    }


    /**
     * Get Image from filePath on computer and upload to FirebaseStorage
     * @return
     */
    public String uploadImage() {
        String downloadUrl = null;
        String fileName = new File(filePath).getName();
        try {
            downloadUrl = firebaseStorageService.uploadImage(filePath, fileName);

            return downloadUrl;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get Mp3 data from filePath on computer and upload to FirebaseStorage
     * @return
     */
    public String uploadMp3() {
        String downloadUrl = null;
        String fileName = new File(filePath).getName();
        try {
            downloadUrl = firebaseStorageService.uploadMp3(filePath, fileName);

            return downloadUrl;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
