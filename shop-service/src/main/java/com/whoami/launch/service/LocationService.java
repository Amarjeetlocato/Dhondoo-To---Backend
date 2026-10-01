package com.whoami.launch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.locato.constants.events.location.LocationCreatedEvent;
import com.whoami.launch.dto.LocationResponseDTO;
import com.whoami.launch.entity.Location;

public interface LocationService {

    Location createLocation(
            Location location);

    Location updateLocation(
            String locationId,
            Location locationDetails);

    Optional<Location> getLocationById(
            String locationId);

    List<Location> getLocationsByUserId(
            String userId);

    List<Location> getLocationsByUserIdOrderedByTime(
            String userId);

    List<Location> getLocationsByTimeRange(
            LocalDateTime startTime,
            LocalDateTime endTime);

    List<Location> getLocationsByCoordinates(
            Double latitude,
            Double longitude);

    List<Location> getAllLocations();

    void deleteLocation(
            String locationId);

    LocationResponseDTO toResponseDTO(
            Location location);

    void createLocationFromUser(
            LocationCreatedEvent event);
}