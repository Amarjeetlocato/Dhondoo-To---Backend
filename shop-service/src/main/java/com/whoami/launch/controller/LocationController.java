package com.whoami.launch.controller;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.LocationResponseDTO;
import com.whoami.launch.entity.Location;
import com.whoami.launch.service.LocationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {

        return ResponseEntity.ok(
                locationService.getAllLocations()
        );
    }

    // ================= GET BY ID =================

    @GetMapping("/{locationId}")
    public ResponseEntity<Location> getLocationById(
            @PathVariable String locationId) {

        return locationService
                .getLocationById(locationId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // ================= GET BY USER =================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Location>> getLocationsByUserId(
            @PathVariable String userId) {

        return ResponseEntity.ok(
                locationService.getLocationsByUserId(userId)
        );
    }

    // ================= GET RECENT BY USER =================

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<Location>>
    getLocationsByUserIdRecent(
            @PathVariable String userId) {

        return ResponseEntity.ok(
                locationService
                        .getLocationsByUserIdOrderedByTime(userId)
        );
    }

    // ================= SEARCH BY TIME =================

    @GetMapping("/search/time-range")
    public ResponseEntity<List<Location>> getLocationsByTimeRange(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {

        return ResponseEntity.ok(
                locationService.getLocationsByTimeRange(
                        startTime,
                        endTime
                )
        );
    }

    // ================= SEARCH BY COORDINATES =================

    @GetMapping("/search/coordinates")
    public ResponseEntity<List<Location>> getLocationsByCoordinates(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        return ResponseEntity.ok(
                locationService.getLocationsByCoordinates(
                        latitude,
                        longitude
                )
        );
    }

    // ================= UPDATE =================

    @PutMapping("/{locationId}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable String locationId,
            @RequestBody Location locationDetails) {

        Location updatedLocation =
                locationService.updateLocation(
                        locationId,
                        locationDetails
                );

        return ResponseEntity.ok(updatedLocation);
    }

    // ================= DELETE =================

    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable String locationId) {

        locationService.deleteLocation(locationId);

        return ResponseEntity.noContent().build();
    }

    // ================= INTERNAL - USER =================

    @GetMapping("/internal/user/{userId}")
    public ResponseEntity<ApiResponse<LocationResponseDTO>>
    getInternalLocationByUserId(
            @PathVariable String userId) {

        List<Location> locations =
                locationService.getLocationsByUserId(userId);

        if (locations.isEmpty()) {
            return ResponseEntity.ok(
                    ApiResponse.success(
                            "No location found",
                            null
                    )
            );
        }

        LocationResponseDTO dto =
                locationService.toResponseDTO(
                        locations.get(0)
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Location retrieved",
                        dto
                )
        );
    }

    // ================= INTERNAL - LATEST =================

    @GetMapping("/internal/user/{userId}/latest")
    public ResponseEntity<ApiResponse<LocationResponseDTO>>
    getInternalLatestLocationByUserId(
            @PathVariable String userId) {

        List<Location> locations =
                locationService
                        .getLocationsByUserIdOrderedByTime(userId);

        if (locations.isEmpty()) {
            return ResponseEntity.ok(
                    ApiResponse.success(
                            "No location found",
                            null
                    )
            );
        }

        LocationResponseDTO dto =
                locationService.toResponseDTO(
                        locations.get(0)
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Latest location retrieved",
                        dto
                )
        );
    }
}