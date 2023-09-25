package com.tunetown.controller;

import com.tunetown.model.Playlist;
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
    public List<Playlist> getAllPlaylistsByUserId(@RequestParam int userId) {
        return playlistService.getAllPlaylistByUserId(userId);
    }

    @PostMapping
    public void addNewPlaylist(@RequestParam int userId) {
        playlistService.addNewPlaylistToUser(userId);
    }
    @PutMapping
    public void addSongToPlaylist(@RequestParam int songId, int playlistId) {
        playlistService.addSongToPlaylist(songId, playlistId);
    }

}
