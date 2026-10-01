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

    @Override
    public Location createLocation(
            Location location) {

        location.setTimestamp(
                LocalDateTime.now()
        );

        return locationRepository.save(location);
    }

    @Override
    public Location updateLocation(
            String locationId,
            Location locationDetails) {

        /*
         * locationId is the public LOCATION_... ID.
         *
         * Current repository uses Long as the internal JPA ID
         * and does not yet expose findByLocationId().
         *
         * Therefore this lookup must be completed at the
         * repository layer before using this method.
         */
        throw new UnsupportedOperationException(
                "Location lookup by locationId requires "
                        + "findByLocationId(String) in LocationRepository"
        );
    }

    @Override
    public Optional<Location> getLocationById(
            String locationId) {

        throw new UnsupportedOperationException(
                "Location lookup by locationId requires "
                        + "findByLocationId(String) in LocationRepository"
        );
    }

    @Override
    public List<Location> getLocationsByUserId(
            String userId) {

        return locationRepository
                .findByUserIdOrderByTimestampDesc(userId);
    }

    @Override
    public List<Location> getLocationsByUserIdOrderedByTime(
            String userId) {

        return locationRepository
                .findByUserIdOrderByTimestampDesc(userId);
    }

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

    @Override
    public List<Location> getAllLocations() {

        return locationRepository.findAll();
    }

    @Override
    public void deleteLocation(
            String locationId) {

        throw new UnsupportedOperationException(
                "Location deletion by locationId requires "
                        + "findByLocationId(String) in LocationRepository"
        );
    }

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

    @Override
    public void createLocationFromUser(
            LocationCreatedEvent event) {

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