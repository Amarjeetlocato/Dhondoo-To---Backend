package com.whoami.launch.service;

import com.whoami.launch.entity.DeviceToken;

import java.util.List;

public interface DeviceTokenService {

    DeviceToken registerDeviceToken(
            String userId,
            String deviceToken,
            String deviceType
    );

    List<DeviceToken> getActiveDeviceTokens(
            String userId
    );

    List<DeviceToken> getActiveDeviceTokensForUsers(
            List<String> userIds
    );

    void deactivateDeviceToken(
            String deviceTokenId
    );

    void deactivateDeviceTokenByString(
            String deviceToken
    );

    void removeDeviceToken(
            String userId,
            String deviceToken
    );

    boolean hasActiveTokens(
            String userId
    );

    void deleteAllTokensForUser(
            String userId
    );
}