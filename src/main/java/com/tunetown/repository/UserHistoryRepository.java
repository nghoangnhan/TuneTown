package com.tunetown.repository;

import com.tunetown.model.UserHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserHistoryRepository extends JpaRepository<UserHistory, UUID> {
    @Query("SELECT new UserHistory (uh.song, MAX(uh.dateListen)) FROM UserHistory uh " +
            "WHERE uh.user.id = ?1 " +
            "GROUP BY uh.song " +
            "ORDER BY MAX(uh.dateListen) DESC")
    List<UserHistory> getHistoryByUserId(UUID userId);

    @Query("SELECT uh FROM UserHistory uh WHERE uh.dateListen BETWEEN ?1 AND ?2")
    List<UserHistory> getTopSongByPeriod(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT new UserHistory(a, COUNT(uh)) FROM UserHistory uh JOIN uh.song.artists a " +
            "WHERE uh.dateListen BETWEEN ?1 AND ?2 " +
            "GROUP BY a " +
            "ORDER BY COUNT(uh) DESC")
    List<UserHistory> createArtistChart(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
}
