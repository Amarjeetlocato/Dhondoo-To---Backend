package com.whoami.launch.kafka;

import com.locato.constants.events.location.LocationCreatedEvent;
import com.whoami.launch.entity.Location;
import com.whoami.launch.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationEventConsumer {

    private final LocationRepository locationRepository;

    @KafkaListener(
            topics = "location-events",
            groupId = "shop-group",
            containerFactory = "locationKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeLocationCreated(
            LocationCreatedEvent event) {

        log.info(
                "Received LOCATION_CREATED event. userId={}",
                event.getUserId()
        );

        if (event.getUserId() == null
                || event.getUserId().isBlank()) {

            log.warn(
                    "Ignoring location event because userId is missing"
            );

            return;
        }

        if (locationRepository.existsByUserId(event.getUserId())) {

            log.info(
                    "Location already exists for userId={}. Skipping.",
                    event.getUserId()
            );

            return;
        }

        Location location = new Location();

        location.setUserId(event.getUserId());

        // Coordinates are not part of the current
        // LocationCreatedEvent contract.
        // They will be populated later from the actual
        // user/device location flow.
        location.setLatitude(null);
        location.setLongitude(null);

        Location saved =
                locationRepository.save(location);

        log.info(
                "Location created successfully. locationId={}, userId={}",
                saved.getLocationId(),
                saved.getUserId()
        );
    }
}