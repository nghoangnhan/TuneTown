package com.tunetown.repository;

import com.tunetown.model.User;
import com.tunetown.model.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserHistoryRepository extends JpaRepository<UserHistory, UUID> {
    @Query("SELECT uh FROM UserHistory uh WHERE uh.user.id = ?1")
    List<UserHistory> getHistoryByUserId(UUID userId);

    @Query("SELECT uh FROM UserHistory uh WHERE uh.dateListen BETWEEN ?1 AND ?2")
    List<UserHistory> getTopSongByPeriod(LocalDateTime startTime, LocalDateTime endTime);
}
