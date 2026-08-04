package com.whoami.launch.enums;

/**
 * Enum representing different types of notifications in the system.
 */
public enum NotificationType {
    ORDER("Order Notification"),
    CHAT("Chat Notification"),
    SHOP("Shop Notification"),
    PRODUCT("Product Notification"),
    REEL("Reel Notification"),
    SERVICE("Service Notification"),
    ADMIN("Admin Notification"),
    SYSTEM("System Notification"),
    PROMOTION("Promotion Notification"),
    FOLLOW("Follow Notification");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
