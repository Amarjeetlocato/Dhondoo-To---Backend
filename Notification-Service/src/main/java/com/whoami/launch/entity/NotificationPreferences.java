package com.whoami.launch.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

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
	private Long id;

	@Column(nullable = false, unique = true, length = 40)
	private String preferenceId;

    @Column(nullable = false, length = 30, unique = true)
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

    @PrePersist
    protected void prePersist() {
        if (preferenceId == null) {
            preferenceId = "PREFERENCE_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}