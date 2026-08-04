package com.whoami.launch.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
    name = "reel_likes",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "user_id",
                "reel_id"
            }
        )
    }
)
@Data
public class ReelLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "reel_id", nullable = false)
    private String reelId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
}