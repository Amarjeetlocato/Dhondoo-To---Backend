package com.whoami.launch.service;

import com.whoami.launch.entity.DeviceToken;
import com.whoami.launch.repository.DeviceTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    public DeviceTokenService(DeviceTokenRepository deviceTokenRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
    }

    /**
     * Register or update device token
     */
    @Transactional
    public DeviceToken registerDeviceToken(
            String userId,
            String deviceToken,
            String deviceType) {

        log.info("Registering device token for user: {}", userId);

        try {

            // Check existing token
            var existingToken =
                    deviceTokenRepository.findByDeviceToken(deviceToken);

            if (existingToken.isPresent()) {

                log.info("Device token already exists, updating user association");

                DeviceToken token = existingToken.get();

                token.setUserId(userId);
                token.setDeviceType(deviceType);
                token.setIsActive(true);

                return deviceTokenRepository.save(token);
            }

            DeviceToken token = DeviceToken.builder()
                    .userId(userId)
                    .deviceToken(deviceToken)
                    .deviceType(deviceType)
                    .isActive(true)
                    .build();

            return deviceTokenRepository.save(token);

        } catch (DataIntegrityViolationException ex) {

            /*
             * Another request inserted same token
             * before current transaction committed.
             */

            log.warn(
                    "Concurrent registration detected for token. Fetching existing record."
            );

            return deviceTokenRepository
                    .findByDeviceToken(deviceToken)
                    .map(existing -> {

                        existing.setUserId(userId);
                        existing.setDeviceType(deviceType);
                        existing.setIsActive(true);

                        return deviceTokenRepository.save(existing);

                    })
                    .orElseThrow(() -> ex);
        }
    }

    /**
     * Get all active device tokens for a user
     */
    public List<DeviceToken> getActiveDeviceTokens(String userId) {
        return deviceTokenRepository.findByUserIdAndIsActiveTrue(userId);
    }

    /**
     * Get active device tokens for multiple users
     */
    public List<DeviceToken> getActiveDeviceTokensForUsers(List<String> userIds) {
        return deviceTokenRepository.findActiveTokensForUsers(userIds);
    }

    /**
     * Deactivate a device token
     */
    @Transactional
    public void deactivateDeviceToken(Long tokenId) {
        log.info("Deactivating device token: {}", tokenId);
        deviceTokenRepository.deactivateToken(tokenId);
    }

    /**
     * Deactivate device token by token string
     */
    @Transactional
    public void deactivateDeviceTokenByString(String deviceToken) {

        log.info("Deactivating device token: {}", deviceToken);

        deviceTokenRepository.findByDeviceToken(deviceToken)
                .ifPresent(token ->
                        deviceTokenRepository.deactivateToken(
                                token.getDeviceTokenId()));
    }

    /**
     * Remove device token
     */
    @Transactional
    public void removeDeviceToken(
            String userId,
            String deviceToken) {

        log.info("Removing device token for user: {}", userId);

        deviceTokenRepository
                .findByUserIdAndDeviceToken(userId, deviceToken)
                .ifPresent(deviceTokenRepository::delete);
    }

    /**
     * Check if user has active tokens
     */
    public boolean hasActiveTokens(String userId) {
        return deviceTokenRepository.countActiveTokens(userId) > 0;
    }

    /**
     * Delete all user tokens
     */
    @Transactional
    public void deleteAllTokensForUser(String userId) {

        log.info("Deleting all device tokens for user: {}", userId);

        deviceTokenRepository.deleteByUserId(userId);
    }
}