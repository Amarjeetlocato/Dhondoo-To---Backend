package com.whoami.businessoperation.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "enterprise_appointments",
        indexes = {
                @Index(name = "idx_appointment_business_id", columnList = "business_id"),
                @Index(name = "idx_appointment_status", columnList = "status")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", unique = true, nullable = false, length = 100)
    private String appointmentId;

    @Column(name = "business_id", nullable = false, length = 100)
    private String businessId;

    @Column(name = "business_name", length = 150)
    private String businessName;

    @Column(name = "contact_person_name", nullable = false, length = 150)
    private String contactPersonName;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(name = "preferred_date_time", length = 100)
    private String preferredDateTime;

    @Column(length = 1000)
    private String notes;

    @Column(length = 50)
    @Builder.Default
    private String status = "PENDING";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.appointmentId == null) {
            this.appointmentId = "APT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
