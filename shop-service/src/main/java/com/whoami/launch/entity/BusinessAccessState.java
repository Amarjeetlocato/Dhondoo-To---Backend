package com.whoami.launch.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.UUID;
@Entity
@Table(name = "business_access_states")
@Data
public class BusinessAccessState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(
            name = "access_state_id",
            unique = true,
            nullable = false,
            updatable = false,
            length = 35
    )
    private String accessStateId;
    @Column(name = "business_id", unique = true, nullable = false, length = 30)
    private String businessId;
    @Column(nullable = false)
    private boolean verified = false;
    @Column(nullable = false)
    private boolean subscriptionActive = false;
    @Column(nullable = false)
    private boolean paymentCompleted = false;
    @PrePersist
    protected void prePersist() {
        if (accessStateId == null) {
            accessStateId = "BUSINESS_ACCESS_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}
