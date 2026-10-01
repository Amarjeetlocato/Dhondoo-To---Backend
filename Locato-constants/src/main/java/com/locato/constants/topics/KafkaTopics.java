package com.locato.constants.topics;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    // =========================
    // IDENTITY / USER DOMAIN
    // =========================

    public static final String USER_EVENTS =
            "user-events";

    public static final String CUSTOMER_EVENTS =
            "customer-events";

    public static final String LOCATION_EVENTS =
            "location-events";


    // =========================
    // BUSINESS DOMAIN
    // =========================

    public static final String BUSINESS_EVENTS =
            "business-events";

    public static final String PRODUCT_EVENTS =
            "product-events";

    public static final String SERVICE_EVENTS =
            "service-events";

    public static final String REEL_EVENTS =
            "reel-events";


    // =========================
    // ORDER DOMAIN
    // =========================

    public static final String ORDER_EVENTS =
            "order-events";


    // =========================
    // CHAT DOMAIN
    // =========================

    public static final String CHAT_EVENTS =
            "chat-events";


    // =========================
    // NOTIFICATION DOMAIN
    // =========================

    public static final String NOTIFICATION_EVENTS =
            "notification-events";


    // =========================
    // BILLING DOMAIN
    // =========================

    public static final String BILLING_EVENTS =
            "billing-events";


    // =========================
    // BUSINESS OPERATION DOMAIN
    // =========================

    public static final String BUSINESS_OPERATION_EVENTS =
            "business-operation-events";


    // =========================
    // DEVICE TOKEN DOMAIN
    // =========================

    public static final String DEVICE_TOKEN_EVENTS =
            "device-token-events";


    // =========================
    // FOLLOW DOMAIN
    // =========================

    public static final String FOLLOW_EVENTS =
            "follow-events";
}
