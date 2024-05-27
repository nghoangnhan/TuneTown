package com.tunetown.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Community {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    private UUID communityId;
    private String communityName;
    @OneToMany(fetch = FetchType.EAGER)
    private List<User> hosts;
    @OneToMany(fetch = FetchType.EAGER)
    private List<User> joinUsers;
    @OneToMany(fetch = FetchType.LAZY)
    private List<User> approveRequests;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Message> communityMessages;
    private String communityAvatar;
}
