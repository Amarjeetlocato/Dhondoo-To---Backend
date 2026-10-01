package com.whoami.launch.repository;

import com.whoami.launch.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LocationRepository
        extends JpaRepository<Location, Long> {

    List<Location> findByUserIdOrderByTimestampDesc(String userId);

    List<Location> findByTimestampBetween(
            LocalDateTime startTime,
            LocalDateTime endTime);

    List<Location> findByLatitudeAndLongitude(
            Double latitude,
            Double longitude);

    boolean existsByUserId(String userId);
}