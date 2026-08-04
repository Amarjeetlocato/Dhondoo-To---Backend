# Notification Service - Production Ready

A production-ready microservice for managing notifications with Firebase Cloud Messaging (FCM) integration, built with Spring Boot 3, Java 21, and MySQL.

## Features

✅ **Complete Notification Management**
- Create, read, update, and soft-delete notifications
- Paginated notification retrieval
- Filter notifications by type
- Mark notifications as read individually or bulk
- Unread count tracking

✅ **Firebase Cloud Messaging (FCM) Integration**
- Push notification delivery
- Automatic retry mechanism with exponential backoff
- Device token management
- Multicast messaging support
- Failed token deactivation

✅ **User Preferences**
- Granular notification preference management
- Per-notification-type opt-in/opt-out
- Default preferences for new users

✅ **Activity Logging**
- User activity tracking
- Paginated activity feed
- Sortable by date

✅ **Microservice Architecture**
- Eureka service discovery integration
- OpenFeign for inter-service communication
- Circuit breaker support (Resilience4j)
- Actuator health endpoints
- Prometheus metrics support

✅ **API Documentation**
- Swagger 3.0 / OpenAPI integration
- Full endpoint documentation
- Interactive API testing

✅ **Production Quality**
- Global exception handling
- Comprehensive logging
- Database connection pooling (HikariCP)
- Transaction management
- Optimized database indexes
- Virtual threads support (Java 21)

## Tech Stack

- **Java**: 21 (with Virtual Threads support)
- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Database**: MySQL 8.0+
- **Firebase Admin SDK**: 9.2.0
- **Build Tool**: Maven
- **Message Pattern**: Asynchronous (FCM)
- **Service Discovery**: Eureka
- **API Documentation**: OpenAPI 3.0 (Swagger UI)

## Prerequisites

- Java 21 JDK
- MySQL 8.0 or higher
- Maven 3.6.0 or higher
- Firebase Project with Admin SDK credentials
- Eureka Server running (optional for development)

## Installation

### 1. Clone and Setup

```bash
cd Notification-Service
```

### 2. Database Setup

Run the schema script to create tables:

```sql
-- Execute schema.sql in MySQL
mysql -u root -p < src/main/resources/schema.sql
```

Or use the application properties for auto-creation:
```properties
spring.jpa.hibernate.ddl-auto=update
```

### 3. Firebase Configuration

1. Create a Firebase project at https://console.firebase.google.com
2. Download the service account JSON key
3. Place it in `src/main/resources/firebase-config.json`

### 4. Update Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/notification_db
spring.datasource.username=root
spring.datasource.password=your_password

# Eureka (if using service discovery)
eureka.client.service-url.defaultZone=http://eureka-server:8761/eureka/

# Firebase
firebase.config.path=classpath:firebase-config.json
firebase.database-url=https://your-firebase-project.firebaseio.com
```

### 5. Build and Run

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the JAR directly
java -jar target/notification-service-1.0.0.jar
```

The service will start on `http://localhost:8080`

## API Endpoints

### Notifications

#### Create Notification (Internal API)
```
POST /api/notifications/internal-api/notifications
Content-Type: application/json

{
  "userId": "user-123",
  "title": "Order Confirmed",
  "message": "Your order has been confirmed",
  "imageUrl": "https://example.com/image.jpg",
  "targetId": "order-456",
  "targetType": "ORDER",
  "type": "ORDER",
  "sendPush": true
}
```

#### Get User Notifications
```
GET /api/notifications/user/{userId}?page=0&size=20
```

#### Get Unread Notifications
```
GET /api/notifications/unread/{userId}?page=0&size=20
```

#### Get Unread Count
```
GET /api/notifications/unread-count/{userId}
```

#### Get Notifications by Type
```
GET /api/notifications/user/{userId}?type=ORDER&page=0&size=20
```

#### Mark as Read
```
PUT /api/notifications/read/{notificationId}
```

#### Mark All as Read
```
PUT /api/notifications/read-all/{userId}
```

#### Delete Notification
```
DELETE /api/notifications/{notificationId}
```

### Preferences

#### Get User Preferences
```
GET /api/preferences/{userId}
```

#### Update Preferences
```
PUT /api/preferences/{userId}
Content-Type: application/json

{
  "orderNotification": true,
  "chatNotification": false,
  "promotionNotification": true,
  "reelNotification": true,
  "productNotification": true,
  "shopNotification": false,
  "serviceNotification": true,
  "adminNotification": true,
  "followNotification": true
}
```

#### Reset to Default
```
POST /api/preferences/reset/{userId}
```

### Device Tokens (FCM)

#### Register Device Token
```
POST /api/device-tokens/register
?userId=user-123&deviceToken=fcm_token_here&deviceType=ANDROID
```

#### Get Active Tokens
```
GET /api/device-tokens/user/{userId}
```

#### Deactivate Token
```
PUT /api/device-tokens/deactivate/{tokenId}
```

