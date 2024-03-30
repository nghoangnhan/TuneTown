package com.tunetown.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String content;
    @ManyToOne(fetch = FetchType.EAGER)
    private Song song;
    @ManyToOne(fetch = FetchType.EAGER)
    private Playlist playlist;
    private Integer likes;
    private Integer dislikes;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Comment> comments;
}
