package com.tunetown.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistSongs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    private Playlist playlist;
    @ManyToOne(fetch = FetchType.EAGER)
    private Song song;
    private int orderSong;
}
