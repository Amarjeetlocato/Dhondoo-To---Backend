package com.whoami.launch.repository;

import com.whoami.launch.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository for DeviceToken entity
 */
@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    /**
     * Find all active device tokens for a user
     */
    List<DeviceToken> findByUserIdAndIsActiveTrue(String userId);

    /**
     * Find device token by token string
     */
    Optional<DeviceToken> findByDeviceToken(String deviceToken);

    /**
     * Find device token by user ID and token
     */
    Optional<DeviceToken> findByUserIdAndDeviceToken(String userId, String deviceToken);

    /**
     * Deactivate device token
     */
    @Modifying
    @Transactional
    @Query("UPDATE DeviceToken d SET d.isActive = false WHERE d.deviceTokenId = :tokenId")
    void deactivateToken(@Param("tokenId") Long tokenId);

    /**
     * Get all active device tokens for multiple users
     */
    @Query("SELECT d FROM DeviceToken d WHERE d.userId IN :userIds AND d.isActive = true")
    List<DeviceToken> findActiveTokensForUsers(@Param("userIds") List<String> userIds);

    /**
     * Count active tokens for a user
     */
    @Query("SELECT COUNT(d) FROM DeviceToken d WHERE d.userId = :userId AND d.isActive = true")
    Long countActiveTokens(@Param("userId") String userId);

    /**
     * Delete tokens by user ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM DeviceToken d WHERE d.userId = :userId")
    void deleteByUserId(@Param("userId") String userId);
}
