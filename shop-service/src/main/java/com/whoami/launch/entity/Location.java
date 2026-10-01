package com.whoami.launch.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "locations")
public class Location {

    /**
     * Internal database primary key.
     * Not shared between microservices.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public location identity.
     * Example: LOCATION_A7K92M4X
     */
    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 30
    )
    private String locationId;

    /**
     * User Registry identity.
     */
    @Column(
            nullable = false,
            length = 30
    )
    private String userId;

    /**
     * User latitude.
     */
    private Double latitude;

    /**
     * User longitude.
     */
    private Double longitude;

    /**
     * Time when this location was recorded.
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Location() {
    }

    public Location(
            String userId,
            Double latitude,
            Double longitude
    ) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = LocalDateTime.now();
    }

    @PrePersist
    protected void prePersist() {

        if (locationId == null) {
            locationId = "LOCATION_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }

        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}