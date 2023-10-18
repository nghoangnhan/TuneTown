package com.tunetown.controller;

import com.tunetown.model.Playlist;
import com.tunetown.model.PlaylistSongs;
import com.tunetown.service.PlaylistService;
import com.tunetown.service.PlaylistSongsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {
    @Resource
    PlaylistService playlistService;
    @Resource
    PlaylistSongsService playlistSongsService;

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

    @PutMapping("/swapPlaylistSongsOrder")
    public void swapPlaylistSongsOrder(@RequestBody PlaylistSongs playlistSongs1, @RequestBody PlaylistSongs playlistSongs2) {
        playlistService.swapPlaylistSongsOrder(playlistSongs1, playlistSongs2);
    }

    @DeleteMapping("/deletePlaylistSongs")
    public void removePlaylistSongs(@RequestBody PlaylistSongs playlistSongs) {
        playlistService.removePlaylistSongs(playlistSongs);
    }

}
