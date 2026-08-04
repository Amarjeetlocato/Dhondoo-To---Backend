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
 * NotificationPreferences Entity - Stores user notification preferences
 */
@Entity
@Table(name = "notification_preferences", indexes = {
        @Index(name = "idx_pref_user_id", columnList = "user_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preferenceId;

    @Column(nullable = false, length = 36, unique = true)
    private String userId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean orderNotification = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean chatNotification = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean promotionNotification = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean reelNotification = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean productNotification = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean shopNotification = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean serviceNotification = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean adminNotification = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean followNotification = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
