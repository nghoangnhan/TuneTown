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
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int communityId;
    private String communityName;
    @OneToMany(fetch = FetchType.EAGER)
    private List<User> hosts;
    @OneToMany(fetch = FetchType.EAGER)
    private List<User> joinUsers;
    @OneToMany(fetch = FetchType.LAZY)
    private List<User> approveRequests;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Message> communityMessages;
    private String communityAvatar;
}
