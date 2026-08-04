package com.whoami.launch.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "locations")
public class Location {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String locationId;
    
    @Column(nullable = false)
    private String userId;
    
    @Column
    private Double latitude;
    
    @Column
    private Double longitude;
    
    @Column
    private LocalDateTime timestamp;
    
    // Constructors
    public Location() {
    }
    
    public Location(String userId, Double latitude, Double longitude) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
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