-- Create all required tables for Shop Service

-- Shops table
CREATE TABLE IF NOT EXISTS shops (
    shop_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    shop_name VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(20),
    address VARCHAR(500),
    latitude DOUBLE,
    longitude DOUBLE,
    total_shops INT DEFAULT 0,
    total_products INT DEFAULT 0,
    total_reels INT DEFAULT 0,
    total_services INT DEFAULT 0,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    product_id VARCHAR(36) PRIMARY KEY,
    shop_id VARCHAR(36) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_price DECIMAL(10, 2),
    product_description TEXT,
    quantity INT DEFAULT 0,
    product_images LONGTEXT,
    quality VARCHAR(50),
    order_type VARCHAR(50),
    visibility VARCHAR(50) DEFAULT 'PUBLIC',
    badges VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES shops(shop_id) ON DELETE CASCADE
);

-- Services table
CREATE TABLE IF NOT EXISTS services (
    service_id VARCHAR(36) PRIMARY KEY,
    shop_id VARCHAR(36) NOT NULL,
    service_name VARCHAR(255) NOT NULL,
    thumbnail_url VARCHAR(500),
    promo_video_url VARCHAR(500),
    service_description TEXT,
    price DECIMAL(10, 2),
    duration VARCHAR(100),
    order_type VARCHAR(50),
    suggestion TEXT,
    visibility VARCHAR(50) DEFAULT 'PUBLIC',
    badges VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES shops(shop_id) ON DELETE CASCADE
);

-- Reels table
CREATE TABLE IF NOT EXISTS reels (
    reel_id VARCHAR(36) PRIMARY KEY,
    shop_id VARCHAR(36) NOT NULL,
    reel_video VARCHAR(500),
    reel_thumbnail VARCHAR(500),
    reel_description TEXT,
    reel_reviews TEXT,
    reel_ratings DECIMAL(3, 1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES shops(shop_id) ON DELETE CASCADE
);

-- Customer Profiles table
CREATE TABLE IF NOT EXISTS customer_profiles (
    customer_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    logo_url VARCHAR(500),
    banner_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Locations table
CREATE TABLE IF NOT EXISTS locations (
    location_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_timestamp (timestamp)
);
