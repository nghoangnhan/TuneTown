package com.tunetown.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    private User author;
    @Lob
    @Column(length = Integer.MAX_VALUE)
    private String content;
    @ManyToOne(fetch = FetchType.EAGER)
    private Song song;
    private String mp3Link;
    @ManyToOne(fetch = FetchType.EAGER)
    private Playlist playlist;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> likes;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Comment> comments;
    private LocalDateTime postTime;
}
