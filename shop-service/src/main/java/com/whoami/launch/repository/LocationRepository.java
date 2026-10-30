package com.whoami.launch.repository;

import com.whoami.launch.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository
        extends JpaRepository<Location, Long> {

    Optional<Location> findByLocationId(String locationId);

    List<Location> findByUserIdOrderByTimestampDesc(String userId);

    List<Location> findByTimestampBetween(
            LocalDateTime startTime,
            LocalDateTime endTime);

    List<Location> findByLatitudeAndLongitude(
            Double latitude,
            Double longitude);

    boolean existsByUserId(String userId);
}
