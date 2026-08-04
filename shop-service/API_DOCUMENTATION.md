# Shop Service API Documentation

**API Base URL:** `http://localhost:8079`

**Last Updated:** 2026-06-14

---

## Table of Contents
1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Common Response Formats](#common-response-formats)
4. [Shop API](#shop-api)
5. [Product API](#product-api)
6. [Service API](#service-api)
7. [Reel API](#reel-api)
8. [Customer Profile API](#customer-profile-api)
9. [Location API](#location-api)
10. [Nearby API](#nearby-api)
11. [Error Codes](#error-codes)

---

## Overview

This API provides endpoints for managing shops, products, services, reels, customer profiles, and locations in the Locato platform. The API supports CORS (Cross-Origin Resource Sharing) for frontend integration.

### Key Features:
- RESTful API design
- JSON request/response format
- UUID-based resource identification
- Geolocation-based services
- Real-time location tracking
- Multi-tenant support

---

## Authentication

> **Note:** Currently, this API does not have authentication implemented. For production use, implement JWT or OAuth2 authentication.

---

## Common Response Formats

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {},
  "timestamp": "2026-06-14T10:30:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error message here",
  "data": null,
  "timestamp": "2026-06-14T10:30:00"
}
```

### Standard HTTP Status Codes
- **200 OK** - Request successful
- **201 Created** - Resource created successfully
- **204 No Content** - Request successful, no content returned
- **400 Bad Request** - Invalid request parameters
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Server error

---

# SHOP API

## Base Path: `/api/shops`

### 1. Get All Shops
**Endpoint:** `GET /api/shops`

**Description:** Retrieve a list of all shops

**Response:**
```json
[
  {
    "shopId": "uuid-1234",
    "userId": "user-5678",
    "shopName": "John's Electronics",
    "mobileNumber": "+1-800-123-4567",
    "address": "123 Main St, City, State 12345",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "totalShops": 1,
    "totalProducts": 25,
    "totalReels": 5,
    "totalServices": 10,
    "status": "ACTIVE"
  }
]
```

### 2. Get Shop by ID
**Endpoint:** `GET /api/shops/{shopId}`

**Parameters:**
- `shopId` (path) - Shop UUID (required)

**Response:**
```json
{
  "shopId": "uuid-1234",
  "userId": "user-5678",
  "shopName": "John's Electronics",
  "mobileNumber": "+1-800-123-4567",
  "address": "123 Main St, City, State 12345",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "totalShops": 1,
  "totalProducts": 25,
  "totalReels": 5,
  "totalServices": 10,
  "status": "ACTIVE"
}
```

### 3. Get Shop by Name
**Endpoint:** `GET /api/shops/search/name/{shopName}`

**Parameters:**
- `shopName` (path) - Name of the shop (required)

**Response:**
```json
{
  "shopId": "uuid-1234",
  "userId": "user-5678",
  "shopName": "John's Electronics",
  "mobileNumber": "+1-800-123-4567",
  "address": "123 Main St, City, State 12345",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "totalShops": 1,
  "totalProducts": 25,
  "totalReels": 5,
  "totalServices": 10,
  "status": "ACTIVE"
}
```

### 4. Get Shops by User ID
**Endpoint:** `GET /api/shops/user/{userId}`

**Parameters:**
- `userId` (path) - User UUID (required)

**Response:**
```json
[
  {
    "shopId": "uuid-1234",
    "userId": "user-5678",
    "shopName": "John's Electronics",
    "mobileNumber": "+1-800-123-4567",
    "address": "123 Main St, City, State 12345",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "totalShops": 1,
    "totalProducts": 25,
    "totalReels": 5,
    "totalServices": 10,
    "status": "ACTIVE"
  }
]
```

### 5. Search Shops
**Endpoint:** `GET /api/shops/search/query`

**Parameters:**
- `query` (query) - Search keyword (required)

**Example:** `GET /api/shops/search/query?query=electronics`

**Response:**
```json
[
  {
    "shopId": "uuid-1234",
    "userId": "user-5678",
    "shopName": "John's Electronics",
    "mobileNumber": "+1-800-123-4567",
    "address": "123 Main St, City, State 12345",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "totalShops": 1,
    "totalProducts": 25,
    "totalReels": 5,
    "totalServices": 10,
    "status": "ACTIVE"
  }
]
```

### 6. Create Shop
**Endpoint:** `POST /api/shops`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": "user-5678",
  "shopName": "John's Electronics",
  "mobileNumber": "+1-800-123-4567",
  "address": "123 Main St, City, State 12345",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "status": "ACTIVE"
}
```

**Response:**
```json
{
  "shopId": "uuid-1234",
  "userId": "user-5678",
  "shopName": "John's Electronics",
  "mobileNumber": "+1-800-123-4567",
  "address": "123 Main St, City, State 12345",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "totalShops": 1,
  "totalProducts": 0,
  "totalReels": 0,
  "totalServices": 0,
  "status": "ACTIVE"
}
```

### 7. Update Shop
**Endpoint:** `PUT /api/shops/{shopId}`

**Parameters:**
- `shopId` (path) - Shop UUID (required)

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "shopName": "John's Updated Electronics",
  "mobileNumber": "+1-800-123-4567",
  "address": "456 Oak Ave, City, State 12345",
  "latitude": 40.7580,
  "longitude": -73.9855,
  "status": "ACTIVE"
}
```

**Response:**
```json
{
  "shopId": "uuid-1234",
  "userId": "user-5678",
  "shopName": "John's Updated Electronics",
  "mobileNumber": "+1-800-123-4567",
  "address": "456 Oak Ave, City, State 12345",
  "latitude": 40.7580,
  "longitude": -73.9855,
  "totalShops": 1,
  "totalProducts": 25,
  "totalReels": 5,
  "totalServices": 10,
  "status": "ACTIVE"
}
```

### 8. Delete Shop
**Endpoint:** `DELETE /api/shops/{shopId}`

**Parameters:**
- `shopId` (path) - Shop UUID (required)

**Response:** 
```
204 No Content
```

---

# PRODUCT API

## Base Path: `/api/products`

### 1. Get All Products
**Endpoint:** `GET /api/products`

**Response:**
```json
[
  {
    "productId": "prod-uuid-1",
    "shopId": "shop-uuid-1",
    "productName": "Samsung Galaxy S24",
    "productPrice": 999.99,
    "productDescription": "Latest flagship smartphone with advanced camera",
    "quantity": 10,
    "productImages": [
      "https://cloudinary.com/image1.jpg",
      "https://cloudinary.com/image2.jpg"
    ],
    "quality": "PREMIUM",
    "orderType": "ONLINE",
    "visibility": "PUBLIC",
    "badges": "NEW,TRENDING"
  }
]
```

### 2. Get Product by ID
**Endpoint:** `GET /api/products/{productId}`

**Parameters:**
- `productId` (path) - Product UUID (required)

**Response:**
```json
{
  "productId": "prod-uuid-1",
  "shopId": "shop-uuid-1",
  "productName": "Samsung Galaxy S24",
  "productPrice": 999.99,
  "productDescription": "Latest flagship smartphone with advanced camera",
  "quantity": 10,
  "productImages": [
    "https://cloudinary.com/image1.jpg",
    "https://cloudinary.com/image2.jpg"
  ],
  "quality": "PREMIUM",
  "orderType": "ONLINE",
  "visibility": "PUBLIC",
  "badges": "NEW,TRENDING"
}
```

### 3. Get Products by Name
**Endpoint:** `GET /api/products/search/name/{productName}`

**Parameters:**
- `productName` (path) - Product name (required)

**Response:**
```json
[
  {
    "productId": "prod-uuid-1",
    "shopId": "shop-uuid-1",
    "productName": "Samsung Galaxy S24",
    "productPrice": 999.99,
    "productDescription": "Latest flagship smartphone with advanced camera",
    "quantity": 10,
    "productImages": [
      "https://cloudinary.com/image1.jpg",
      "https://cloudinary.com/image2.jpg"
    ],
    "quality": "PREMIUM",
    "orderType": "ONLINE",
    "visibility": "PUBLIC",
    "badges": "NEW,TRENDING"
  }
]
```

### 4. Get Products by Shop ID
**Endpoint:** `GET /api/products/shop/{shopId}`

**Parameters:**
- `shopId` (path) - Shop UUID (required)

**Response:**
```json
[
  {
    "productId": "prod-uuid-1",
    "shopId": "shop-uuid-1",
    "productName": "Samsung Galaxy S24",
    "productPrice": 999.99,
    "productDescription": "Latest flagship smartphone with advanced camera",
    "quantity": 10,
    "productImages": [
      "https://cloudinary.com/image1.jpg",
      "https://cloudinary.com/image2.jpg"
    ],
    "quality": "PREMIUM",
    "orderType": "ONLINE",
    "visibility": "PUBLIC",
    "badges": "NEW,TRENDING"
  }
]
```

### 5. Get Products by Visibility
**Endpoint:** `GET /api/products/search/visibility/{visibility}`

**Parameters:**
- `visibility` (path) - PUBLIC or PRIVATE (required)

**Response:**
```json
[
  {
    "productId": "prod-uuid-1",
    "shopId": "shop-uuid-1",
    "productName": "Samsung Galaxy S24",
    "productPrice": 999.99,
    "productDescription": "Latest flagship smartphone with advanced camera",
    "quantity": 10,
    "productImages": ["https://cloudinary.com/image1.jpg"],
    "quality": "PREMIUM",
    "orderType": "ONLINE",
    "visibility": "PUBLIC",
    "badges": "NEW,TRENDING"
  }
]
```

### 6. Get Products by Badges
**Endpoint:** `GET /api/products/search/badges/{badges}`

**Parameters:**
- `badges` (path) - Badge name (NEW, TRENDING, SALE, etc.) (required)

**Response:**
```json
[
  {
    "productId": "prod-uuid-1",
    "shopId": "shop-uuid-1",
    "productName": "Samsung Galaxy S24",
    "productPrice": 999.99,
    "productDescription": "Latest flagship smartphone with advanced camera",
    "quantity": 10,
    "productImages": ["https://cloudinary.com/image1.jpg"],
    "quality": "PREMIUM",
    "orderType": "ONLINE",
    "visibility": "PUBLIC",
    "badges": "NEW,TRENDING"
  }
]
```

### 7. Get Products by Quality
**Endpoint:** `GET /api/products/search/quality/{quality}`

**Parameters:**
- `quality` (path) - PREMIUM, STANDARD, ECONOMY (required)

**Response:**
```json
[
  {
    "productId": "prod-uuid-1",
    "shopId": "shop-uuid-1",
    "productName": "Samsung Galaxy S24",
    "productPrice": 999.99,
    "productDescription": "Latest flagship smartphone with advanced camera",
    "quantity": 10,
    "productImages": ["https://cloudinary.com/image1.jpg"],
    "quality": "PREMIUM",
    "orderType": "ONLINE",
    "visibility": "PUBLIC",
    "badges": "NEW,TRENDING"
  }
]
```

### 8. Search Products
**Endpoint:** `GET /api/products/search/query`

**Parameters:**
- `query` (query) - Search keyword (required)

**Example:** `GET /api/products/search/query?query=samsung`

**Response:**
```json
[
  {
    "productId": "prod-uuid-1",
    "shopId": "shop-uuid-1",
    "productName": "Samsung Galaxy S24",
    "productPrice": 999.99,
    "productDescription": "Latest flagship smartphone with advanced camera",
    "quantity": 10,
    "productImages": ["https://cloudinary.com/image1.jpg"],
    "quality": "PREMIUM",
    "orderType": "ONLINE",
    "visibility": "PUBLIC",
    "badges": "NEW,TRENDING"
  }
]
```

### 9. Create Product
**Endpoint:** `POST /api/products`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "shopId": "shop-uuid-1",
  "productName": "Samsung Galaxy S24",
  "productPrice": 999.99,
  "productDescription": "Latest flagship smartphone with advanced camera",
  "quantity": 10,
  "productImages": [
    "https://cloudinary.com/image1.jpg",
    "https://cloudinary.com/image2.jpg"
  ],
  "quality": "PREMIUM",
  "orderType": "ONLINE",
  "visibility": "PUBLIC",
  "badges": "NEW,TRENDING"
}
```

**Response:**
```json
{
  "productId": "prod-uuid-1",
  "shopId": "shop-uuid-1",
  "productName": "Samsung Galaxy S24",
  "productPrice": 999.99,
  "productDescription": "Latest flagship smartphone with advanced camera",
  "quantity": 10,
  "productImages": [
    "https://cloudinary.com/image1.jpg",
    "https://cloudinary.com/image2.jpg"
  ],
  "quality": "PREMIUM",
  "orderType": "ONLINE",
  "visibility": "PUBLIC",
  "badges": "NEW,TRENDING"
}
```

### 10. Update Product
**Endpoint:** `PUT /api/products/{productId}`

**Parameters:**
- `productId` (path) - Product UUID (required)

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "productName": "Samsung Galaxy S24 Ultra",
  "productPrice": 1199.99,
  "productDescription": "Latest flagship smartphone with advanced camera and 5G",
  "quantity": 5,
  "quality": "PREMIUM",
  "orderType": "ONLINE",
  "visibility": "PUBLIC",
  "badges": "NEW,TRENDING,PREMIUM"
}
```

**Response:**
```json
{
  "productId": "prod-uuid-1",
  "shopId": "shop-uuid-1",
  "productName": "Samsung Galaxy S24 Ultra",
  "productPrice": 1199.99,
  "productDescription": "Latest flagship smartphone with advanced camera and 5G",
  "quantity": 5,
  "productImages": [
    "https://cloudinary.com/image1.jpg",
    "https://cloudinary.com/image2.jpg"
  ],
  "quality": "PREMIUM",
  "orderType": "ONLINE",
  "visibility": "PUBLIC",
  "badges": "NEW,TRENDING,PREMIUM"
}
```

### 11. Delete Product
**Endpoint:** `DELETE /api/products/{productId}`

**Parameters:**
- `productId` (path) - Product UUID (required)

**Response:**
```
204 No Content
```

---

# SERVICE API

## Base Path: `/api/services`

### 1. Get All Services
**Endpoint:** `GET /api/services`

**Response:**
```json
[
  {
    "serviceId": "svc-uuid-1",
    "shopId": "shop-uuid-1",
    "serviceName": "Mobile Phone Repair",
    "thumbnailUrl": "https://cloudinary.com/thumbnail.jpg",
    "promoVideoUrl": "https://cloudinary.com/video.mp4",
    "serviceDescription": "Professional repair for all mobile phones",
    "price": 49.99,
    "duration": "1 hour",
    "orderType": "OFFLINE",
    "suggestion": "Book now for best rates",
    "visibility": "PUBLIC",
    "badges": "RECOMMENDED,FAST"
  }
]
```

### 2. Get Service by ID
**Endpoint:** `GET /api/services/{serviceId}`

**Parameters:**
- `serviceId` (path) - Service UUID (required)

**Response:**
```json
{
  "serviceId": "svc-uuid-1",
  "shopId": "shop-uuid-1",
  "serviceName": "Mobile Phone Repair",
  "thumbnailUrl": "https://cloudinary.com/thumbnail.jpg",
  "promoVideoUrl": "https://cloudinary.com/video.mp4",
  "serviceDescription": "Professional repair for all mobile phones",
  "price": 49.99,
  "duration": "1 hour",
  "orderType": "OFFLINE",
  "suggestion": "Book now for best rates",
  "visibility": "PUBLIC",
  "badges": "RECOMMENDED,FAST"
}
```

### 3. Get Services by Name
**Endpoint:** `GET /api/services/search/name/{serviceName}`

**Parameters:**
- `serviceName` (path) - Service name (required)

**Response:**
```json
[
  {
    "serviceId": "svc-uuid-1",
    "shopId": "shop-uuid-1",
    "serviceName": "Mobile Phone Repair",
    "thumbnailUrl": "https://cloudinary.com/thumbnail.jpg",
    "promoVideoUrl": "https://cloudinary.com/video.mp4",
    "serviceDescription": "Professional repair for all mobile phones",
    "price": 49.99,
    "duration": "1 hour",
    "orderType": "OFFLINE",
    "suggestion": "Book now for best rates",
    "visibility": "PUBLIC",
    "badges": "RECOMMENDED,FAST"
  }
]
```

### 4. Get Services by Shop ID
**Endpoint:** `GET /api/services/shop/{shopId}`

**Parameters:**
- `shopId` (path) - Shop UUID (required)

**Response:**
```json
[
  {
    "serviceId": "svc-uuid-1",
    "shopId": "shop-uuid-1",
    "serviceName": "Mobile Phone Repair",
    "thumbnailUrl": "https://cloudinary.com/thumbnail.jpg",
    "promoVideoUrl": "https://cloudinary.com/video.mp4",
    "serviceDescription": "Professional repair for all mobile phones",
    "price": 49.99,
    "duration": "1 hour",
    "orderType": "OFFLINE",
    "suggestion": "Book now for best rates",
    "visibility": "PUBLIC",
    "badges": "RECOMMENDED,FAST"
  }
]
```

### 5. Get Services by Visibility
**Endpoint:** `GET /api/services/search/visibility/{visibility}`

**Parameters:**
- `visibility` (path) - PUBLIC or PRIVATE (required)

**Response:**
```json
[
  {
    "serviceId": "svc-uuid-1",
    "shopId": "shop-uuid-1",
    "serviceName": "Mobile Phone Repair",
    "thumbnailUrl": "https://cloudinary.com/thumbnail.jpg",
    "promoVideoUrl": "https://cloudinary.com/video.mp4",
    "serviceDescription": "Professional repair for all mobile phones",
    "price": 49.99,
    "duration": "1 hour",
    "orderType": "OFFLINE",
    "suggestion": "Book now for best rates",
    "visibility": "PUBLIC",
    "badges": "RECOMMENDED,FAST"
  }
]
```

### 6. Get Services by Badges
**Endpoint:** `GET /api/services/search/badges/{badges}`

**Parameters:**
- `badges` (path) - Badge name (required)

**Response:**
```json
[
  {
    "serviceId": "svc-uuid-1",
    "shopId": "shop-uuid-1",
    "serviceName": "Mobile Phone Repair",
    "thumbnailUrl": "https://cloudinary.com/thumbnail.jpg",
    "promoVideoUrl": "https://cloudinary.com/video.mp4",
    "serviceDescription": "Professional repair for all mobile phones",
    "price": 49.99,
    "duration": "1 hour",
    "orderType": "OFFLINE",
    "suggestion": "Book now for best rates",
    "visibility": "PUBLIC",
    "badges": "RECOMMENDED,FAST"
  }
]
```

### 7. Search Services
**Endpoint:** `GET /api/services/search/query`

**Parameters:**
- `query` (query) - Search keyword (required)

**Example:** `GET /api/services/search/query?query=repair`

**Response:**
```json
[
  {
    "serviceId": "svc-uuid-1",
    "shopId": "shop-uuid-1",
    "serviceName": "Mobile Phone Repair",
    "thumbnailUrl": "https://cloudinary.com/thumbnail.jpg",
    "promoVideoUrl": "https://cloudinary.com/video.mp4",
    "serviceDescription": "Professional repair for all mobile phones",
    "price": 49.99,
    "duration": "1 hour",
    "orderType": "OFFLINE",
    "suggestion": "Book now for best rates",
    "visibility": "PUBLIC",
    "badges": "RECOMMENDED,FAST"
  }
]
```

### 8. Create Service
**Endpoint:** `POST /api/services`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "shopId": "shop-uuid-1",
  "serviceName": "Mobile Phone Repair",
  "thumbnailUrl": "https://cloudinary.com/thumbnail.jpg",
  "promoVideoUrl": "https://cloudinary.com/video.mp4",
  "serviceDescription": "Professional repair for all mobile phones",
  "price": 49.99,
  "duration": "1 hour",
  "orderType": "OFFLINE",
  "suggestion": "Book now for best rates",
  "visibility": "PUBLIC",
  "badges": "RECOMMENDED,FAST"
}
```

**Response:**
```json
{
  "serviceId": "svc-uuid-1",
  "shopId": "shop-uuid-1",
  "serviceName": "Mobile Phone Repair",
  "thumbnailUrl": "https://cloudinary.com/thumbnail.jpg",
  "promoVideoUrl": "https://cloudinary.com/video.mp4",
  "serviceDescription": "Professional repair for all mobile phones",
  "price": 49.99,
  "duration": "1 hour",
  "orderType": "OFFLINE",
  "suggestion": "Book now for best rates",
  "visibility": "PUBLIC",
  "badges": "RECOMMENDED,FAST"
}
```

### 9. Update Service
**Endpoint:** `PUT /api/services/{serviceId}`

**Parameters:**
- `serviceId` (path) - Service UUID (required)

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "serviceName": "Premium Mobile Phone Repair",
  "price": 59.99,
  "duration": "30 minutes",
  "serviceDescription": "Expert repair with warranty",
  "visibility": "PUBLIC",
  "badges": "RECOMMENDED,FAST,WARRANTY"
}
```

**Response:**
```json
{
  "serviceId": "svc-uuid-1",
  "shopId": "shop-uuid-1",
  "serviceName": "Premium Mobile Phone Repair",
  "thumbnailUrl": "https://cloudinary.com/thumbnail.jpg",
  "promoVideoUrl": "https://cloudinary.com/video.mp4",
  "serviceDescription": "Expert repair with warranty",
  "price": 59.99,
  "duration": "30 minutes",
  "orderType": "OFFLINE",
  "suggestion": "Book now for best rates",
  "visibility": "PUBLIC",
  "badges": "RECOMMENDED,FAST,WARRANTY"
}
```

### 10. Delete Service
**Endpoint:** `DELETE /api/services/{serviceId}`

**Parameters:**
- `serviceId` (path) - Service UUID (required)

**Response:**
```
204 No Content
```

---

# REEL API

## Base Path: `/api/reels`

### 1. Get All Reels
**Endpoint:** `GET /api/reels`

**Response:**
```json
[
  {
    "reelId": "reel-uuid-1",
    "shopId": "shop-uuid-1",
    "reelVideo": "https://cloudinary.com/video.mp4",
    "reelThumbnail": "https://cloudinary.com/thumbnail.jpg",
    "reelDescription": "Check out our new product line!",
    "reelReviews": "Great quality products",
    "reelRatings": 4.5
  }
]
```

### 2. Get Reel by ID
**Endpoint:** `GET /api/reels/{reelId}`

**Parameters:**
- `reelId` (path) - Reel UUID (required)

**Response:**
```json
{
  "reelId": "reel-uuid-1",
  "shopId": "shop-uuid-1",
  "reelVideo": "https://cloudinary.com/video.mp4",
  "reelThumbnail": "https://cloudinary.com/thumbnail.jpg",
  "reelDescription": "Check out our new product line!",
  "reelReviews": "Great quality products",
  "reelRatings": 4.5
}
```

### 3. Get Reels by Shop ID
**Endpoint:** `GET /api/reels/shop/{shopId}`

**Parameters:**
- `shopId` (path) - Shop UUID (required)

**Response:**
```json
[
  {
    "reelId": "reel-uuid-1",
    "shopId": "shop-uuid-1",
    "reelVideo": "https://cloudinary.com/video.mp4",
    "reelThumbnail": "https://cloudinary.com/thumbnail.jpg",
    "reelDescription": "Check out our new product line!",
    "reelReviews": "Great quality products",
    "reelRatings": 4.5
  }
]
```

### 4. Search Reels
**Endpoint:** `GET /api/reels/search/query`

**Parameters:**
- `query` (query) - Search keyword (required)

**Example:** `GET /api/reels/search/query?query=products`

**Response:**
```json
[
  {
    "reelId": "reel-uuid-1",
    "shopId": "shop-uuid-1",
    "reelVideo": "https://cloudinary.com/video.mp4",
    "reelThumbnail": "https://cloudinary.com/thumbnail.jpg",
    "reelDescription": "Check out our new product line!",
    "reelReviews": "Great quality products",
    "reelRatings": 4.5
  }
]
```

### 5. Create Reel
**Endpoint:** `POST /api/reels`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "shopId": "shop-uuid-1",
  "reelVideo": "https://cloudinary.com/video.mp4",
  "reelThumbnail": "https://cloudinary.com/thumbnail.jpg",
  "reelDescription": "Check out our new product line!",
  "reelReviews": "Great quality products",
  "reelRatings": 4.5
}
```

**Response:**
```json
{
  "reelId": "reel-uuid-1",
  "shopId": "shop-uuid-1",
  "reelVideo": "https://cloudinary.com/video.mp4",
  "reelThumbnail": "https://cloudinary.com/thumbnail.jpg",
  "reelDescription": "Check out our new product line!",
  "reelReviews": "Great quality products",
  "reelRatings": 4.5
}
```

### 6. Update Reel
**Endpoint:** `PUT /api/reels/{reelId}`

**Parameters:**
- `reelId` (path) - Reel UUID (required)

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "reelDescription": "Updated description",
  "reelReviews": "Excellent quality",
  "reelRatings": 4.8
}
```

**Response:**
```json
{
  "reelId": "reel-uuid-1",
  "shopId": "shop-uuid-1",
  "reelVideo": "https://cloudinary.com/video.mp4",
  "reelThumbnail": "https://cloudinary.com/thumbnail.jpg",
  "reelDescription": "Updated description",
  "reelReviews": "Excellent quality",
  "reelRatings": 4.8
}
```

### 7. Delete Reel
**Endpoint:** `DELETE /api/reels/{reelId}`

**Parameters:**
- `reelId` (path) - Reel UUID (required)

**Response:**
```
204 No Content
```

---

# CUSTOMER PROFILE API

## Base Path: `/api/customer-profiles`

### 1. Get All Customer Profiles
**Endpoint:** `GET /api/customer-profiles`

**Response:**
```json
[
  {
    "customerId": "cust-uuid-1",
    "userId": "user-uuid-1",
    "username": "john_doe",
    "email": "john@example.com",
    "logoUrl": "https://cloudinary.com/logo.png",
    "bannerUrl": "https://cloudinary.com/banner.jpg"
  }
]
```

### 2. Get Customer Profile by ID
**Endpoint:** `GET /api/customer-profiles/{customerId}`

**Parameters:**
- `customerId` (path) - Customer UUID (required)

**Response:**
```json
{
  "customerId": "cust-uuid-1",
  "userId": "user-uuid-1",
  "username": "john_doe",
  "email": "john@example.com",
  "logoUrl": "https://cloudinary.com/logo.png",
  "bannerUrl": "https://cloudinary.com/banner.jpg"
}
```

### 3. Get Customer Profile by User ID
**Endpoint:** `GET /api/customer-profiles/user/{userId}`

**Parameters:**
- `userId` (path) - User UUID (required)

**Response:**
```json
{
  "customerId": "cust-uuid-1",
  "userId": "user-uuid-1",
  "username": "john_doe",
  "email": "john@example.com",
  "logoUrl": "https://cloudinary.com/logo.png",
  "bannerUrl": "https://cloudinary.com/banner.jpg"
}
```

### 4. Get Customer Profile by Username
**Endpoint:** `GET /api/customer-profiles/search/username/{username}`

**Parameters:**
- `username` (path) - Username (required)

**Response:**
```json
{
  "customerId": "cust-uuid-1",
  "userId": "user-uuid-1",
  "username": "john_doe",
  "email": "john@example.com",
  "logoUrl": "https://cloudinary.com/logo.png",
  "bannerUrl": "https://cloudinary.com/banner.jpg"
}
```

### 5. Get Customer Profile by Email
**Endpoint:** `GET /api/customer-profiles/search/email/{email}`

**Parameters:**
- `email` (path) - Email address (required)

**Response:**
```json
{
  "customerId": "cust-uuid-1",
  "userId": "user-uuid-1",
  "username": "john_doe",
  "email": "john@example.com",
  "logoUrl": "https://cloudinary.com/logo.png",
  "bannerUrl": "https://cloudinary.com/banner.jpg"
}
```

### 6. Create Customer Profile
**Endpoint:** `POST /api/customer-profiles`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": "user-uuid-1",
  "username": "john_doe",
  "email": "john@example.com",
  "logoUrl": "https://cloudinary.com/logo.png",
  "bannerUrl": "https://cloudinary.com/banner.jpg"
}
```

**Response:**
```json
{
  "customerId": "cust-uuid-1",
  "userId": "user-uuid-1",
  "username": "john_doe",
  "email": "john@example.com",
  "logoUrl": "https://cloudinary.com/logo.png",
  "bannerUrl": "https://cloudinary.com/banner.jpg"
}
```

### 7. Update Customer Profile
**Endpoint:** `PUT /api/customer-profiles/{customerId}`

**Parameters:**
- `customerId` (path) - Customer UUID (required)

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "john_doe_updated",
  "email": "newemail@example.com",
  "logoUrl": "https://cloudinary.com/new-logo.png",
  "bannerUrl": "https://cloudinary.com/new-banner.jpg"
}
```

**Response:**
```json
{
  "customerId": "cust-uuid-1",
  "userId": "user-uuid-1",
  "username": "john_doe_updated",
  "email": "newemail@example.com",
  "logoUrl": "https://cloudinary.com/new-logo.png",
  "bannerUrl": "https://cloudinary.com/new-banner.jpg"
}
```

### 8. Delete Customer Profile
**Endpoint:** `DELETE /api/customer-profiles/{customerId}`

**Parameters:**
- `customerId` (path) - Customer UUID (required)

**Response:**
```
204 No Content
```

---

# LOCATION API

## Base Path: `/api/locations`

### 1. Get All Locations
**Endpoint:** `GET /api/locations`

**Response:**
```json
[
  {
    "locationId": "loc-uuid-1",
    "userId": "user-uuid-1",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "timestamp": "2026-06-14T10:30:00"
  }
]
```

### 2. Get Location by ID
**Endpoint:** `GET /api/locations/{locationId}`

**Parameters:**
- `locationId` (path) - Location UUID (required)

**Response:**
```json
{
  "locationId": "loc-uuid-1",
  "userId": "user-uuid-1",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "timestamp": "2026-06-14T10:30:00"
}
```

### 3. Get Locations by User ID
**Endpoint:** `GET /api/locations/user/{userId}`

**Parameters:**
- `userId` (path) - User UUID (required)

**Response:**
```json
[
  {
    "locationId": "loc-uuid-1",
    "userId": "user-uuid-1",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "timestamp": "2026-06-14T10:30:00"
  }
]
```

### 4. Get Recent Locations by User ID
**Endpoint:** `GET /api/locations/user/{userId}/recent`

**Parameters:**
- `userId` (path) - User UUID (required)

**Response:**
```json
[
  {
    "locationId": "loc-uuid-1",
    "userId": "user-uuid-1",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "timestamp": "2026-06-14T10:30:00"
  }
]
```

### 5. Get Locations by Time Range
**Endpoint:** `GET /api/locations/search/time-range`

**Parameters:**
- `startTime` (query) - Start timestamp in ISO format (required)
- `endTime` (query) - End timestamp in ISO format (required)

**Example:** `GET /api/locations/search/time-range?startTime=2026-06-14T08:00:00&endTime=2026-06-14T12:00:00`

**Response:**
```json
[
  {
    "locationId": "loc-uuid-1",
    "userId": "user-uuid-1",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "timestamp": "2026-06-14T10:30:00"
  }
]
```

### 6. Get Locations by Coordinates
**Endpoint:** `GET /api/locations/search/coordinates`

**Parameters:**
- `latitude` (query) - Latitude (required)
- `longitude` (query) - Longitude (required)

**Example:** `GET /api/locations/search/coordinates?latitude=40.7128&longitude=-74.0060`

**Response:**
```json
[
  {
    "locationId": "loc-uuid-1",
    "userId": "user-uuid-1",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "timestamp": "2026-06-14T10:30:00"
  }
]
```

### 7. Create Location
**Endpoint:** `POST /api/locations`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": "user-uuid-1",
  "latitude": 40.7128,
  "longitude": -74.0060
}
```

**Response:**
```json
{
  "locationId": "loc-uuid-1",
  "userId": "user-uuid-1",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "timestamp": "2026-06-14T10:30:00"
}
```

### 8. Update Location
**Endpoint:** `PUT /api/locations/{locationId}`

**Parameters:**
- `locationId` (path) - Location UUID (required)

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "latitude": 40.7580,
  "longitude": -73.9855
}
```

**Response:**
```json
{
  "locationId": "loc-uuid-1",
  "userId": "user-uuid-1",
  "latitude": 40.7580,
  "longitude": -73.9855,
  "timestamp": "2026-06-14T10:35:00"
}
```

### 9. Delete Location
**Endpoint:** `DELETE /api/locations/{locationId}`

**Parameters:**
- `locationId` (path) - Location UUID (required)

**Response:**
```
204 No Content
```

---

# NEARBY API

## Base Path: `/api/nearby`

### 1. Get Nearby Shops
**Endpoint:** `GET /api/nearby/shops`

**Parameters:**
- `userId` (query) - Current user UUID (required)
- `radiusKm` (query) - Search radius in kilometers (optional, default: 10)
- `limit` (query) - Maximum number of results (optional, default: 20)

**Example:** `GET /api/nearby/shops?userId=user-uuid-1&radiusKm=5&limit=10`

**Response:**
```json
[
  {
    "shopId": "shop-uuid-1",
    "shopName": "John's Electronics",
    "userId": "user-5678",
    "address": "123 Main St, City, State 12345",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "distance": 2.5,
    "mobileNumber": "+1-800-123-4567"
  },
  {
    "shopId": "shop-uuid-2",
    "shopName": "Tech Store",
    "userId": "user-9012",
    "address": "456 Oak Ave, City, State 12345",
    "latitude": 40.7580,
    "longitude": -73.9855,
    "distance": 4.2,
    "mobileNumber": "+1-800-987-6543"
  }
]
```

### 2. Get Nearby Products
**Endpoint:** `GET /api/nearby/products`

**Parameters:**
- `userId` (query) - Current user UUID (required)
- `radiusKm` (query) - Search radius in kilometers (optional, default: 10)
- `limit` (query) - Maximum number of results (optional, default: 20)

**Example:** `GET /api/nearby/products?userId=user-uuid-1&radiusKm=5&limit=10`

**Response:**
```json
[
  {
    "productId": "prod-uuid-1",
    "productName": "Samsung Galaxy S24",
    "shopId": "shop-uuid-1",
    "productPrice": 999.99,
    "productDescription": "Latest flagship smartphone",
    "distance": 2.5
  },
  {
    "productId": "prod-uuid-2",
    "productName": "iPhone 16",
    "shopId": "shop-uuid-2",
    "productPrice": 1099.99,
    "productDescription": "Latest Apple smartphone",
    "distance": 4.2
  }
]
```

### 3. Get Nearby Services
**Endpoint:** `GET /api/nearby/services`

**Parameters:**
- `userId` (query) - Current user UUID (required)
- `radiusKm` (query) - Search radius in kilometers (optional, default: 10)
- `limit` (query) - Maximum number of results (optional, default: 20)

**Example:** `GET /api/nearby/services?userId=user-uuid-1&radiusKm=5&limit=10`

**Response:**
```json
[
  {
    "serviceId": "svc-uuid-1",
    "serviceName": "Mobile Phone Repair",
    "shopId": "shop-uuid-1",
    "price": 49.99,
    "serviceDescription": "Professional repair",
    "distance": 2.5
  }
]
```

### 4. Get Nearby Reels
**Endpoint:** `GET /api/nearby/reels`

**Parameters:**
- `userId` (query) - Current user UUID (required)
- `radiusKm` (query) - Search radius in kilometers (optional, default: 10)
- `limit` (query) - Maximum number of results (optional, default: 20)

**Example:** `GET /api/nearby/reels?userId=user-uuid-1&radiusKm=5&limit=10`

**Response:**
```json
[
  {
    "reelId": "reel-uuid-1",
    "shopId": "shop-uuid-1",
    "reelDescription": "Check out our new products",
    "reelThumbnail": "https://cloudinary.com/thumbnail.jpg",
    "distance": 2.5
  }
]
```

---

# ERROR CODES

## Standard Error Responses

### 400 Bad Request
Returned when request parameters are invalid or missing required fields.

**Response:**
```json
{
  "success": false,
  "message": "Invalid request parameters",
  "data": null,
  "timestamp": "2026-06-14T10:30:00"
}
```

### 404 Not Found
Returned when requested resource does not exist.

**Response:**
```json
{
  "success": false,
  "message": "Resource not found",
  "data": null,
  "timestamp": "2026-06-14T10:30:00"
}
```

### 500 Internal Server Error
Returned when an unexpected server error occurs.

**Response:**
```json
{
  "success": false,
  "message": "Internal server error",
  "data": null,
  "timestamp": "2026-06-14T10:30:00"
}
```

---

## Frontend Integration Examples

### Using JavaScript Fetch API

```javascript
// GET request
const getShops = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/shops');
    const shops = await response.json();
    console.log(shops);
  } catch (error) {
    console.error('Error:', error);
  }
};

