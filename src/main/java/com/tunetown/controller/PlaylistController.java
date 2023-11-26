package com.tunetown.controller;

import com.tunetown.model.Playlist;
import com.tunetown.model.PlaylistSongs;
import com.tunetown.service.PlaylistService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/playlists")
@Slf4j
public class PlaylistController {
    @Resource
    PlaylistService playlistService;

    @GetMapping("/getUserPlaylists")
    public List<Playlist> getAllPlaylistByUserId(@RequestParam int userId) {
        return playlistService.getAllPlaylistByUserId(userId);
    }

    @GetMapping
    public Playlist getPlaylistById(@RequestParam int playlistId) {
        return playlistService.getPlaylistById(playlistId);
    }

    @DeleteMapping
    public ResponseEntity<String> deletePlaylist(@RequestParam int playlistId) {
        boolean isDeleted = playlistService.deletePlaylist(playlistId);
        if(isDeleted)
            return ResponseEntity.ok("Removed from your Library");
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot removed playlist from your Library");
    }

    @PostMapping
    public void addNewPlaylist(@RequestParam int userId) {
        playlistService.addNewPlaylistToUser(userId);
    }

    @PutMapping
    public void addSongToPlaylist(@RequestParam int songId, @RequestParam int playlistId) {
        playlistService.addSongToPlaylist(songId, playlistId);
    }

    @GetMapping("/getPlaylistSongs")
    public List<PlaylistSongs> getPLaylistSongsById(@RequestParam int playlistId) {
        return playlistService.getPlaylistSongsById(playlistId);
    }

    @PutMapping(value = "/editPlaylist", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void editPlaylist(@RequestBody Playlist playlist) {
        playlistService.modifyPlaylist(playlist, playlist.getPlaylistSongsList());
    }

    @DeleteMapping("/deletePlaylistSongs")
    public void removePlaylistSongs(@RequestBody PlaylistSongs playlistSongs) {
        playlistService.removePlaylistSongs(playlistSongs);
    }

}
