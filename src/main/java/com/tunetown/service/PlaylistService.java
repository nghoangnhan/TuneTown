package com.tunetown.service;

import com.tunetown.model.Playlist;
import com.tunetown.model.PlaylistSongs;
import com.tunetown.model.Song;
import com.tunetown.model.User;
import com.tunetown.repository.PlaylistRepository;
import com.tunetown.repository.PlaylistSongsRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
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
    @Resource
    PlaylistSongsRepository playlistSongsRepository;

    @Transactional
    public void addNewPlaylistToUser(int userId) {
        User user = userService.getUserById(userId);

        Playlist playlist = new Playlist();
        playlist.setUser(user);
        playlist.setPlaylistName("New playlist");
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
    public void addSongToPlaylist(int songId, int playlistId) {
        Song song = songService.getActiveSongById(songId);
        Playlist playlist = getPlaylistById(playlistId);

        PlaylistSongs playlistSongs = new PlaylistSongs();
        playlistSongs.setSong(song);
        playlistSongs.setPlaylist(playlist);
        playlistSongs.setOrderSong(playlistSongsRepository.getNumberOfSongsInPlaylist(playlistId) + 1);

        playlistSongsRepository.save(playlistSongs);
    }

    public List<Playlist> getAllPlaylistByUserId(int userId) {
        return playlistRepository.getAllPlaylistsByUserId(userId);
    }
    public List<PlaylistSongs> getPlaylistSongsById(int playlistId) {
        return playlistSongsRepository.getPlaylistSongsById(playlistId);
    }

    @Transactional
    public void modifyPlaylist(Playlist modifiedPlaylist) {
        Optional<Playlist> optionalPlaylist = playlistRepository.findById(modifiedPlaylist.getId());
        if(optionalPlaylist.isPresent()) {
            Playlist dbPlaylist = optionalPlaylist.get();

            dbPlaylist.setPlaylistName(modifiedPlaylist.getPlaylistName());
            dbPlaylist.setPlaylistType(modifiedPlaylist.getPlaylistType());
            dbPlaylist.setCoverArt(modifiedPlaylist.getCoverArt());
        }
    }

    @Transactional
    public void swapPlaylistSongsOrder(PlaylistSongs ps1, PlaylistSongs ps2) {
        int tempOrder = ps1.getOrderSong();

        ps1.setOrderSong(ps2.getOrderSong());
        ps2.setOrderSong(tempOrder);
    }

    public void removePlaylistSongs(PlaylistSongs playlistSongs) {
        playlistSongsRepository.delete(playlistSongs);
    }
}
