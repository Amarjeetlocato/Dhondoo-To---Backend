package com.whoami.launch.entity;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(
            name = "comment_id",
            unique = true,
            nullable = false,
            updatable = false,
            length = 35
    )
    private String commentId;

    @Column(name = "reel_id", nullable = false, length = 30)
    private String reelId;

    @Column(name = "user_id", nullable = false, length = 30)
    private String userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status = CommentStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (commentId == null) {
            commentId = "REEL_COMMENT_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}