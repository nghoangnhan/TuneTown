package com.tunetown.controller;

import com.tunetown.model.Genre;
import com.tunetown.model.Song;
import com.tunetown.model.User;
import com.tunetown.service.FirebaseStorageService;
import com.tunetown.service.SongService;
import com.tunetown.service.UserService;
import com.tunetown.service.jwt.JwtService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/songs")
public class SongController {
    @Resource
    SongService songService;

    @Resource
    FirebaseStorageService firebaseStorageService;
    @Resource
    JwtService jwtService;
    @Resource
    UserService userService;

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

    @GetMapping("/getAllSongsByAdmin")
    public Map<String, Object> getAllSongsByAdmin(){
        List<Song> songList = songService.getAllSongsByAdmin();
        return Map.of(
                "songList", songList
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
    public List<String> addSong(@RequestBody MultipartFile poster, @RequestBody MultipartFile songData){
        List<String> listFile = new ArrayList<>();
        if(firebaseStorageService.checkValidImage(poster)){
            listFile.add(uploadImage(poster));
        }
        else{
            throw new RuntimeException("Invalid image! Image size > 1MB or the file type is not PNG or JPEG");
        }

        if(firebaseStorageService.checkValidMp3(songData)){
            listFile.add(uploadMp3(songData));
        }
        else{
            throw new RuntimeException("Invalid MP3 file! MP3 file size > 10MB or the file type is not MP3");
        }
        return listFile;
    }

    @DeleteMapping(path = "/deleteSong")
    public ResponseEntity<String> deleteSong(@RequestParam("songId") int songId, @RequestHeader("Authorization") String accessToken){
        if(accessToken.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Access token is missing!");
        }
        if(songService.deleteSong(songId, accessToken)){
            return ResponseEntity.ok("Song deleted successfully");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not the artist!");
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSong(@RequestBody Song song, @RequestHeader("Authorization") String accessToken){
        if(accessToken.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Access token is missing!");
        }
        if(songService.updateSong(song, accessToken)){
            return ResponseEntity.ok("Song updated successfully");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not the artist!");
    }


    @PostMapping(path = "/findSong")
    public List<Song> findSong(@RequestParam("name") String name){
        return songService.findSongByNameOrArtist(name);
    }


    /**
     * Get Image from filePath on computer and upload to FirebaseStorage
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
     */
    public String uploadMp3(MultipartFile songData) {
        String fileName = songData.getOriginalFilename();
        try {
            return firebaseStorageService.uploadMp3(songData, fileName, 10);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Group the songs in list by Id and count the time listened
     */
    @PostMapping("/getTopSong")
    public List<Map<String, Object>> getTopSongByPeriod(@RequestParam("startTime") String startString, @RequestParam("endTime") String endString){
        // Convert to LocalDateTime before compare
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        // Get the start time of startDate
        LocalDateTime startDate = LocalDate.parse(startString, formatter).atStartOfDay();
        // Get the end time of endDate
        LocalDateTime endDate = LocalDate.parse(endString, formatter).atTime(LocalTime.MAX);

        return songService.getTopSongByPeriod(startDate, endDate);
    }

    @GetMapping("/getAllGenres")
    public List<Genre> getALlGenres(){
        return songService.getAllGenres();
    }

    @GetMapping("/getSongsByArtist")
    public Map<String, Object> getSongsByArtist(@RequestParam UUID artistID, @RequestParam(defaultValue = "1") int pageNo) {
        Page<Song> songPage = songService.getSongsByArtist(artistID, pageNo);
        int page = songPage.getNumber();
        List<Song> songList = songPage.getContent();
        return Map.of(
                "pageNo", page + 1,
                "songList", songList
        );
    }

    @GetMapping("/listenAgain")
    public Map<String, Object> getSongsForListenAgain(@RequestHeader("Authorization") String accessToken) {
        String token = accessToken.substring(6);
        String email = jwtService.extractUserEmail(token);
        User user = userService.getActiveUserByEmail(email);

        return Map.of(
                "songs", songService.getSongsForListenAgain(user.getId())
        );
    }

    @PostMapping("/combineData")
    public byte[] combineData(@RequestParam("songId") Integer songId){
        byte[] mp3Data = firebaseStorageService.combineMP3(songId);
        return mp3Data;
    }
}
