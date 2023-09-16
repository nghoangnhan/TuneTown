package com.tunetown.controller;

import com.tunetown.model.Song;
import com.tunetown.repository.SongRepository;
import com.tunetown.service.SongService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class SongController {
    @Resource
    SongService songService;

    @Resource
    SongRepository songRepository;


    @GetMapping(path = "/song/getAll")
    public List<Song> getAllSongs(){
        return songService.getAllSongs();
    }

    @PostMapping(path = "/addSong", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addSong(@RequestBody Song song ){
        songService.addSong(song);
    }


    @DeleteMapping(path = "/deleteSong/{songId}")
    public void deleteSong(@PathVariable("songId") int id){
        songService.deleteSong(id);
    }

    @PutMapping(path = "/updateSong/{songID}")
    public void updateStudent(@PathVariable("songID") int id,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String poster,
                              @RequestParam(required = false) String data,
                              @RequestParam(required = false) Integer listens,
                              @RequestParam(required = false) Integer likes,
                              @RequestParam(required = false) Integer status){
        Optional<Song> optionalSong = songRepository.getSongById(id);
        if(listens == null){
            listens = optionalSong.get().getListens();
        }

        if(likes == null){
            likes = optionalSong.get().getLikes();
        }
        songService.updateSong(id, name, poster, data, listens, likes, status);
    }

}
