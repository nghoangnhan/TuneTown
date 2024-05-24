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
    private UUID userId;
    @ElementCollection
    private List<UUID> sentUser;
    @ElementCollection
    private List<UUID> sentCommunity;
}
