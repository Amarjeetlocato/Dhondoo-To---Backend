package com.whoami.launch.repository;

import com.whoami.launch.entity.Reel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReelRepository extends JpaRepository<Reel, String> {
    List<Reel> findByShopId(String shopId);
    List<Reel> findByReelDescriptionContaining(String description);
    
    @Query("SELECT r FROM Reel r WHERE r.shopId IN " +
           "(SELECT s.shopId FROM Shop s WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL)")
    List<Reel> findAllFromShopsWithCoordinates();
    long countByShopId(String string);
    
    Page<Reel> findByShopId(String shopId, Pageable pageable);
}
