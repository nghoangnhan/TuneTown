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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    /**
     * Group the songs in list by Id and count the time listened
     * @param startString
     * @param endString
     * @return
     */
    @PostMapping("/getTopSong")
    public Map<Integer, Long> getTopSongByPeriod(@RequestParam("startTime") String startString, @RequestParam("endTime") String endString){
        // Convert to LocalDateTime before compare
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        // Get the start time of startDate
        LocalDateTime startDate = LocalDate.parse(startString, formatter).atStartOfDay();
        // Get the end time of endDate
        LocalDateTime endDate = LocalDate.parse(endString, formatter).atTime(LocalTime.MAX);;

        List<Song> topSongList = songService.getTopSongByPeriod(startDate, endDate);

        // Count the number of times each song with the same ID was listened to
        Map<Integer, Long> songCount = topSongList.stream()
                .collect(Collectors.groupingBy(Song::getId, Collectors.counting()));

        return songCount;
    }
}
