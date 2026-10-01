package com.whoami.launch.repository;

import com.whoami.launch.entity.Product;
import com.whoami.launch.enums.ProductVisibility;
import com.whoami.launch.enums.StockStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findByProductName(String productName);

    List<Product> findByProductNameContaining(String productName);

    List<Product> findByBusinessId(String businessId);

    List<Product> findByVisibility(ProductVisibility visibility);

    List<Product> findByBadges(String badges);

    List<Product> findByQuality(String quality);

    @Query("""
            SELECT p
            FROM Product p
            WHERE p.businessId IN
            (
                SELECT b.businessId
                FROM Business b
                WHERE b.latitude IS NOT NULL
                AND b.longitude IS NOT NULL
            )
            """)
    List<Product> findAllWithBusinessCoordinates();

    long countByBusinessId(String businessId);

    List<Product> findByStockStatus(StockStatus stockStatus);

    Page<Product> findByBusinessId(
            String businessId,
            Pageable pageable);

    Optional<Product> findByProductId(String productId);
}
