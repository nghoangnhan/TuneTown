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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @PostMapping(path = "/addSongFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<String> addSong(@RequestParam(name = "poster") MultipartFile poster, @RequestParam(name = "songData") MultipartFile songData){
        List<String> listFile = new ArrayList<>();
        listFile.add(uploadImage(poster));
        listFile.add(uploadMp3(songData));
        return listFile;
    }

    @DeleteMapping(path = "/deleteSong")
    public ResponseEntity<String> deleteSong(@RequestParam("songId") int songId){
        songService.deleteSong(songId);
        return ResponseEntity.ok("Song deleted successfully");
    }

    @PutMapping(path = "/updateSong", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSong(@RequestParam("songId") int songId, @RequestBody Song song){
        Optional<Song> optionalSong = songRepository.getSongById(songId);
        if (optionalSong.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song with id = " + songId + " does not exists!");
        }
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
    public String uploadImage(MultipartFile poster) {
        String fileName = poster.getOriginalFilename();
        try {
            return firebaseStorageService.uploadImage(poster, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get Mp3 data from filePath on computer and upload to FirebaseStorage
     * @return
     */
    public String uploadMp3(MultipartFile songData) {
        String fileName = songData.getOriginalFilename();
        try {
            return firebaseStorageService.uploadMp3(songData, fileName, 10);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