// POST request
const createProduct = async (productData) => {
  try {
    const response = await fetch('http://localhost:8080/api/products', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(productData)
    });
    const createdProduct = await response.json();
    console.log(createdProduct);
  } catch (error) {
    console.error('Error:', error);
  }
};

// PUT request
const updateService = async (serviceId, updatedData) => {
  try {
    const response = await fetch(`http://localhost:8080/api/services/${serviceId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(updatedData)
    });
    const updatedService = await response.json();
    console.log(updatedService);
  } catch (error) {
    console.error('Error:', error);
  }
};

// DELETE request
const deleteProduct = async (productId) => {
  try {
    const response = await fetch(`http://localhost:8080/api/products/${productId}`, {
      method: 'DELETE'
    });
    if (response.status === 204) {
      console.log('Product deleted successfully');
    }
  } catch (error) {
    console.error('Error:', error);
  }
};
```

### Using Axios

```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

// GET all shops
axios.get(`${API_BASE_URL}/api/shops`)
  .then(response => console.log(response.data))
  .catch(error => console.error(error));

// GET shop by ID
axios.get(`${API_BASE_URL}/api/shops/uuid-1234`)
  .then(response => console.log(response.data))
  .catch(error => console.error(error));

// POST new product
axios.post(`${API_BASE_URL}/api/products`, {
  shopId: 'shop-uuid-1',
  productName: 'Samsung Galaxy S24',
  productPrice: 999.99,
  productDescription: 'Latest flagship smartphone',
  quantity: 10,
  productImages: ['https://cloudinary.com/image1.jpg'],
  quality: 'PREMIUM',
  orderType: 'ONLINE',
  visibility: 'PUBLIC',
  badges: 'NEW,TRENDING'
})
  .then(response => console.log(response.data))
  .catch(error => console.error(error));

// PUT update service
axios.put(`${API_BASE_URL}/api/services/svc-uuid-1`, {
  serviceName: 'Updated Service',
  price: 59.99,
  visibility: 'PUBLIC'
})
  .then(response => console.log(response.data))
  .catch(error => console.error(error));

// DELETE product
axios.delete(`${API_BASE_URL}/api/products/prod-uuid-1`)
  .then(() => console.log('Product deleted'))
  .catch(error => console.error(error));
```

---

## Notes for Frontend Developers

1. **CORS Enabled**: All endpoints have CORS enabled with `*` origins and 3600 seconds max age.
2. **UUID Format**: All IDs are in UUID format
3. **Timestamps**: Use ISO 8601 format for date/time parameters
4. **Nullable Fields**: Many fields are optional. Include only required fields in POST/PUT requests
5. **Images**: All image URLs should be from Cloudinary or your configured CDN
6. **Pagination**: Currently not implemented. Use `limit` parameter in nearby endpoints for result limiting
7. **Sorting**: Not implemented. Results are returned in database order
8. **Filtering**: Use dedicated search endpoints rather than generic filtering
9. **Rate Limiting**: Not currently implemented
10. **Caching**: Implement client-side caching for frequently accessed resources

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-06-14 | Initial API documentation |

