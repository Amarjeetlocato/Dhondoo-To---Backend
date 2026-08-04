package com.whoami.launch.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.LocationResponseDTO;
import com.whoami.launch.entity.CustomerProfile;
import com.whoami.launch.entity.Location;
import com.whoami.launch.dto.LocationCreatedEvent;
import com.whoami.launch.service.LocationService;

@RestController
public class LocationController {
    
    @Autowired
    private LocationService locationService;
    
    // Public API endpoints
    @RequestMapping("/api/locations")
    private static class PublicAPI {}
    
    // GET all locations
    @GetMapping("/api/locations")
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }
    
    // GET location by ID
    @GetMapping("/api/locations/{locationId}")
    public ResponseEntity<Optional<Location>> getLocationById(@PathVariable String locationId) {
        Optional<Location> location = locationService.getLocationById(locationId);
        if (location.isPresent()) {
            return ResponseEntity.ok(location);
        }
        return ResponseEntity.notFound().build();
    }
    
    // GET locations by user ID
    @GetMapping("/api/locations/user/{userId}")
    public ResponseEntity<Optional<Location>> getLocationsByUserId(@PathVariable String userId) {
        Optional<Location> locations = locationService.getLocationsByUserId(userId);
        return ResponseEntity.ok(locations);
    }
    
    // GET locations by user ID ordered by timestamp (most recent first)
    @GetMapping("/api/locations/user/{userId}/recent")
    public ResponseEntity<List<Location>> getLocationsByUserIdRecent(@PathVariable String userId) {
        List<Location> locations = locationService.getLocationsByUserIdOrderedByTime(userId);
        return ResponseEntity.ok(locations);
    }
    
    // GET locations by time range
    @GetMapping("/api/locations/search/time-range")
    public ResponseEntity<List<Location>> getLocationsByTimeRange(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        List<Location> locations = locationService.getLocationsByTimeRange(startTime, endTime);
        return ResponseEntity.ok(locations);
    }
    
    // GET locations by coordinates
    @GetMapping("/api/locations/search/coordinates")
    public ResponseEntity<List<Location>> getLocationsByCoordinates(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        List<Location> locations = locationService.getLocationsByCoordinates(latitude, longitude);
        return ResponseEntity.ok(locations);
    }
    
    @KafkaListener(
    	    topics = "location-created-topic",
    	    groupId = "location-group"
    	)
    	public void createLocation(LocationCreatedEvent event) {

    	    locationService.createLocationFromUser(event);
    	} 
    // PUT update location
    @PutMapping("/api/locations/{userId}")
    public ResponseEntity<Location> updateLocation(@PathVariable String userId, @RequestBody Location locationDetails) {
        Location updatedLocation = locationService.updateLocation(userId, locationDetails);
        if (updatedLocation != null) {
            return ResponseEntity.ok(updatedLocation);
        }
        return ResponseEntity.notFound().build();
    }
    
    // DELETE location
    @DeleteMapping("/api/locations/{locationId}")
    public ResponseEntity<Void> deleteLocation(@PathVariable String locationId) {
        locationService.deleteLocation(locationId);
        return ResponseEntity.noContent().build();
    }
    
    // Internal API endpoints for Feign clients
    @GetMapping("/internal-api/locations/user/{userId}")
    public ResponseEntity<ApiResponse<LocationResponseDTO>> getInternalLocationByUserId(@PathVariable String userId) {
        Optional<Location> location = locationService.getLocationsByUserId(userId);
        if (location.isPresent()) {
            LocationResponseDTO dto = locationService.toResponseDTO(location.get());
            return ResponseEntity.ok(ApiResponse.success("Location retrieved", dto));
        }
        return ResponseEntity.ok(ApiResponse.success("No location found", null));
    }
    
    @GetMapping("/internal-api/locations/user/{userId}/latest")
    public ResponseEntity<ApiResponse<LocationResponseDTO>> getInternalLatestLocationByUserId(@PathVariable String userId) {
        List<Location> locations = locationService.getLocationsByUserIdOrderedByTime(userId);
        if (!locations.isEmpty()) {
            LocationResponseDTO dto = locationService.toResponseDTO(locations.get(0));
            return ResponseEntity.ok(ApiResponse.success("Latest location retrieved", dto));
        }
        return ResponseEntity.ok(ApiResponse.success("No location found", null));
    }
}

