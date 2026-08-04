package com.whoami.launch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.LocationResponseDTO;
import com.whoami.launch.entity.Location;
import com.whoami.launch.dto.LocationCreatedEvent;
import com.whoami.launch.repository.LocationRepository;

@Service
public class LocationService {
    
    @Autowired
    private LocationRepository locationRepository;
    
    public Location createLocation(Location location) {

        if (locationRepository.existsByUserId(location.getUserId())) {
            throw new RuntimeException(
                    "Location already exists for userId: " + location.getUserId()
            );
        }

        location.setTimestamp(LocalDateTime.now());

        return locationRepository.save(location);
    } 
   
    public Location updateLocation(String userId, Location locationDetails) {
    	Optional<Location> location = locationRepository.findByUserId(userId);
    	if (location.isPresent()) {
            Location existingLocation = location.get();
            if (locationDetails.getLatitude() != null) {
                existingLocation.setLatitude(locationDetails.getLatitude());
            }
            if (locationDetails.getLongitude() != null) {
                existingLocation.setLongitude(locationDetails.getLongitude());
            }
            if (locationDetails.getTimestamp() != null) {
                existingLocation.setTimestamp(locationDetails.getTimestamp());
            }
            existingLocation.setTimestamp(LocalDateTime.now());
            return locationRepository.save(existingLocation);
        }
        return null;
    }
    
    public Optional<Location> getLocationById(String userId) {
        return locationRepository.findById(userId);
    }
    
    public Optional<Location> getLocationsByUserId(String userId) {
        return locationRepository.findByUserId(userId);
    }
    
    public List<Location> getLocationsByUserIdOrderedByTime(String userId) {
        return locationRepository.findByUserIdOrderByTimestampDesc(userId);
    }
    
    public List<Location> getLocationsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return locationRepository.findByTimestampBetween(startTime, endTime);
    }
    
    public List<Location> getLocationsByCoordinates(Double latitude, Double longitude) {
        return locationRepository.findByLatitudeAndLongitude(latitude, longitude);
    }
    
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }
    
    public void deleteLocation(String locationId) {
        locationRepository.deleteById(locationId);
    }
    
    public LocationResponseDTO toResponseDTO(Location location) {

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

    public void createLocationFromUser(LocationCreatedEvent event) {

        if (locationRepository.existsByUserId(event.getUserId())) {
            return;
        }

        Location location = new Location();

        location.setUserId(event.getUserId());

        // latitude and longitude will be null initially
        location.setTimestamp(LocalDateTime.now());

        locationRepository.save(location);
    }

	         
}