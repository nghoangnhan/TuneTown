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
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String playlistName;
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    private String playlistType;
    private String coverArt;

    @Transient
    private List<PlaylistSongs> playlistSongsList;
}
