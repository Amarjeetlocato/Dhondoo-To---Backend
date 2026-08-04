package com.whoami.launch.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
    name = "shop_followers",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"shopId", "userId"})
    }
)
@Data
public class ShopFollower {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String followId;

    @Column(nullable = false)
    private String shopId;

    @Column(nullable = false)
    private String userId;

    private LocalDateTime followedAt = LocalDateTime.now();
}