package com.tunetown.service;

import com.tunetown.model.Playlist;
import com.tunetown.model.PlaylistSongs;
import com.tunetown.model.Song;
import com.tunetown.repository.PlaylistRepository;
import com.tunetown.repository.PlaylistSongsRepository;
import com.tunetown.repository.SongRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PlaylistSongsService {
    @Resource
    PlaylistSongsRepository playlistSongsRepository;
    @Resource
    SongRepository songRepository;
    @Resource
    PlaylistRepository playlistRepository;

    public void addSongToPlaylist(Song song, Playlist playlist) {
        PlaylistSongs playlistSongs = new PlaylistSongs();

        playlistSongs.setPlaylist(playlist);
        playlistSongs.setSong(song);
        playlistSongs.setOrderSong(playlistSongsRepository.getNumberOfSongsInPlaylist(playlist.getId()) + 1);

        playlistSongsRepository.save(playlistSongs);
    }

    public boolean orderPlaylistSong(int songOrder, int playlistId, int anotherSongOrder){
        Optional<Playlist> optionalPlaylist = playlistRepository.findById(playlistId);
        if(!optionalPlaylist.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist with id " + playlistId + " not found!");
        }

        try {
            List<PlaylistSongs> playlistSongs = playlistSongsRepository.getPlaylistSongsById(playlistId);
            PlaylistSongs currentSong = playlistSongs.get(songOrder - 1);
            PlaylistSongs anotherSong = playlistSongs.get(anotherSongOrder - 1);

            if(songOrder < anotherSongOrder){
                currentSong.setOrderSong(anotherSongOrder);
                playlistSongsRepository.save(currentSong);
                anotherSong.setOrderSong(anotherSongOrder - 1);

                List<PlaylistSongs> subList1 = playlistSongs.subList(songOrder, anotherSongOrder - 1);

                for (PlaylistSongs songSubList1 : subList1
                ) {
                    songSubList1.setOrderSong(songSubList1.getOrderSong() - 1);
                    playlistSongsRepository.save(songSubList1);
                }

                if(anotherSongOrder < playlistSongs.size()){
                    List<PlaylistSongs> subList2 = playlistSongs.subList(anotherSongOrder, playlistSongs.size()-1);

                    for (PlaylistSongs songSubList2 : subList2
                    ) {
                        songSubList2.setOrderSong(songSubList2.getOrderSong());
                        playlistSongsRepository.save(songSubList2);
                    }
                }
            }
            else{
                currentSong.setOrderSong(anotherSongOrder);
                playlistSongsRepository.save(currentSong);

                List<PlaylistSongs> subList1 = playlistSongs.subList(anotherSongOrder-1, songOrder-1);

                for (PlaylistSongs songSubList1 : subList1
                ) {
                    songSubList1.setOrderSong(songSubList1.getOrderSong() + 1);
                    playlistSongsRepository.save(songSubList1);
                }
            }
            return true;
        }
        catch (Exception e){
            log.error(e.getMessage());
        }

        return false;
    }
}
