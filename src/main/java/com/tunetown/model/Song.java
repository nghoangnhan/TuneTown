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
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String songName;
    private String poster;
    @OneToMany(fetch = FetchType.EAGER)
    private List<User> artists;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Genre> genres;
    private String songData;
    private int likes;
    private int listens;


}
