package com.whoami.launch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entity.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, String> {
    Optional<Shop> findByShopName(String shopName);
    List<Shop> findByUserId(String userId);
    List<Shop> findByShopNameContaining(String shopName);
    boolean existsByUserId(String userId);
    
    @Query("SELECT s FROM Shop s WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL")
    List<Shop> findAllWithCoordinates();
    Optional<Shop> findByMobileNumber(
            String mobileNumber
    );
    List<Shop> findByPincode(String pincode);

    List<Shop> findByVillageContainingIgnoreCase(String village);

    List<Shop> findByDistrictContainingIgnoreCase(String district);

    List<Shop> findByStateContainingIgnoreCase(String state);
	Page<Shop> findAll(Pageable pageable);
	Optional<Shop> findBySlug(String slug);

    boolean existsBySlug(String slug);


}
