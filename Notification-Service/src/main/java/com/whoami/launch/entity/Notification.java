package com.whoami.launch.entity;

import com.whoami.launch.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification Entity - Represents a notification in the system
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_is_read", columnList = "is_read"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_is_deleted", columnList = "is_deleted"),
        @Index(name = "idx_user_is_read", columnList = "user_id, is_read"),
        @Index(name = "idx_user_is_deleted", columnList = "user_id, is_deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @Column(length = 36)
    private String notificationId;
    

    @Column(nullable = false, length = 36)
    private String userId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Column(length = 36)
    private String targetId;

    @Column(length = 50)
    private String targetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;
    
    @Column(columnDefinition = "LONGTEXT")
    private String metadataJson;
    
    @Column(columnDefinition = "LONGTEXT")
    private String actionsJson;
    
    private String deepLink;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
