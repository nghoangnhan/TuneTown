package com.tunetown.service;

import com.tunetown.model.Playlist;
import com.tunetown.model.Song;
import com.tunetown.model.User;
import com.tunetown.repository.PlaylistRepository;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {
    @Resource
    PlaylistRepository playlistRepository;
    @Resource
    UserService userService;
    @Resource
    SongService songService;

    public void addNewPlaylistToUser(int userId) {
        User user = userService.getUserById(userId);

        Playlist playlist = new Playlist();
        playlist.setUser(user);
        playlist.setPlaylistType("Private");

        playlistRepository.save(playlist);
    }
    public Playlist getPlaylistById(int playlistId) {
        Optional<Playlist> optionalPlaylist = playlistRepository.findById(playlistId);
        if(optionalPlaylist.isPresent())
            return optionalPlaylist.get();
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Playlist found with Id: " + playlistId);
    }
    public void addSongToPlaylist(int playlistId, int songId) {
        Song song = songService.getActiveSongById(songId);
        Playlist playlist = getPlaylistById(playlistId);
    }

    public List<Playlist> getAllPlaylistByUserId(int userId) {
        return playlistRepository.getAllPlaylistsByUserId(userId);
    }
}
