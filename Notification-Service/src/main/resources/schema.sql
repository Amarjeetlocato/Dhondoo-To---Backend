-- Create Database
CREATE DATABASE IF NOT EXISTS notification_service;
USE notification_service;

-- Create Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    notification_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message LONGTEXT NOT NULL,
    image_url LONGTEXT,
    target_id VARCHAR(36),
    target_type VARCHAR(50),
    type VARCHAR(20) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_user_is_read (user_id, is_read),
    INDEX idx_user_is_deleted (user_id, is_deleted),
    INDEX idx_type (type),
    INDEX idx_target (target_id, target_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Notification Preferences Table
CREATE TABLE IF NOT EXISTS notification_preferences (
    preference_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL UNIQUE,
    order_notification BOOLEAN NOT NULL DEFAULT TRUE,
    chat_notification BOOLEAN NOT NULL DEFAULT TRUE,
    promotion_notification BOOLEAN NOT NULL DEFAULT TRUE,
    reel_notification BOOLEAN NOT NULL DEFAULT TRUE,
    product_notification BOOLEAN NOT NULL DEFAULT TRUE,
    shop_notification BOOLEAN NOT NULL DEFAULT TRUE,
    service_notification BOOLEAN NOT NULL DEFAULT TRUE,
    admin_notification BOOLEAN NOT NULL DEFAULT TRUE,
    follow_notification BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_pref_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Activity Logs Table
CREATE TABLE IF NOT EXISTS activity_logs (
    activity_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description LONGTEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_activity_user_id (user_id),
    INDEX idx_activity_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Device Tokens Table (FCM)
CREATE TABLE IF NOT EXISTS device_tokens (
    device_token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    device_token LONGTEXT NOT NULL UNIQUE,
    device_type VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_device_user_id (user_id),
    INDEX idx_device_token (device_token(255)),
    INDEX idx_device_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create composite indexes for common queries
CREATE INDEX idx_notification_user_status ON notifications(user_id, is_read, is_deleted, created_at DESC);
CREATE INDEX idx_notification_type_user ON notifications(user_id, type, is_deleted, created_at DESC);

-- Add foreign key-like constraints (using triggers if needed)
-- These indexes help with JOIN operations when querying related data

COMMIT;
