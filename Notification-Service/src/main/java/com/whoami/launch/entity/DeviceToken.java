package com.whoami.launch.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

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
	private Long id;

	@Column(nullable = false, unique = true, updatable = false, length = 40)
	private String deviceTokenId;

    @Column(nullable = false, length = 30)
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

    @PrePersist
    protected void prePersist() {
        if (deviceTokenId == null) {
            deviceTokenId = "DEVICE_TOKEN_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}