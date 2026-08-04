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

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
	List<Product> findByProductName(String productName);

	List<Product> findByProductNameContaining(String productName);

	List<Product> findByShopId(String shopId);

	List<Product> findByVisibility(ProductVisibility visibility);

	List<Product> findByBadges(String badges);

	List<Product> findByQuality(String quality);

	@Query("SELECT p FROM Product p WHERE p.shopId IN "
			+ "(SELECT s.shopId FROM Shop s WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL)")
	List<Product> findAllFromShopsWithCoordinates();

	long countByShopId(String string);

	List<Product> findByStockStatus(StockStatus stockStatus);
	
	Page<Product> findByShopId(String shopId, Pageable pageable);

}
