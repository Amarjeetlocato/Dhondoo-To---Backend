package com.whoami.launch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.launch.entity.ShopFollower;

public interface ShopFollowerRepository
        extends JpaRepository<ShopFollower, String> {

    Optional<ShopFollower> findByShopIdAndUserId(
            String shopId,
            String userId);

    List<ShopFollower> findByShopId(String shopId);

    List<ShopFollower> findByUserId(String userId);

    Long countByShopId(String shopId);

    void deleteByShopIdAndUserId(
            String shopId,
            String userId);
}