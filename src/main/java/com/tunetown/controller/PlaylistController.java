package com.tunetown.controller;

import com.tunetown.model.Playlist;
import com.tunetown.model.PlaylistSongs;
import com.tunetown.service.PlaylistService;
import com.tunetown.service.PlaylistSongsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
@Slf4j
public class PlaylistController {
    @Resource
    PlaylistService playlistService;

    @GetMapping
    public List<Playlist> getAllPlaylistByUserId(@RequestParam int userId) {
        return playlistService.getAllPlaylistByUserId(userId);
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
