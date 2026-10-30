package com.whoami.launch.order.delivery;

import com.whoami.launch.order.orders.dto.DispatchParcelRequest;
import com.whoami.launch.order.orders.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UberDeliveryGatewayClientTest {

    private UberDeliveryGatewayClient uberDeliveryGatewayClient;

    @BeforeEach
    public void setUp() {
        uberDeliveryGatewayClient = new UberDeliveryGatewayClient();
    }

    @Test
    public void testGetPartnerName() {
        assertEquals("UBER", uberDeliveryGatewayClient.getPartnerName());
    }

    @Test
    public void testDispatchParcel() {
        Order order = new Order();
        order.setOrderId("ORDER_TEST_123");
        order.setCustomerId("CUST_456");
        order.setDeliveryAddress("123 Main Street, City");

        DispatchParcelRequest request = DispatchParcelRequest.builder()
                .deliveryPartner("UBER")
                .deliveryBoyName("Uber Rider")
                .deliveryBoyPhone("+1-800-593-8237")
                .startLat(28.6139)
                .startLng(77.2090)
                .estimatedDeliveryTime("15-20 mins")
                .build();

        DispatchResult result = uberDeliveryGatewayClient.dispatchParcel(order, request);

        assertNotNull(result);
        assertEquals("UBER", result.getDeliveryPartner());
        assertNotNull(result.getTrackingId());
        assertTrue(result.getTrackingId().startsWith("UBER_DEL_"));
        assertNotNull(result.getTrackingUrl());
        assertEquals("DISPATCHED", result.getStatus());
        assertEquals("Uber Rider", result.getDeliveryBoyName());
    }

    @Test
    public void testTrackParcel() {
        TrackingResult tracking = uberDeliveryGatewayClient.trackParcel("UBER_DEL_12345");

        assertNotNull(tracking);
        assertEquals("UBER_DEL_12345", tracking.getTrackingId());
        assertEquals("OUT_FOR_DELIVERY", tracking.getDeliveryStatus());
        assertNotNull(tracking.getDeliveryBoyName());
    }
}
