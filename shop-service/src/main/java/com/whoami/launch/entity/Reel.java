package com.whoami.launch.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reels")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 30
    )
    private String reelId;

    @Column(nullable = false, length = 30)
    private String businessId;
    

    private String reelVideo;

    private String reelThumbnail;

    private String reelThumbnailPublicId;

    private String reelVideoPublicId;

    @Column(columnDefinition = "TEXT")
    private String reelDescription;

    @Column(columnDefinition = "TEXT")
    private String reelReviews;

    private Double reelRatings;

    @PrePersist
    protected void prePersist() {
        if (reelId == null) {
            reelId = "REEL_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}