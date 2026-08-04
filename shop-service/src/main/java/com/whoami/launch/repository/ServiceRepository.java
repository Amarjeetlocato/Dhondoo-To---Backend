package com.whoami.launch.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entity.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {
    List<Service> findByServiceName(String serviceName);
    List<Service> findByServiceNameContaining(String serviceName);
    List<Service> findByShopId(String shopId);
    List<Service> findByVisibility(String visibility);
    List<Service> findByBadges(String badges);
    
    @Query("SELECT s FROM Service s WHERE s.shopId IN " +
           "(SELECT sh.shopId FROM Shop sh WHERE sh.latitude IS NOT NULL AND sh.longitude IS NOT NULL)")
    List<Service> findAllFromShopsWithCoordinates();
    long countByShopId(String string);
    
    Page<Service> findAll(Pageable pageable);
    
    Page<Service> findByShopId(String shopId, Pageable pageable);

}
