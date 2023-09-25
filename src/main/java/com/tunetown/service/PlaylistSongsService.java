package com.tunetown.service;

import com.tunetown.model.Playlist;
import com.tunetown.model.PlaylistSongs;
import com.tunetown.model.Song;
import com.tunetown.repository.PlaylistSongsRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class PlaylistSongsService {
    @Resource
    PlaylistSongsRepository playlistSongsRepository;

    public void addSongToPlaylist(Song song, Playlist playlist) {
        PlaylistSongs playlistSongs = new PlaylistSongs();

        playlistSongs.setPlaylist(playlist);
        playlistSongs.setSong(song);
        playlistSongs.setOrderSong(playlistSongsRepository.getNumberOfSongsInPlaylist(playlist.getId()) + 1);

        playlistSongsRepository.save(playlistSongs);
    }
}
