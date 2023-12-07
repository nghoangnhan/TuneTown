package com.tunetown.controller;

import com.tunetown.service.PlaylistSongsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/playlistSongs")
@Slf4j
public class PlaylistSongsController {
    @Resource
    PlaylistSongsService playlistSongsService;

    @PostMapping(path = "/orderSong")
    public ResponseEntity<String> orderSong(@RequestParam("songOrder") int songOrder, @RequestParam("playlistId") int playlistId, @RequestParam("anotherSongOrder") int anotherSongOrder){
        if(playlistSongsService.orderPlaylistSong(songOrder, playlistId, anotherSongOrder)){
            return ResponseEntity.ok("Song order successfully");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fail to order song!");
    }
}
