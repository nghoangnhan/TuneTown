package com.tunetown.controller;

import com.tunetown.model.Song;
import com.tunetown.repository.SongRepository;
import com.tunetown.service.FirebaseStorageService;
import com.tunetown.service.SongService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/songs")
@Slf4j
public class SongController {
    @Resource
    SongService songService;
    @Resource
    FirebaseController firebaseController;

    /**
     * Get songs by numbers in each page using Paging Technique
     * @param page: Page number 1 for default
     * @param size: size of items each page
     */
    @GetMapping
    public Map<String, Object> getAllSongs(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Song> songPage = songService.getAllSongs(pageRequest);
        return Map.of(
                "songList", songPage.getContent(),
                "currentPage", songPage.getNumber() + 1,
                "totalPages", songPage.getTotalPages(),
                "totalElement", songPage.getTotalElements()
        );
    }
    @GetMapping("/getSongById")
    public Song getSongById(@RequestParam int songId) {
        return songService.getActiveSongById(songId);
    }

    @PostMapping(path = "/addSong", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addSong(@RequestBody Song song ){
        songService.addSong(song);
        return ResponseEntity.ok("Song added successfully");
    }

    /**
     * Upload poster and data on firebase and get data string
     * @param poster
     * @param songData
     * @return
     */
    @PostMapping(path = "/addSongFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<String> addSong(@RequestBody MultipartFile poster, @RequestBody MultipartFile songData){
        List<String> listFile = new ArrayList<>();
        if(firebaseStorageService.checkValidImage(poster)){
            listFile.add(uploadImage(poster));
        }
        else{
            throw new RuntimeException("Invalid image");
        }

        if(firebaseStorageService.checkValidMp3(songData)){
            listFile.add(uploadMp3(songData));
        }
        else{
            throw new RuntimeException("Invalid mp3 file");
        }
   
    @DeleteMapping(path = "/deleteSong")
    public ResponseEntity<String> deleteSong(@RequestParam("songId") int songId, @RequestHeader("Authorization") String accessToken){
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access token is missing");
        }
        boolean isDeleted = songService.deleteSong(songId, accessToken);
        if(isDeleted){
            return ResponseEntity.ok("Song deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete song, you are not the artist!");
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSong(@RequestBody Song song, @RequestHeader("Authorization") String accessToken){
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access token is missing");
        }

        boolean isUpdated = songService.updateSong(song, accessToken);
        if(isUpdated){
            return ResponseEntity.ok("Song updated successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update song, you are not the artist!");
    }


    @PostMapping(path = "/findSong")
    public List<Song> findSong(@RequestParam("name") String name){
        List<Song> listSong = songService.findSongByNameOrArtist(name);
        return listSong;
    }

    @GetMapping(path = "/getSongById")
    public Song getSongById(@RequestParam("songId") int id){
        Song song = songService.getActiveSongById(id);
        return song;
    }
}
