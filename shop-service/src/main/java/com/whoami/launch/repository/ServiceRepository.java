package com.whoami.launch.repository;

import com.whoami.launch.entity.Business;
import com.whoami.launch.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository
        extends JpaRepository<Service, Long> {

    Optional<Service> findByServiceId(String serviceId);

    List<Service> findByServiceName(String serviceName);

    List<Service> findByServiceNameContaining(String serviceName);

    List<Service> findByBusinessId(String businessId);

    List<Service> findByVisibility(String visibility);

    List<Service> findByBadges(String badges);

    @Query("""
            SELECT s
            FROM Service s
            WHERE s.businessId IN
            (
                SELECT b.businessId
                FROM Business b
                WHERE b.latitude IS NOT NULL
                AND b.longitude IS NOT NULL
            )
            """)
    List<Service> findAllFromBusinessesWithCoordinates();

    long countByBusinessId(String businessId);

    Page<Service> findAll(Pageable pageable);

    Page<Service> findByBusinessId(
            String businessId,
            Pageable pageable);
}
