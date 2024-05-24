package com.tunetown.model;

import com.google.type.DateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    private User sendUser;
    private UUID receiveUserId;
    private String content;
    private LocalDateTime messageDate;
    private int seen; // 0: unseen, 1: seen
    private int type; // 0: private: 1: community, 2: system
}
