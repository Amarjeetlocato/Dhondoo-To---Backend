package com.whoami.launch.order.orders.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.whoami.launch.dto.NotificationEvent;
import com.whoami.launch.enums.NotificationType;
import com.whoami.launch.order.cart.entity.Cart;
import com.whoami.launch.order.cart.repository.CartRepository;
import com.whoami.launch.order.enums.OrderStatus;
import com.whoami.launch.order.exception.BadRequestException;
import com.whoami.launch.order.exception.ResourceNotFoundException;
import com.whoami.launch.order.orders.dto.OrderItemResponse;
import com.whoami.launch.order.orders.dto.OrderResponse;
import com.whoami.launch.order.orders.dto.PlaceOrderRequest;
import com.whoami.launch.order.orders.entity.Order;
import com.whoami.launch.order.orders.entity.OrderItem;
import com.whoami.launch.order.orders.repository.OrderItemRepository;
import com.whoami.launch.order.orders.repository.OrderRepository;
import com.whoami.launch.order.orders.service.OrderService;
import com.whoami.launch.producer.OrderKafkaProducer;
import com.locato.topics.KafkaTopics;

@Service
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    private OrderKafkaProducer orderKafkaProducer;
    public OrderServiceImpl(
            CartRepository cartRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {

        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional
    public List<OrderResponse> placeOrder(
            String customerId,
            PlaceOrderRequest request) {

        List<Cart> cartItems = cartRepository.findByUserId(customerId);

        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Map<String, List<Cart>> grouped = cartItems.stream()
                .collect(Collectors.groupingBy(Cart::getShopId));

        List<OrderResponse> responses = new ArrayList<>();

        for (Map.Entry<String, List<Cart>> entry : grouped.entrySet()) {

            String shopId = entry.getKey();
            List<Cart> items = entry.getValue();

            String orderId = UUID.randomUUID().toString();

            Order order = new Order();
            order.setOrderId(orderId);
            order.setCustomerId(customerId);
            order.setShopId(shopId);
            order.setStatus(OrderStatus.REQUESTED);
            order.setCustomerNote(request.getCustomerNote());
            order.setDeliveryAddress(request.getDeliveryAddress());

            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal subtotal = BigDecimal.ZERO;

            for (Cart cart : items) {

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                orderItem.setProductId(cart.getProductId());
                orderItem.setProductNameSnapshot(cart.getProductNameSnapshot());
                orderItem.setImageSnapshot(cart.getImageSnapshot());
                orderItem.setPriceSnapshot(cart.getPriceSnapshot());
                orderItem.setQuantity(cart.getQuantity());

                orderItem.setTotalPrice(
                        cart.getPriceSnapshot()
                                .multiply(BigDecimal.valueOf(cart.getQuantity()))
                );

                subtotal = subtotal.add(orderItem.getTotalPrice());
                orderItems.add(orderItem);
            }

            order.setSubtotal(subtotal);

            Order savedOrder = orderRepository.save(order);

            orderItemRepository.saveAll(orderItems);

            // Order Placed Notification
            sendOrderStatusNotification(savedOrder);
            sendShopOrderNotification(savedOrder);

            responses.add(toResponse(savedOrder, orderItems));
        }

        cartRepository.deleteByUserId(customerId);

        return responses;
    }

    @Override
    public List<OrderResponse> getCustomerOrders(String customerId) {

        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(order -> toResponse(
                        order,
                        orderItemRepository.findByOrderId(order.getOrderId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getShopOrders(String shopId) {

        return orderRepository.findByShopId(shopId)
                .stream()
                .map(order -> toResponse(
                        order,
                        orderItemRepository.findByOrderId(order.getOrderId())))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderDetails(
            String orderId,
            String currentCustomerId,
            String currentShopId) {

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        return toResponse(
                order,
                orderItemRepository.findByOrderId(order.getOrderId()));
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            String orderId,
            OrderStatus newStatus,
            String shopId) {

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        order.setStatus(newStatus);

        Order updated = orderRepository.save(order);

        // Status Change Notification
        sendOrderStatusNotification(updated);

        return toResponse(
                updated,
                orderItemRepository.findByOrderId(updated.getOrderId()));
    }

    private void sendOrderStatusNotification(Order order) {

        String title;
        String message;

        switch (order.getStatus()) {

            case REQUESTED:
                title = "Order Placed";
                message = "Your order has been placed successfully.";
                break;

            case ACCEPTED:
                title = "Order Accepted";
                message = "Your order has been accepted by the shop.";
                break;

            case REJECTED:
                title = "Order Rejected";
                message = "Your order has been rejected by the shop.";
                break;

            case PREPARING:
                title = "Order Preparing";
                message = "Your order is currently being prepared.";
                break;

            case READY:
                title = "Order Ready";
                message = "Your order is ready.";
                break;

            case DELIVERED:
                title = "Order Delivered";
                message = "Your order has been delivered successfully.";
                break;

            case CANCELLED:
                title = "Order Cancelled";
                message = "Your order has been cancelled.";
                break;

            default:
                return;
        }

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
    }
    private OrderResponse toResponse(
            Order order,
            List<OrderItem> items) {

        OrderResponse response = new OrderResponse();

        response.setOrderId(order.getOrderId());
        response.setCustomerId(order.getCustomerId());
        response.setShopId(order.getShopId());
        response.setStatus(order.getStatus());
        response.setSubtotal(order.getSubtotal());
        response.setCustomerNote(order.getCustomerNote());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemResponse> itemResponses = items.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        response.setItems(itemResponses);

        return response;
    }

    private OrderItemResponse toItemResponse(OrderItem item) {

        OrderItemResponse response = new OrderItemResponse();

        response.setProductId(item.getProductId());
        response.setProductNameSnapshot(item.getProductNameSnapshot());
        response.setImageSnapshot(item.getImageSnapshot());
        response.setPriceSnapshot(item.getPriceSnapshot());
        response.setQuantity(item.getQuantity());
        response.setTotalPrice(item.getTotalPrice());

        return response;
    }
    private void sendShopOrderNotification(Order order) {

        NotificationEvent event =
                NotificationEvent.builder()
                        .userId(order.getShopId())
                        .title("New Order Received")
                        .message("New order received. Order ID: " + order.getOrderId())
                        .targetId(order.getOrderId())
                        .targetType("ORDER")
                        .type(NotificationType.ORDER)
                        .sendPush(true)
                        .build();

        orderKafkaProducer.publish(
                KafkaTopics.NOTIFICATION_EVENTS,
                event
        );
    }
}