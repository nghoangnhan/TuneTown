package com.tunetown.service;

import com.tunetown.model.Playlist;
import com.tunetown.model.PlaylistSongs;
import com.tunetown.model.Song;
import com.tunetown.model.User;
import com.tunetown.repository.PlaylistRepository;
import com.tunetown.repository.PlaylistSongsRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PlaylistService {
    @Resource
    PlaylistRepository playlistRepository;
    @Resource
    UserService userService;
    @Resource
    SongService songService;
    @Resource
    PlaylistSongsRepository playlistSongsRepository;

    public void addNewPlaylistToUser(UUID userId) {
        User user = userService.getUserById(userId);

        Playlist playlist = new Playlist();
        playlist.setUser(user);
        playlist.setPlaylistName("New playlist");
        playlist.setPlaylistType("Private");

        playlistRepository.save(playlist);
    }

    /**
     * Get playlist's information by playlistId
     */
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

    public List<Playlist> getAllPlaylistByUserId(UUID userId) {
        return playlistRepository.getAllPlaylistsByUserId(userId);
    }
    public List<PlaylistSongs> getPlaylistSongsById(int playlistId) {
        return playlistSongsRepository.getPlaylistSongsById(playlistId);
    }

    @Transactional
    public void modifyPlaylist(Playlist modifiedPlaylist, List<PlaylistSongs> playlistSongsList) {
        Optional<Playlist> optionalPlaylist = playlistRepository.findById(modifiedPlaylist.getId());
        log.info("playlistId: " + modifiedPlaylist.getId());
        if(optionalPlaylist.isPresent()) {
            log.info("presented");
            Playlist dbPlaylist = optionalPlaylist.get();

            // Setting basic information of Playlist
            dbPlaylist.setPlaylistName(modifiedPlaylist.getPlaylistName());
            dbPlaylist.setPlaylistType(modifiedPlaylist.getPlaylistType());
            dbPlaylist.setCoverArt(modifiedPlaylist.getCoverArt());

            // Setting playlistSongs in Playlist
            for(PlaylistSongs pSong : playlistSongsList) {
                // if pSong has already added to playlist -> then modify the information
                // else -> add new song to playlist
                Optional<PlaylistSongs> optionalDbSong = playlistSongsRepository.findById(pSong.getId());
                if(optionalDbSong.isPresent()) {
                    PlaylistSongs dbSong = optionalDbSong.get();
                    dbSong.setOrderSong(pSong.getOrderSong());
                }
                else {
                    addSongToPlaylist(pSong.getSong().getId(), pSong.getPlaylist().getId());
                }
            }
        }
    }

    @Transactional
    public void removePlaylistSongs(PlaylistSongs playlistSongs) {
        boolean isAfterDeletedSong = false;

        List<PlaylistSongs> playlistSongsList = getPlaylistSongsById(playlistSongs.getPlaylist().getId());
        for (PlaylistSongs song: playlistSongsList) {
            if(isAfterDeletedSong) {
                log.info(song.getId() + " is after deleted song");
                song.setOrderSong(song.getOrderSong() - 1);
            }
            else if(song.getId() == playlistSongs.getId())
                isAfterDeletedSong = true;
        }
        playlistSongsRepository.saveAll(playlistSongsList);
        playlistSongsRepository.delete(playlistSongs);
    }

    public boolean deletePlaylist(int playlistId) {
        Playlist playlist = getPlaylistById(playlistId);
        try {
            List<PlaylistSongs> playlistSongsList = getPlaylistSongsById(playlistId);
            for(PlaylistSongs ps : playlistSongsList) {
                removePlaylistSongs(ps);
            }
            playlistRepository.delete(playlist);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * Create a playlist based on recommended songs.
     */
    public Playlist createRecommendedPlaylist(User user) {
        Optional<Playlist> optionalPlaylist = playlistRepository.getUserRecommendedPlaylist(user.getId());
        Playlist savedPlaylist;
        if(optionalPlaylist.isEmpty()) {
            Playlist playlist = new Playlist();
            playlist.setUser(user);
            playlist.setPlaylistName("Songs from what you like");
            playlist.setPlaylistType("Recommended");

            savedPlaylist = playlistRepository.save(playlist);
        }
        else {
            savedPlaylist = optionalPlaylist.get();
            List<PlaylistSongs> playlistSongs = playlistSongsRepository.getPlaylistSongsById(savedPlaylist.getId());
            playlistSongsRepository.deleteAll(playlistSongs);
        }

        List<Song> recommendedSongs = songService.getRecommendedSongs(user);
        for(Song song : recommendedSongs)
            addSongToPlaylist(song.getId(), savedPlaylist.getId());

        return savedPlaylist;
    }

    public List<Playlist> getPublicPlaylist(UUID userId) {
        return playlistRepository.getPublicPlaylist(userId);
    }

    public void savePlaylist(UUID userId, int playlistId) {
        Playlist dbPlaylist = getPlaylistById(playlistId);

        Playlist playlist = new Playlist();
        playlist.setUser(new User(userId));
        playlist.setPlaylistType("Private");
        playlist.setPlaylistName(dbPlaylist.getPlaylistName());
        playlist.setCoverArt(dbPlaylist.getCoverArt());

        Playlist savedPlaylist = playlistRepository.save(playlist);
        List<PlaylistSongs> playlistSongs = playlistSongsRepository.getPlaylistSongsById(playlistId);

        List<PlaylistSongs> newPlaylistSongs = new ArrayList<>();
        for(PlaylistSongs ps : playlistSongs) {
            PlaylistSongs newPS = new PlaylistSongs();
            newPS.setSong(ps.getSong());
            newPS.setPlaylist(savedPlaylist);
            newPS.setOrderSong(ps.getOrderSong());
            newPlaylistSongs.add(newPS);
        }
        playlistSongsRepository.saveAll(newPlaylistSongs);
    }
}