#### Remove Token
```
DELETE /api/device-tokens/remove
?userId=user-123&deviceToken=fcm_token_here
```

### Activity Logs

#### Get Activity Logs
```
GET /api/activity-logs/user/{userId}?page=0&size=20
```

#### Get Activity Count
```
GET /api/activity-logs/count/{userId}
```

## API Documentation

### Swagger UI
Visit: `http://localhost:8080/swagger-ui.html`

### OpenAPI JSON
Visit: `http://localhost:8080/v3/api-docs`

## Using in Other Services

### Option 1: Add as Dependency (Create a shared library)

```xml
<dependency>
    <groupId>com.whoami</groupId>
    <artifactId>notification-service</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Option 2: Use Feign Client

Enable Feign clients in your service:

```java
@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication {
    // ...
}
```

Inject and use the client:

```java
@Service
public class OrderService {
    
    @Autowired
    private NotificationServiceClient notificationClient;
    
    public void createOrder(Order order) {
        // Create order logic
        
        // Send notification
        NotificationRequest request = NotificationRequest.builder()
            .userId(order.getUserId())
            .title("Order Confirmed")
            .message("Your order has been confirmed")
            .targetId(order.getOrderId())
            .targetType("ORDER")
            .type(NotificationType.ORDER)
            .sendPush(true)
            .build();
        
        notificationClient.createNotification(request);
    }
}
```

## Notification Types

- `ORDER` - Order-related notifications
- `CHAT` - Chat/Message notifications
- `SHOP` - Shop notifications
- `PRODUCT` - Product notifications
- `REEL` - Reel/Social content notifications
- `SERVICE` - Service notifications
- `ADMIN` - Administrative notifications
- `SYSTEM` - System notifications
- `PROMOTION` - Promotional notifications
- `FOLLOW` - Follow/Social notifications

## Database Schema

### Notifications Table
- `notification_id` - Primary key (UUID)
- `user_id` - User identifier
- `title` - Notification title
- `message` - Notification message
- `imageUrl` - Optional image URL
- `targetId` - Reference to related entity
- `targetType` - Type of related entity
- `type` - Notification type (Enum)
- `isRead` - Read status
- `isDeleted` - Soft delete flag
- `createdAt` - Creation timestamp
- `updatedAt` - Last update timestamp

### Notification Preferences Table
- Stores per-user notification preferences
- Unique constraint on userId
- Default all notifications enabled

### Device Tokens Table
- Stores FCM device tokens
- Multiple tokens per user supported
- Tracks device type (iOS, Android, Web)
- Soft active flag

### Activity Logs Table
- Tracks user activities
- Indexed for quick retrieval

## Monitoring

### Health Check
```
GET http://localhost:8080/actuator/health
```

### Metrics
```
GET http://localhost:8080/actuator/metrics
GET http://localhost:8080/actuator/prometheus
```

## Configuration Examples

### Development
```properties
spring.jpa.hibernate.ddl-auto=update
logging.level.com.whoami.launch=DEBUG
app.env=development
```

### Production
```properties
spring.jpa.hibernate.ddl-auto=validate
logging.level.com.whoami.launch=INFO
app.env=production
spring.datasource.hikari.maximum-pool-size=50
```

## Best Practices

1. **Use Internal API** - Other services should use `/internal-api/notifications` for creation
2. **Prefer Async** - FCM sending is asynchronous with retry
3. **Device Token Management** - Remove tokens when users uninstall app
4. **User Preferences** - Always check user preferences before sending
5. **Error Handling** - Implement proper error handling when using Feign client
6. **Rate Limiting** - Consider implementing rate limiting for production

## Performance Optimization

- Database connection pooling with HikariCP
- Virtual Threads for handling concurrent async tasks
- Optimized indexes on frequently queried columns
- Soft deletes to avoid hard deletions
- Pagination for large result sets
- Async FCM delivery

## Security Considerations

1. **Authentication** - Add OAuth2/JWT for API security
2. **Authorization** - Users can only access their own notifications
3. **Firebase Security** - Keep firebase-config.json secure
4. **Credentials** - Use environment variables for sensitive config
5. **Rate Limiting** - Implement rate limiting per user
6. **Input Validation** - All inputs are validated

## Troubleshooting

### Firebase Connection Issues
- Verify firebase-config.json exists and is valid
- Check Firebase project credentials
- Ensure Firebase Admin SDK is properly initialized

### Database Connection Issues
- Verify MySQL is running
- Check database URL and credentials
- Ensure notification_db exists

### Service Discovery Issues
- Ensure Eureka server is running
- Check eureka.client.service-url.defaultZone
- Verify service registration

## Contributing

1. Follow Spring Boot conventions
2. Add unit tests for new features
3. Update API documentation
4. Use meaningful commit messages

## License

Apache 2.0

## Support

For issues and questions, contact: dev@whoami.com
