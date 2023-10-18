package com.tunetown.controller;

import com.tunetown.model.Song;
import com.tunetown.repository.SongRepository;
import com.tunetown.service.FirebaseStorageService;
import com.tunetown.service.SongService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/songs")
public class SongController {
    @Resource
    SongService songService;

    @Resource
    SongRepository songRepository;

    @Resource
    FirebaseStorageService firebaseStorageService;

    // TODO: Get filePath from FileChooser on Front-End
    String filePath = "E:\\Nhạc nền\\Nhói Lòng Thuyền Hoa Remix.mp3";

    /**
     * Get songs by numbers in each page using Paging Technique
     * @param page: Page number 1 for default
     * @param size: size of items each page
     * @return
     */
    @GetMapping
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

    @DeleteMapping(path = "/deleteSong")
    public ResponseEntity<String> deleteSong(@RequestParam("songId") int songId){
        songService.deleteSong(songId);
        return ResponseEntity.ok("Song deleted successfully");
    }

    @PutMapping(path = "/updateSong", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSong(@RequestParam("songId") int songId, @RequestBody Song song){
        Optional<Song> optionalSong = songRepository.getSongById(songId);
        if (!optionalSong.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song with id = " + songId + " does not exists!");
        }
        Song oldSong = optionalSong.get();
        // Check if Poster and Data updated
//        if (!Objects.equals(oldSong.getPoster(), song.getPoster()) || !Objects.equals(oldSong.getSongDatas(), song.getSongDatas())){
//            // TODO: Call these 2 functions to save data on firebase
////            uploadImage();
//            uploadMp3();
//            song.setSongData(uploadMp3());
//        }
        songService.updateSong(song);
        return ResponseEntity.ok("Song updated successfully");
    }


    @PostMapping(path = "/findSong")
    public List<Song> findSong(@RequestParam("name") String name){
        List<Song> listSong = songService.findSongByNameOrArtist(name);
        return listSong;
    }


    /**
     * Get Image from filePath on computer and upload to FirebaseStorage
     * @return
     */
    public void uploadImage() {
        String fileName = new File(filePath).getName();
        try {
            firebaseStorageService.uploadImage(filePath, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get Mp3 data from filePath on computer and upload to FirebaseStorage
     * @return
     */
//    @PostMapping("uploadMp3")
    public String uploadMp3() {
        String fileName = new File(filePath).getName();
        try {
            String songData = firebaseStorageService.uploadMp3(filePath, fileName, 10);
            return songData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
