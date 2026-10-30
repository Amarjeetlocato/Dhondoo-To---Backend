package com.whoami.launch.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.locato.constants.events.location.LocationCreatedEvent;
import com.whoami.launch.dto.LocationResponseDTO;
import com.whoami.launch.entity.Location;
import com.whoami.launch.repository.LocationRepository;
import com.whoami.launch.service.LocationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl
        implements LocationService {

    private final LocationRepository locationRepository;

    // ================= CREATE =================

    @Override
    public Location createLocation(
            Location location) {

        location.setTimestamp(
                LocalDateTime.now()
        );

        return locationRepository.save(location);
    }

    // ================= UPDATE =================

    @Override
    public Location updateLocation(
            String locationId,
            Location locationDetails) {

        Location existingLocation =
                locationRepository.findByLocationId(locationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Location not found: "
                                                + locationId
                                )
                        );

        if (locationDetails.getLatitude() != null) {

            existingLocation.setLatitude(
                    locationDetails.getLatitude()
            );
        }

        if (locationDetails.getLongitude() != null) {

            existingLocation.setLongitude(
                    locationDetails.getLongitude()
            );
        }

        existingLocation.setTimestamp(
                LocalDateTime.now()
        );

        return locationRepository.save(
                existingLocation
        );
    }

    // ================= GET BY ID =================

    @Override
    public Optional<Location> getLocationById(
            String locationId) {

        return locationRepository
                .findByLocationId(locationId);
    }

    // ================= GET BY USER =================

    @Override
    public List<Location> getLocationsByUserId(
            String userId) {

        return locationRepository
                .findByUserIdOrderByTimestampDesc(
                        userId
                );
    }

    // ================= GET BY USER + TIME =================

    @Override
    public List<Location> getLocationsByUserIdOrderedByTime(
            String userId) {

        return locationRepository
                .findByUserIdOrderByTimestampDesc(
                        userId
                );
    }

    // ================= GET BY TIME RANGE =================

    @Override
    public List<Location> getLocationsByTimeRange(
            LocalDateTime startTime,
            LocalDateTime endTime) {

        return locationRepository
                .findByTimestampBetween(
                        startTime,
                        endTime
                );
    }

    // ================= GET BY COORDINATES =================

    @Override
    public List<Location> getLocationsByCoordinates(
            Double latitude,
            Double longitude) {

        return locationRepository
                .findByLatitudeAndLongitude(
                        latitude,
                        longitude
                );
    }

    // ================= GET ALL =================

    @Override
    public List<Location> getAllLocations() {

        return locationRepository.findAll();
    }

    // ================= DELETE =================

    @Override
    public void deleteLocation(
            String locationId) {

        Location location =
                locationRepository
                        .findByLocationId(locationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Location not found: "
                                                + locationId
                                )
                        );

        locationRepository.delete(location);
    }

    // ================= RESPONSE DTO =================

    @Override
    public LocationResponseDTO toResponseDTO(
            Location location) {

        if (location == null) {
            return null;
        }

        return new LocationResponseDTO(
                location.getLocationId(),
                location.getUserId(),
                location.getLatitude(),
                location.getLongitude(),
                location.getTimestamp()
        );
    }

    // ================= KAFKA =================

    @Override
    public void createLocationFromUser(
            LocationCreatedEvent event) {

        if (event == null
                || event.getUserId() == null
                || event.getUserId().isBlank()) {

            return;
        }

        if (locationRepository.existsByUserId(
                event.getUserId())) {

            return;
        }

        Location location =
                new Location();

        location.setUserId(
                event.getUserId()
        );

        location.setTimestamp(
                LocalDateTime.now()
        );

        locationRepository.save(location);
    }
}