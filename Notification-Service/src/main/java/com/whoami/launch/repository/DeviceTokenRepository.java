package com.whoami.launch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.whoami.launch.entity.DeviceToken;

/**
 * Repository for DeviceToken entity
 */
@Repository
public interface DeviceTokenRepository
        extends JpaRepository<DeviceToken, Long> {

    List<DeviceToken> findByUserIdAndIsActiveTrue(String userId);

    Optional<DeviceToken> findByDeviceToken(String deviceToken);

    Optional<DeviceToken> findByUserIdAndDeviceToken(
            String userId,
            String deviceToken
    );

    @Modifying
    @Transactional
    @Query("""
        UPDATE DeviceToken d
        SET d.isActive = false
        WHERE d.deviceTokenId = :deviceTokenId
    """)
    void deactivateToken(
            @Param("deviceTokenId") String deviceTokenId
    );

    @Query("""
        SELECT d
        FROM DeviceToken d
        WHERE d.userId IN :userIds
        AND d.isActive = true
    """)
    List<DeviceToken> findActiveTokensForUsers(
            @Param("userIds") List<String> userIds
    );

    @Query("""
        SELECT COUNT(d)
        FROM DeviceToken d
        WHERE d.userId = :userId
        AND d.isActive = true
    """)
    Long countActiveTokens(
            @Param("userId") String userId
    );

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM DeviceToken d
        WHERE d.userId = :userId
    """)
    void deleteByUserId(
            @Param("userId") String userId
    );
}