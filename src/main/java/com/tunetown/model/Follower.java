package com.tunetown.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User follower;
    @ManyToOne(fetch = FetchType.EAGER)
    private User subject;

    @Temporal(TemporalType.DATE)
    private LocalDate followedDate;

    public Follower(UUID followerId, UUID subjectId) {
        this.follower = new User(followerId);
        this.subject = new User(subjectId);
    }
}
