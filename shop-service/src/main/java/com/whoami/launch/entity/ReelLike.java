package com.whoami.launch.entity;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(
            name = "like_id",
            unique = true,
            nullable = false,
            updatable = false,
            length = 30
    )
    private String likeId;

    @Column(name = "user_id", nullable = false, length = 30)
    private String userId;

    @Column(name = "reel_id", nullable = false, length = 30)
    private String reelId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {

        if (likeId == null) {
            likeId = "REEL_LIKE_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}