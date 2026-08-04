package com.whoami.launch.feign;

import com.whoami.launch.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for Order Service integration
 * Example of how other services can communicate with Notification Service
 */
@FeignClient(name = "order-service", url = "${feign.order-service.url:http://localhost:8081}", fallback = OrderServiceFallback.class)
public interface OrderServiceFeignClient {

    /**
     * Notify order service about a notification event
     */
    @PostMapping("/internal-api/orders/notify")
    void notifyOrderEvent(@RequestBody NotificationRequest request);
}
