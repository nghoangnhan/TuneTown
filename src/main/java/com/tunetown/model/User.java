package com.tunetown.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String userName;
    private String email;
    private String password;
    private String role;
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    private String userBio;
    private String avatar;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Song> history;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Genre> favoriteGenres;
    @OneToMany(fetch = FetchType.LAZY)
    private List<User> followingArtists;
    private String method;
}
