package com.tunetown.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ChatDeleted {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private UUID userId;
    private UUID sentUserId;
    private LocalDateTime timeDeleted;
}
