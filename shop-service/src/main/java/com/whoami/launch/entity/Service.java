package com.whoami.launch.entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "services")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 30
    )
    private String serviceId;

    @JsonProperty("businessId")
    @JsonAlias({"shopId", "businessId"})
    @Column(nullable = false, length = 30)
    private String businessId;

    @JsonProperty("serviceName")
    @JsonAlias({"name", "serviceName", "title"})
    @Column(nullable = false)
    private String serviceName;

    private String thumbnailUrl;

    private String thumbnailPublicId;

    private String promoVideoUrl;

    private String videoPublicId;

    @JsonProperty("serviceDescription")
    @JsonAlias({"description", "serviceDescription"})
    @Column(columnDefinition = "TEXT")
    private String serviceDescription;

    private Double price;

    private String duration;

    private String orderType;

    private String suggestion;

    private String visibility;

    private String badges;

    @PrePersist
    protected void prePersist() {
        if (serviceId == null) {
            serviceId = "SERVICE_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}
