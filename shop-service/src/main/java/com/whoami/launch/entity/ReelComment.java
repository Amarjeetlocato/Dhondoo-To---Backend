package com.whoami.launch.entity;

import java.time.LocalDateTime;

import com.whoami.launch.enums.CommentStatus;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "reel_comments")
@Data
public class ReelComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reel_id", nullable = false)
    private String reelId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;
    
    @Enumerated(EnumType.STRING)
    private CommentStatus status = CommentStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
}