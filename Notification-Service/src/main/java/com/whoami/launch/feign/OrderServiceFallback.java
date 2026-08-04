package com.whoami.launch.feign;

import com.whoami.launch.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for OrderServiceFeignClient
 * Handles failures in inter-service communication
 */
@Slf4j
@Component
public class OrderServiceFallback implements OrderServiceFeignClient {

    @Override
    public void notifyOrderEvent(NotificationRequest request) {
        log.warn("Order Service is unavailable. Notification request failed to send: {}", request);
        // Implement fallback logic - e.g., queue the message, log error, etc.
    }
}
