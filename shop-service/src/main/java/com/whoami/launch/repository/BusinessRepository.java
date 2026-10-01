package com.whoami.launch.repository;

import com.whoami.launch.entity.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessRepository
        extends JpaRepository<Business, Long> {

    Optional<Business> findByBusinessName(String businessName);

    List<Business> findByUserId(String userId);

    List<Business> findByBusinessNameContaining(String businessName);

    boolean existsByUserId(String userId);

    @Query("""
            SELECT b
            FROM Business b
            WHERE b.latitude IS NOT NULL
            AND b.longitude IS NOT NULL
            """)
    List<Business> findAllWithCoordinates();

    Optional<Business> findByMobileNumber(String mobileNumber);

    List<Business> findByPincode(String pincode);

    List<Business> findByVillageContainingIgnoreCase(String village);

    List<Business> findByDistrictContainingIgnoreCase(String district);

    List<Business> findByStateContainingIgnoreCase(String state);

    Page<Business> findAll(Pageable pageable);

    Optional<Business> findBySlug(String slug);

    boolean existsBySlug(String slug);

	Optional<Business> findByBusinessId(String businessId);
}