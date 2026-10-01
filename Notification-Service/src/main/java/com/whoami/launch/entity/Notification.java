package com.whoami.launch.entity;

import com.whoami.launch.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, updatable = false, length = 40)
	private String notificationId;
	
    @Column(nullable = false, length = 30)
    private String userId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Column(length = 50)
    private String targetId;

    @Column(length = 50)
    private String targetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
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

    @PrePersist
    protected void prePersist() {
        if (notificationId == null) {
            notificationId = "NOTIFICATION_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}