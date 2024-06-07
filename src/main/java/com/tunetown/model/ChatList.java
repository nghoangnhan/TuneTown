package com.tunetown.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<User> sentUser;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Community> sentCommunity;
}
