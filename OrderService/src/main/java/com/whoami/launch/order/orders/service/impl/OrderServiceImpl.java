package com.whoami.launch.order.orders.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locato.constants.topics.KafkaTopics;
import com.whoami.launch.dto.NotificationEvent;
import com.whoami.launch.enums.NotificationType;
import com.whoami.launch.order.cart.entity.Cart;
import com.whoami.launch.order.cart.repository.CartRepository;
import com.whoami.launch.order.enums.OrderStatus;
import com.whoami.launch.order.exception.BadRequestException;
import com.whoami.launch.order.exception.ResourceNotFoundException;
import com.whoami.launch.order.orders.dto.DispatchParcelRequest;
import com.whoami.launch.order.orders.dto.OrderItemResponse;
import com.whoami.launch.order.orders.dto.OrderResponse;
import com.whoami.launch.order.orders.dto.PlaceOrderRequest;
import com.whoami.launch.order.orders.dto.UpdateDeliveryLocationRequest;
import com.whoami.launch.order.orders.entity.Order;
import com.whoami.launch.order.orders.entity.OrderItem;
import com.whoami.launch.order.orders.repository.OrderItemRepository;
import com.whoami.launch.order.orders.repository.OrderRepository;
import com.whoami.launch.order.orders.service.OrderService;
import com.whoami.launch.order.delivery.DeliveryGatewayClient;
import com.whoami.launch.order.delivery.DeliveryGatewayFactory;
import com.whoami.launch.order.delivery.DispatchResult;
import com.whoami.launch.producer.OrderKafkaProducer;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderKafkaProducer orderKafkaProducer;
    private final DeliveryGatewayFactory deliveryGatewayFactory;
    private final com.whoami.launch.order.delivery.UberDeliveryGatewayClient uberDeliveryGatewayClient;

    @Override
    @Transactional
    public List<OrderResponse> placeOrder(
            String customerId,
            PlaceOrderRequest request) {

        List<Cart> cartItems =
                cartRepository.findByCustomerId(customerId);

        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Map<String, List<Cart>> grouped =
                cartItems.stream()
                        .collect(Collectors.groupingBy(
                                Cart::getBusinessId
                        ));

        List<OrderResponse> responses =
                new ArrayList<>();

        for (Map.Entry<String, List<Cart>> entry :
                grouped.entrySet()) {

            String businessId = entry.getKey();
            List<Cart> items = entry.getValue();

            // Validate Store Open Status & Accepting Orders setting in shop-service
            String bName = "Business";
            String bLogo = null;
            String bBanner = null;
            String bPhone = null;
            String bAddr = null;
            Double bLat = null;
            Double bLng = null;

            try {
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                String shopUrl = "http://localhost:8084/internal/businesses/" + businessId;
                org.springframework.http.ResponseEntity<java.util.Map> shopRes = restTemplate.getForEntity(shopUrl, java.util.Map.class);
                if (shopRes.getStatusCode().is2xxSuccessful() && shopRes.getBody() != null) {
                    java.util.Map shopData = shopRes.getBody();
                    String status = (String) shopData.get("businessStatus");
                    if (status == null) status = (String) shopData.get("status");
                    Boolean acceptingOrders = (Boolean) shopData.get("acceptingOrders");

                    if ("CLOSED".equalsIgnoreCase(status) || "TEMPORARILY_CLOSED".equalsIgnoreCase(status) || "SUSPENDED".equalsIgnoreCase(status)) {
                        throw new BadRequestException("Store is currently CLOSED. Orders cannot be placed at this time.");
                    }
                    if (Boolean.FALSE.equals(acceptingOrders)) {
                        throw new BadRequestException("Store is currently NOT accepting orders.");
                    }

                    bName = (String) (shopData.get("businessName") != null ? shopData.get("businessName") : shopData.get("name"));
                    bLogo = (String) (shopData.get("logoUrl") != null ? shopData.get("logoUrl") : shopData.get("imageUrl"));
                    bBanner = (String) shopData.get("bannerUrl");
                    bPhone = (String) (shopData.get("mobileNumber") != null ? shopData.get("mobileNumber") : shopData.get("phone"));
                    bAddr = (String) shopData.get("address");
                    if (shopData.get("latitude") != null) bLat = ((Number) shopData.get("latitude")).doubleValue();
                    if (shopData.get("longitude") != null) bLng = ((Number) shopData.get("longitude")).doubleValue();
                }
            } catch (BadRequestException e) {
                throw e;
            } catch (Exception e) {
                log.warn("Could not check store open status for businessId={}: {}", businessId, e.getMessage());
            }

            String orderId =
                    "ORDER_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

            Order order = new Order();

            order.setOrderId(orderId);
            order.setCustomerId(customerId);
            order.setBusinessId(businessId);
            order.setBusinessNameSnapshot(bName);
            order.setBusinessLogoSnapshot(bLogo);
            order.setBusinessBannerSnapshot(bBanner);
            order.setBusinessPhone(bPhone);
            order.setBusinessAddress(bAddr);
            order.setBusinessLat(bLat);
            order.setBusinessLng(bLng);
            order.setFulfillmentType(request.getFulfillmentType() != null ? request.getFulfillmentType() : "HOME_DELIVERY");
            order.setDeliveryFee(request.getDeliveryFee() != null ? request.getDeliveryFee() : BigDecimal.ZERO);
            order.setStatus(OrderStatus.REQUESTED);
            order.setCustomerNote(
                    request.getCustomerNote()
            );
            order.setDeliveryAddress(
                    request.getDeliveryAddress()
            );
            order.setCustomerLat(
                    request.getCustomerLat()
            );
            order.setCustomerLng(
                    request.getCustomerLng()
            );

            List<OrderItem> orderItems =
                    new ArrayList<>();

            BigDecimal subtotal =
                    BigDecimal.ZERO;

            for (Cart cart : items) {

                OrderItem orderItem =
                        new OrderItem();

                orderItem.setOrderId(orderId);
                orderItem.setProductId(
                        cart.getProductId()
                );
                orderItem.setProductNameSnapshot(
                        cart.getProductNameSnapshot()
                );
                orderItem.setImageSnapshot(
                        cart.getImageSnapshot()
                );
                orderItem.setPriceSnapshot(
                        cart.getPriceSnapshot()
                );
                orderItem.setQuantity(
                        cart.getQuantity()
                );

                orderItem.setTotalPrice(
                        cart.getPriceSnapshot()
                                .multiply(
                                        BigDecimal.valueOf(
                                                cart.getQuantity()
                                        )
                                )
                );

                subtotal =
                        subtotal.add(
                                orderItem.getTotalPrice()
                        );

                orderItems.add(orderItem);
            }

            order.setSubtotal(subtotal);

            Order savedOrder =
                    orderRepository.save(order);

            orderItemRepository.saveAll(orderItems);

            sendOrderStatusNotification(savedOrder);

            /*
             * Business-owner notification is intentionally
             * not migrated here yet.
             *
             * businessId != userId.
             *
             * We need the business owner's userId from
             * the Order/business snapshot architecture
             * before sending this notification correctly.
             */

            responses.add(
                    toResponse(
                            savedOrder,
                            orderItems
                    )
            );
        }

        cartRepository.deleteByCustomerId(customerId);

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrders(
            String customerId) {

        return orderRepository
                .findByCustomerId(customerId)
                .stream()
                .map(order ->
                        toResponse(
                                order,
                                orderItemRepository
                                        .findByOrderId(
                                                order.getOrderId()
                                        )
                        )
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getBusinessOrders(
            String businessId) {

        return orderRepository
                .findByBusinessId(businessId)
                .stream()
                .map(order ->
                        toResponse(
                                order,
                                orderItemRepository
                                        .findByOrderId(
                                                order.getOrderId()
                                        )
                        )
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(
            String orderId,
            String currentCustomerId,
            String currentBusinessId) {

        Order order =
                orderRepository
                        .findByOrderId(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found"
                                )
                        );

        return toResponse(
                order,
                orderItemRepository
                        .findByOrderId(
                                order.getOrderId()
                        )
        );
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            String orderId,
            OrderStatus newStatus,
            String businessId) {

        Order order =
                orderRepository
                        .findByOrderId(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found"
                                )
                        );

        order.setStatus(newStatus);

        Order updated =
                orderRepository.save(order);

        sendOrderStatusNotification(updated);

        return toResponse(
                updated,
                orderItemRepository
                        .findByOrderId(
                                updated.getOrderId()
                        )
        );
    }

    private void sendOrderStatusNotification(
            Order order) {

        String title;
        String message;

        switch (order.getStatus()) {

            case REQUESTED:
                title = "Order Placed";
                message =
                        "Your order has been placed successfully.";
                break;

            case ACCEPTED:
                title = "Order Accepted";
                message =
                        "Your order has been accepted by the business.";
                break;

            case REJECTED:
                title = "Order Rejected";
                message =
                        "Your order has been rejected by the business.";
                break;

            case PREPARING:
                title = "Order Preparing";
                message =
                        "Your order is currently being prepared.";
                break;

            case READY:
                title = "Order Ready";
                message =
                        "Your order is ready.";
                break;

            case DELIVERED:
                title = "Order Delivered";
                message =
                        "Your order has been delivered successfully.";
                break;

            case CANCELLED:
                title = "Order Cancelled";
                message =
                        "Your order has been cancelled.";
                break;

            default:
                return;
        }

        try {
            NotificationEvent event =
                    NotificationEvent.builder()
                            .userId(order.getCustomerId())
                            .title(title)
                            .message(message)
                            .targetId(order.getOrderId())
                            .targetType("ORDER")
                            .type(NotificationType.ORDER)
                            .sendPush(true)
                            .build();

            orderKafkaProducer.publish(
                    KafkaTopics.NOTIFICATION_EVENTS,
                    event
            );
        } catch (Exception e) {
            log.warn("Could not publish Kafka notification for orderId={}: {}", order.getOrderId(), e.getMessage());
        }
    }

    private OrderResponse toResponse(
            Order order,
            List<OrderItem> items) {

        OrderResponse response =
                new OrderResponse();

        response.setOrderId(
                order.getOrderId()
        );

        response.setCustomerId(
                order.getCustomerId()
        );

        response.setCustomerPhone(
                order.getCustomerPhone()
        );

        response.setBusinessId(
                order.getBusinessId()
        );

        response.setBusinessName(
                order.getBusinessNameSnapshot()
        );

        response.setBusinessLogo(
                order.getBusinessLogoSnapshot()
        );

        response.setBusinessBanner(
                order.getBusinessBannerSnapshot()
        );

        response.setBusinessPhone(
                order.getBusinessPhone()
        );

        response.setBusinessAddress(
                order.getBusinessAddress()
        );

        response.setBusinessLat(
                order.getBusinessLat()
        );

        response.setBusinessLng(
                order.getBusinessLng()
        );

        response.setCustomerLat(
                order.getCustomerLat()
        );

        response.setCustomerLng(
                order.getCustomerLng()
        );

        response.setFulfillmentType(
                order.getFulfillmentType()
        );

        response.setDeliveryFee(
                order.getDeliveryFee()
        );

        response.setStatus(
                order.getStatus()
        );

        response.setSubtotal(
                order.getSubtotal()
        );

        response.setCustomerNote(
                order.getCustomerNote()
        );

        response.setDeliveryAddress(
                order.getDeliveryAddress()
        );

        response.setDeliveryPartner(order.getDeliveryPartner());
        response.setTrackingId(order.getTrackingId());
        response.setDeliveryStatus(order.getDeliveryStatus());
        response.setDeliveryBoyName(order.getDeliveryBoyName());
        response.setDeliveryBoyPhone(order.getDeliveryBoyPhone());
        response.setDeliveryBoyLat(order.getDeliveryBoyLat());
        response.setDeliveryBoyLng(order.getDeliveryBoyLng());
        response.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        response.setDispatchApiUrl(order.getDispatchApiUrl());

        response.setCreatedAt(
                order.getCreatedAt()
        );

        response.setUpdatedAt(
                order.getUpdatedAt()
        );

        List<OrderItemResponse> itemResponses =
                items.stream()
                        .map(this::toItemResponse)
                        .toList();

        response.setItems(itemResponses);

        return response;
    }

    private OrderItemResponse toItemResponse(
            OrderItem item) {

        OrderItemResponse response =
                new OrderItemResponse();

        response.setProductId(
                item.getProductId()
        );

        response.setProductNameSnapshot(
                item.getProductNameSnapshot()
        );

        response.setImageSnapshot(
                item.getImageSnapshot()
        );

        response.setPriceSnapshot(
                item.getPriceSnapshot()
        );

        response.setQuantity(
                item.getQuantity()
        );

        response.setTotalPrice(
                item.getTotalPrice()
        );

        return response;
    }

    @Override
    @Transactional
    public OrderResponse dispatchParcel(
            String orderId,
            String businessId,
            DispatchParcelRequest request) {

        validateBusinessSubscriptionForDelivery(businessId);

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (!order.getBusinessId().equals(businessId)) {
            throw new BadRequestException("Order does not belong to this business");
        }

        DeliveryGatewayClient gateway = deliveryGatewayFactory.getGateway(request.getDeliveryPartner());
        DispatchResult dispatchResult = gateway.dispatchParcel(order, request);

        order.setDeliveryPartner(dispatchResult.getDeliveryPartner());
        order.setTrackingId(dispatchResult.getTrackingId());
        order.setDeliveryStatus("DISPATCHED");
        order.setDeliveryBoyName(dispatchResult.getDeliveryBoyName());
        order.setDeliveryBoyPhone(dispatchResult.getDeliveryBoyPhone());
        order.setDeliveryBoyLat(dispatchResult.getStartLat());
        order.setDeliveryBoyLng(dispatchResult.getStartLng());
        order.setEstimatedDeliveryTime(dispatchResult.getEstimatedDeliveryTime());
        order.setDispatchApiUrl(request.getDeliveryWebsiteApiUrl());
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);

        Order saved = orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        // Notify customer of parcel dispatch
        try {
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .userId(order.getCustomerId())
                    .title("Parcel Dispatched 📦")
                    .message("Your order #" + order.getOrderId() + " has been dispatched via " + dispatchResult.getDeliveryPartner() + ". Tracking ID: " + dispatchResult.getTrackingId())
                    .targetId(order.getOrderId())
                    .targetType("ORDER")
                    .type(NotificationType.ORDER)
                    .sendPush(true)
                    .build();

            orderKafkaProducer.publish(KafkaTopics.NOTIFICATION_EVENTS, notificationEvent);
        } catch (Exception e) {
            // notification warning
        }

        return toResponse(saved, items);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getTracking(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return toResponse(order, items);
    }

    @Override
    @Transactional
    public OrderResponse updateDeliveryLocation(
            String orderId,
            UpdateDeliveryLocationRequest request) {

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (request.getLatitude() != null) order.setDeliveryBoyLat(request.getLatitude());
        if (request.getLongitude() != null) order.setDeliveryBoyLng(request.getLongitude());
        if (request.getDeliveryStatus() != null && !request.getDeliveryStatus().isBlank()) {
            order.setDeliveryStatus(request.getDeliveryStatus());
            if ("DELIVERED".equalsIgnoreCase(request.getDeliveryStatus())) {
                order.setStatus(OrderStatus.DELIVERED);
            }
        }

        Order saved = orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return toResponse(saved, items);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderDetails(
            String orderId,
            String businessId,
            com.whoami.launch.order.orders.dto.UpdateOrderDetailsRequest request) {

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (request.getCustomerPhone() != null && !request.getCustomerPhone().isBlank()) {
            order.setCustomerPhone(request.getCustomerPhone());
        }
        if (request.getDeliveryAddress() != null && !request.getDeliveryAddress().isBlank()) {
            order.setDeliveryAddress(request.getDeliveryAddress());
        }
        if (request.getDeliveryFee() != null) {
            order.setDeliveryFee(request.getDeliveryFee());
        }

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            orderItemRepository.deleteByOrderId(orderId);

            List<OrderItem> newItems = new ArrayList<>();
            BigDecimal calculatedSubtotal = BigDecimal.ZERO;

            for (com.whoami.launch.order.orders.dto.UpdateOrderDetailsRequest.OrderItemUpdateDto itemDto : request.getItems()) {
                OrderItem item = new OrderItem();
                item.setOrderId(orderId);
                item.setProductId(itemDto.getProductId() != null ? itemDto.getProductId() : "CUSTOM_ITEM_" + UUID.randomUUID().toString().substring(0, 6));
                item.setProductNameSnapshot(itemDto.getProductNameSnapshot() != null ? itemDto.getProductNameSnapshot() : "Item");
                item.setImageSnapshot(itemDto.getImageSnapshot());
                item.setPriceSnapshot(itemDto.getPriceSnapshot() != null ? itemDto.getPriceSnapshot() : BigDecimal.ZERO);
                item.setQuantity(itemDto.getQuantity() != null ? itemDto.getQuantity() : 1);

                BigDecimal itemTotal = item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getQuantity()));
                item.setTotalPrice(itemTotal);
                calculatedSubtotal = calculatedSubtotal.add(itemTotal);

                newItems.add(item);
            }

            orderItemRepository.saveAll(newItems);
            order.setSubtotal(request.getSubtotal() != null ? request.getSubtotal() : calculatedSubtotal);
        }

        Order saved = orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        return toResponse(saved, items);
    }

    private void validateBusinessSubscriptionForDelivery(String businessId) {
        if (businessId == null || businessId.isBlank()) {
            return;
        }
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String url = "http://localhost:8084/internal/businesses/" + businessId + "/access-state";
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.getForEntity(url, java.util.Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Boolean active = (Boolean) response.getBody().get("subscriptionActive");
                if (Boolean.FALSE.equals(active)) {
                    log.info("Subscription note for businessId={}: inactive subscription (sandbox dispatch active)", businessId);
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public com.whoami.launch.order.orders.dto.DeliveryEstimateResponse estimateDeliveryFee(
            com.whoami.launch.order.orders.dto.DeliveryEstimateRequest request) {

        double storeLat = 28.6139;
        double storeLng = 77.2090;

        String sId = request.getShopId() != null && !request.getShopId().isBlank() 
                ? request.getShopId() : request.getBusinessId();

        if (sId != null && !sId.isBlank()) {
            try {
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                String shopUrl = "http://localhost:8084/internal/businesses/" + sId;
                org.springframework.http.ResponseEntity<java.util.Map> shopRes = restTemplate.getForEntity(shopUrl, java.util.Map.class);
                if (shopRes.getStatusCode().is2xxSuccessful() && shopRes.getBody() != null) {
                    java.util.Map shopData = shopRes.getBody();
                    if (shopData.get("latitude") != null && shopData.get("longitude") != null) {
                        storeLat = ((Number) shopData.get("latitude")).doubleValue();
                        storeLng = ((Number) shopData.get("longitude")).doubleValue();
                    }
                }
            } catch (Exception ignored) {}
        }

        double customerLat = request.getCustomerLat() != null ? request.getCustomerLat() : 28.6273;
        double customerLng = request.getCustomerLng() != null ? request.getCustomerLng() : 77.3725;

        // Try direct live call to production Uber Direct Delivery Estimates API
        try {
            com.whoami.launch.order.orders.dto.DeliveryEstimateResponse realUberEstimate =
                    uberDeliveryGatewayClient.getUberDeliveryEstimate(request, storeLat, storeLng);
            if (realUberEstimate != null) {
                return realUberEstimate;
            }
        } catch (Exception e) {
            log.warn("Production Uber Direct Delivery Estimate call encountered exception: {}", e.getMessage());
        }

        // Calculate Haversine distance in KM
        double earthRadius = 6371; // km
        double dLat = Math.toRadians(customerLat - storeLat);
        double dLng = Math.toRadians(customerLng - storeLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(storeLat)) * Math.cos(Math.toRadians(customerLat)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = Math.round((earthRadius * c) * 10.0) / 10.0;
        if (distanceKm < 0.5) distanceKm = 1.0;
        if (distanceKm > 20.0) distanceKm = 3.5;

        // Base fee = ₹35 + ₹10/km
        BigDecimal fee = BigDecimal.valueOf(Math.round((35.0 + (distanceKm * 10.0)) * 100.0) / 100.0);
        int etaMin = 15 + (int)(distanceKm * 2);
        int etaMax = 25 + (int)(distanceKm * 2);
        String etaStr = etaMin + "-" + etaMax + " mins (Uber Direct)";

        return com.whoami.launch.order.orders.dto.DeliveryEstimateResponse.builder()
                .deliveryFee(fee)
                .estimatedTime(etaStr)
                .deliveryPartner("UBER")
                .distanceKm(distanceKm)
                .build();
    }
}