package com.whoami.launch.repository;

import com.whoami.launch.entity.Reel;
import com.whoami.launch.entity.Report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReelRepository
        extends JpaRepository<Reel, Long> {

    List<Reel> findByBusinessId(String businessId);

    List<Reel> findByReelDescriptionContaining(String description);

    @Query("""
            SELECT r
            FROM Reel r
            WHERE r.businessId IN
            (
                SELECT b.businessId
                FROM Business b
                WHERE b.latitude IS NOT NULL
                AND b.longitude IS NOT NULL
            )
            """)
    List<Reel> findAllFromBusinessesWithCoordinates();

    long countByBusinessId(String businessId);

    Page<Reel> findByBusinessId(
            String businessId,
            Pageable pageable);

	Optional<Reel> findByReelId(String targetId);
}