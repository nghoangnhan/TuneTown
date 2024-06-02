package com.tunetown.repository;

import com.tunetown.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, Integer> {
    @Query("SELECT s FROM Song s WHERE s.id = ?1")
    Optional<Song> getSongById(int id);

    @Query("SELECT s FROM Song s WHERE s.status = ?1")
    Page<Song> getByStatus(int status, Pageable pageable);

    @Query("SELECT s FROM Song s")
    Page<Song> getAllSongsByAdmin(Pageable pageable);

    @Query("SELECT s FROM Song s JOIN s.artists a WHERE s.status = 1 AND ((s.songName LIKE %:songName% OR a.userName LIKE %:songName%)" +
            " OR (s.songName LIKE %:songName% AND a.userName LIKE %:artistName%) " +
            "OR (CONCAT(s.songName, '', a.userName) LIKE %:songName%) " +
            "OR (CONCAT(a.userName, '', s.songName) LIKE %:songName%) OR s.lyric LIKE %:songName%)")
    List<Song> findSongByNameOrArtist(String songName, String artistName);

    @Query("SELECT s FROM Song s JOIN s.artists a WHERE a.id = ?1")
    Page<Song> songListByArtist(UUID artistId, Pageable pageable);

    @Query("SELECT s FROM Song s JOIN s.artists a WHERE a.id = ?1")
    List<Song> getAllSongsOfArtist(UUID artistId);

    @Query("SELECT s FROM Song s JOIN s.artists a WHERE a.id = ?1 ORDER BY s.listens")
    List<Song> getTopSongsOfArtist(UUID artistID, Pageable pageable);

    @Query(value =
            "SELECT * FROM (" +
                    "(SELECT DISTINCT s.* " +
                        "FROM user u, song s " +
                            "JOIN song_genres sg ON s.id = sg.song_id " +
                            "JOIN user_genres ug ON ug.genres_id = sg.genres_id " +
                    "WHERE u.id = ?1 ) " +
                    "UNION " +
                    "(SELECT DISTINCT s.* " +
                        "FROM song s " +
                        "JOIN user_history uh ON s.id = uh.song_id)) as s " +
            "ORDER BY s.listens DESC LIMIT 100", nativeQuery = true)
    List<Song> getListRecommendedSong(UUID userId);

    @Query("SELECT uh.song FROM UserHistory uh " +
            "WHERE uh.user.id = ?1 " +
            "GROUP BY uh.song " +
            "ORDER BY MAX(uh.dateListen) ASC, COUNT(uh) DESC ")
    List<Song> getSongsForListenAgain(UUID userId, Pageable pageable);
}
