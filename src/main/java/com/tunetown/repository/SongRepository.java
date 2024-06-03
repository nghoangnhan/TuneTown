package com.tunetown.repository;

import com.tunetown.model.Genre;
import com.tunetown.model.Song;
import com.tunetown.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, Integer> {
    @Query("SELECT s FROM Song s WHERE s.id = ?1")
    Optional<Song> getSongById(int id);

    @Query("SELECT s FROM Song s WHERE s.status = ?1")
    Page<Song> getByStatus(int status, Pageable pageable);

    @Query("SELECT s FROM Song s")
    List<Song> getAllSongsByAdmin();

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

    @Query("SELECT uh.song FROM UserHistory uh " +
            "WHERE uh.user.id = ?1 " +
            "AND uh.song.status = 1 " +
            "GROUP BY uh.song " +
            "ORDER BY MAX(uh.dateListen) ASC, COUNT(uh) DESC ")
    List<Song> getSongsForListenAgain(UUID userId, Pageable pageable);

    @Query("SELECT s FROM Song s " +
            "WHERE ((:artists1 IS NULL) OR (s.artists IN :artists2)) " +
            "OR ((:genres1 IS NULL) OR (s.genres IN :genres2)) " +
            "ORDER BY RAND()")
    List<Song> getRecommendSongs(@Param("genres1") List<Genre> genres1, @Param("genres2") List<Genre> genres2,
                                 @Param("artists1") List<User> artists1, @Param("artists2") List<User> artists2,
                                 Pageable pageable);

    @Query("SELECT s FROM Song s " +
            "WHERE s.id NOT IN ( SELECT uh.song.id FROM UserHistory uh WHERE uh.user.id = ?1 ) " +
            "ORDER BY RAND()")
    List<Song> getShouldTrySongs(UUID userId, Pageable pageable);
}
