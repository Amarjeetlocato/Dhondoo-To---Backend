package com.whoami.launch.service.impl;

import com.whoami.launch.entity.DeviceToken;
import com.whoami.launch.repository.DeviceTokenRepository;
import com.whoami.launch.service.DeviceTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DeviceTokenServiceImpl
        implements DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    public DeviceTokenServiceImpl(
            DeviceTokenRepository deviceTokenRepository) {

        this.deviceTokenRepository =
                deviceTokenRepository;
    }

    @Override
    @Transactional
    public DeviceToken registerDeviceToken(
            String userId,
            String deviceToken,
            String deviceType) {

        log.info(
                "Registering device token for user: {}",
                userId
        );

        try {

            var existingToken =
                    deviceTokenRepository
                            .findByDeviceToken(deviceToken);

            if (existingToken.isPresent()) {

                log.info(
                        "Device token already exists, " +
                        "updating user association"
                );

                DeviceToken token =
                        existingToken.get();

                token.setUserId(userId);
                token.setDeviceType(deviceType);
                token.setIsActive(true);

                return deviceTokenRepository.save(token);
            }

            DeviceToken token =
                    DeviceToken.builder()
                            .userId(userId)
                            .deviceToken(deviceToken)
                            .deviceType(deviceType)
                            .isActive(true)
                            .build();

            return deviceTokenRepository.save(token);

        } catch (DataIntegrityViolationException ex) {

            log.warn(
                    "Concurrent registration detected " +
                    "for token. Fetching existing record."
            );

            return deviceTokenRepository
                    .findByDeviceToken(deviceToken)
                    .map(existing -> {

                        existing.setUserId(userId);
                        existing.setDeviceType(deviceType);
                        existing.setIsActive(true);

                        return deviceTokenRepository
                                .save(existing);

                    })
                    .orElseThrow(() -> ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceToken> getActiveDeviceTokens(
            String userId) {

        return deviceTokenRepository
                .findByUserIdAndIsActiveTrue(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceToken> getActiveDeviceTokensForUsers(
            List<String> userIds) {

        return deviceTokenRepository
                .findActiveTokensForUsers(userIds);
    }

    @Override
    @Transactional
    public void deactivateDeviceToken(
            String deviceTokenId) {

        log.info(
                "Deactivating device token: {}",
                deviceTokenId
        );

        deviceTokenRepository
                .deactivateToken(deviceTokenId);
    }

    @Override
    @Transactional
    public void deactivateDeviceTokenByString(
            String deviceToken) {

        log.info(
                "Deactivating device token: {}",
                deviceToken
        );

        deviceTokenRepository
                .findByDeviceToken(deviceToken)
                .ifPresent(token ->
                        deviceTokenRepository.deactivateToken(
                                token.getDeviceTokenId()
                        )
                );
    }

    @Override
    @Transactional
    public void removeDeviceToken(
            String userId,
            String deviceToken) {

        log.info(
                "Removing device token for user: {}",
                userId
        );

        deviceTokenRepository
                .findByUserIdAndDeviceToken(
                        userId,
                        deviceToken
                )
                .ifPresent(
                        deviceTokenRepository::delete
                );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveTokens(
            String userId) {

        return deviceTokenRepository
                .countActiveTokens(userId) > 0;
    }

    @Override
    @Transactional
    public void deleteAllTokensForUser(
            String userId) {

        log.info(
                "Deleting all device tokens for user: {}",
                userId
        );

        deviceTokenRepository
                .deleteByUserId(userId);
    }
}