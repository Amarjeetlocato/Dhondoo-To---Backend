package com.whoami.launch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * DeviceToken Entity - Stores Firebase Cloud Messaging device tokens
 */
@Entity
@Table(name = "device_tokens", indexes = {
        @Index(name = "idx_device_user_id", columnList = "user_id"),
        @Index(name = "idx_device_token", columnList = "device_token", unique = true),
        @Index(name = "idx_device_is_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deviceTokenId;

    @Column(nullable = false, length = 36)
    private String userId;

    @Column(nullable = false, columnDefinition = "TEXT", unique = true)
    private String deviceToken;

    @Column(length = 50)
    private String deviceType;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
