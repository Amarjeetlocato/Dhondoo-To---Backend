package com.whoami.launch.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locato.dto.ProductCreatedEvent;
import com.locato.dto.ProductDeletedEvent;
import com.locato.dto.ProductUpdatedEvent;
import com.locato.dto.ReelCreatedEvent;
import com.locato.dto.ReelDeletedEvent;
import com.locato.dto.ReelUpdatedEvent;
import com.locato.dto.ServiceCreatedEvent;
import com.locato.dto.ServiceDeletedEvent;
import com.locato.dto.ServiceUpdatedEvent;
import com.locato.dto.ShopCreatedEvent;
import com.locato.dto.ShopDeletedEvent;
import com.locato.dto.ShopStatusChangedEvent;
import com.locato.dto.ShopUpdatedEvent;
import com.whoami.launch.dto.NotificationRequest;
import com.whoami.launch.dto.NotificationResponse;
import com.whoami.launch.dto.UnreadCountResponse;
import com.whoami.launch.entity.Notification;
import com.whoami.launch.enums.NotificationType;
import com.whoami.launch.repository.NotificationRepository;

import lombok.extern.slf4j.Slf4j;
/**
 * Service for managing notifications
 */
@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final Optional<FCMService> fcmService;
    private final NotificationTemplateService templateService;

    public NotificationService(NotificationRepository notificationRepository, Optional<FCMService> fcmService,NotificationTemplateService templateService) {
        this.notificationRepository = notificationRepository;
        this.fcmService = fcmService;
        this.templateService = templateService;
    }

    /**
     * Create a new notification
     */
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        log.info("Creating notification for user: {}", request.getUserId());

        // Generate unique notification ID
        String notificationId = UUID.randomUUID().toString();
        String actionsJson =
                templateService.buildActions(request.getType());

        String deepLink =
                templateService.buildDeepLink(
                        request.getType(),
                        request.getTargetId()
                );


        // Create notification entity
        Notification notification = Notification.builder()
                .notificationId(notificationId)
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .imageUrl(request.getImageUrl())
                .targetId(request.getTargetId())
                .targetType(request.getTargetType())
                .actionsJson(actionsJson)
                .deepLink(deepLink)
                .metadataJson(
                        request.getMetadataJson() != null
                                ? request.getMetadataJson()
                                : "{}"
                )
                .type(request.getType())
                .isRead(false)
                .isDeleted(false)
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created successfully with ID: {}", notificationId);

        // Convert to response DTO
        NotificationResponse response = mapToResponse(savedNotification);

        // Send FCM push notification asynchronously if FCM is configured
        if (request.getSendPush() != null && request.getSendPush()) {
            if (fcmService.isPresent()) {
                fcmService.get().sendNotificationWithRetry(request.getUserId(), response, 3);
            } else {
                log.warn("FCM service not available, skipping push notification for user: {}", request.getUserId());
            }
        }

        return response;
    }

    
    /**
     * Get paginated notifications for a user
     */
    public Page<NotificationResponse> getNotifications(String userId, Pageable pageable) {
        log.info("Fetching notifications for user: {} with pagination", userId);
        return notificationRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get unread notifications for a user
     */
    public Page<NotificationResponse> getUnreadNotifications(String userId, Pageable pageable) {
        log.info("Fetching unread notifications for user: {}", userId);
        return notificationRepository
                .findByUserIdAndIsReadFalseAndIsDeletedFalseOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get notifications by type for a user
     */
    public Page<NotificationResponse> getNotificationsByType(String userId, String type, Pageable pageable) {
        log.info("Fetching notifications for user: {} with type: {}", userId, type);
        return notificationRepository
                .findByUserIdAndTypeAndIsDeletedFalseOrderByCreatedAtDesc(
                        userId,
                        Enum.valueOf(com.whoami.launch.enums.NotificationType.class, type),
                        pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get unread count and total count for a user
     */
    public UnreadCountResponse getUnreadCount(String userId) {
        log.info("Fetching unread count for user: {}", userId);
        Long unreadCount = notificationRepository.countUnreadNotifications(userId);
        Long totalCount = notificationRepository.countTotalNotifications(userId);

        return UnreadCountResponse.builder()
                .userId(userId)
                .unreadCount(unreadCount)
                .totalCount(totalCount)
                .build();
    }

    /**
     * Mark notification as read
     */
    @Transactional
    public NotificationResponse markAsRead(String notificationId) {
        log.info("Marking notification as read: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        notificationRepository.markAsRead(notificationId);
        notification.setIsRead(true);

        return mapToResponse(notification);
    }

    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public void markAllAsRead(String userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        notificationRepository.markAllAsRead(userId);
    }

    /**
     * Delete a notification (soft delete)
     */
    @Transactional
    public void deleteNotification(String notificationId) {
        log.info("Deleting notification: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        notificationRepository.softDeleteById(notificationId);
    }

    /**
     * Get a notification by ID
     */
    public NotificationResponse getNotificationById(String notificationId) {
        log.info("Fetching notification: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        return mapToResponse(notification);
    }

    /**
     * Check if user has unread notifications
     */
    public boolean hasUnreadNotifications(String userId) {
        return notificationRepository.hasUnreadNotifications(userId);
    }

    /**
     * Map Notification entity to NotificationResponse DTO
     */
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .imageUrl(notification.getImageUrl())
                .targetId(notification.getTargetId())
                .targetType(notification.getTargetType())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .metadataJson(notification.getMetadataJson())
                .actionsJson(notification.getActionsJson())
                .deepLink(notification.getDeepLink())
                .isDeleted(notification.getIsDeleted())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
    
    public void handleShopCreated(ShopCreatedEvent event) {
    	
    	String metadataJson = """
    			{
    			  "shopName":"%s",
    			  "logoUrl":"%s",
    			  "bannerUrl":"%s",
    			  "eventType":"%s",
    			  "eventId":"%s"
    			}
    			""".formatted(
    			        event.getShopName(),
    			        event.getLogoUrl(),
    			        event.getBannerUrl(),
    			        event.getEventType(),
    			        event.getEventId()
    			);

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Shop Created")
                        .message("Your shop '" +
                                event.getShopName() +
                                "' has been created successfully.")
                        .targetId(event.getShopId())
                        .targetType("SHOP")
                        .imageUrl(event.getLogoUrl())
                        .metadataJson(metadataJson)
                        .type(NotificationType.SHOP)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    
    public void handleShopUpdated(ShopUpdatedEvent event) {
    	
    	String metadataJson = """
    			{
    			  "shopName":"%s",
    			  "logoUrl":"%s",
    			  "bannerUrl":"%s",
    			  "changes":"%s"
    			}
    			""".formatted(
    			        event.getShopName(),
    			        event.getLogoUrl(),
    			        event.getBannerUrl(),
    			        event.getChanges()
    			);

        NotificationRequest request =
        		NotificationRequest.builder()
        	    .userId(event.getUserId())
        	    .title("Shop Updated")
        	    .message("Your shop details have been updated successfully.")
        	    .targetId(event.getShopId())
        	    .targetType("SHOP")
        	    .imageUrl(event.getLogoUrl())
        	    .metadataJson(metadataJson)
        	    .type(NotificationType.SHOP)
        	    .sendPush(true)
        	    .build();
        createNotification(request);
    }
    
    
    
    public void handleShopStatusChanged(com.locato.dto.ShopStatusChangedEvent e) {
    	
    	
    	String metadataJson = """
    			{
    			  "shopName":"%s",
    			  "logoUrl":"%s",
    			  "bannerUrl":"%s",
    			  "status":"%s"
    			}
    			""".formatted(
    			        e.getShopName(),
    			        e.getLogoUrl(),
    			        e.getBannerUrl(),
    			        e.getShopStatus()
    			);

        String title;
        String message;

        switch (e.getShopStatus()) {

            case OPEN:
                title = "Shop Opened";
                message = "Your shop is now open and accepting orders.";
                break;

            case CLOSED:
                title = "Shop Closed";
                message = "Your shop is currently closed.";
                break;

            case AUTO:
                title = "Auto Mode Enabled";
                message = "Your shop is now running in auto mode.";
                break;

            default:
                title = "Shop Status Updated";
                message = "Your shop status has changed.";
        }

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(e.getUserId())
                        .title(title)
                        .message(message)
                        .targetId(e.getShopId())
                        .targetType("SHOP")
                        .imageUrl(e.getLogoUrl())
                        .metadataJson(metadataJson)
                        .type(NotificationType.SHOP)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }

	public void handleShopDeleted(ShopDeletedEvent e) {
		
		String metadataJson = """
				{
				  "shopName":"%s",
				  "logoUrl":"%s",
				  "bannerUrl":"%s"
				}
				""".formatted(
				        e.getShopName(),
				        e.getLogoUrl(),
				        e.getBannerUrl()
				);
		NotificationRequest request =
                NotificationRequest.builder()
                        .userId(e.getUserId())
                        .title("Shop Deleted")
                        .message("Your shop '" +
                                e.getShopName() +
                                "' has been deleted.")
                        .targetId(e.getShopId())
                        .targetType("SHOP")
                        .imageUrl(e.getLogoUrl())
                        .metadataJson(metadataJson)
                        .type(NotificationType.SHOP)
                        .sendPush(true)
                        .build();

        createNotification(request);// TODO Auto-generated method stub
		
	}
	
	public void handleProductCreated(ProductCreatedEvent event) {

	    String metadataJson = """
	    {
	      "productImage":"%s",
	      "shopLogo":"%s",
	      "shopBanner":"%s",
	      "shopName":"%s"
	    }
	    """.formatted(
	            event.getProductImages(),
	            event.getShopLogo(),
	            event.getShopBanner(),
	            event.getShopName()
	    );

	    NotificationRequest request =
	            NotificationRequest.builder()
	                    .userId(event.getUserId())
	                    .title("Product Added")
	                    .message("Product '" +
	                            event.getProductName() +
	                            "' has been added successfully.")
	                    .imageUrl(event.getShopLogo()) // main image
	                    .metadataJson(metadataJson)
	                    .targetId(event.getProductId())
	                    .targetType("PRODUCT")
	                    .type(NotificationType.PRODUCT)
	                    .sendPush(true)
	                    .build();

	    createNotification(request);
	}
	    /*
	     * Later:
	     * notifyFollowers(event);
	     */
	
	public void handleProductUpdated(ProductUpdatedEvent event) {
		
		String metadataJson = """
				{
				  "productImage":"%s",
				  "shopLogo":"%s",
				  "shopBanner":"%s",
				  "shopName":"%s",
				  "changes":"%s"
				}
				""".formatted(
				        event.getProductImages(),
				        event.getShopLogo(),
				        event.getShopBanner(),
				        event.getShopName(),
				        event.getChanges()
				);

	    NotificationRequest request =
	            NotificationRequest.builder()
	                    .userId(event.getUserId())
	                    .title("Product Updated")
	                    .message("Product '" +
	                            event.getProductName() +
	                            "' updated successfully. "
	                            + event.getChanges())
	                    .targetId(event.getProductId())
	                    .imageUrl(event.getShopLogo())
	                    .metadataJson(metadataJson)
	                    .targetType("PRODUCT")
	                    .type(NotificationType.PRODUCT)
	                    .sendPush(true)
	                    .build();

	    createNotification(request);
	}
	
	public void handleProductDeleted(ProductDeletedEvent event) {
		
		String metadataJson = """
				{
				  "productImage":"%s",
				  "shopLogo":"%s",
				  "shopBanner":"%s",
				  "shopName":"%s"
				}
				""".formatted(
				        event.getProductImages(),
				        event.getShopLogo(),
				        event.getShopBanner(),
				        event.getShopName()
				);

	    NotificationRequest request =
	            NotificationRequest.builder()
	                    .userId(event.getUserId())
	                    .title("Product Deleted")
	                    .message("Product '" +
	                            event.getProductName() +
	                            "' has been deleted.")
	                    .targetId(event.getProductId())
	                    .imageUrl(event.getShopLogo())
	                    .metadataJson(metadataJson)
	                    .targetType("PRODUCT")
	                    .type(NotificationType.PRODUCT)
	                    .sendPush(true)
	                    .build();

	    createNotification(request);
	}
	
	public void handleServiceCreated(ServiceCreatedEvent event) {
		
		String metadataJson = """
				{
				  "serviceThumbnail":"%s",
				  "shopLogo":"%s",
				  "shopBanner":"%s",
				  "shopName":"%s"
				}
				""".formatted(
				        event.getServiceThumbnail(),
				        event.getShopLogo(),
				        event.getShopBanner(),
				        event.getShopName()
				);

	    NotificationRequest request =
	            NotificationRequest.builder()
	                    .userId(event.getUserId())
	                    .title("Service Created")
	                    .message("Service '" +
	                            event.getServiceName() +
	                            "' has been created successfully.")
	                    .targetId(event.getServiceId())
	                    .imageUrl(event.getServiceThumbnail())
	                    .metadataJson(metadataJson)
	                    .targetType("SERVICE")
	                    .type(NotificationType.SERVICE)
	                    .sendPush(true)
	                    .build();

	    createNotification(request);

	    /*
	     * Later:
	     * Notify followers
	     */
	}
	
	public void handleServiceUpdated(ServiceUpdatedEvent event) {
		String metadataJson = """
				{
				  "serviceThumbnail":"%s",
				  "shopLogo":"%s",
				  "shopBanner":"%s",
				  "shopName":"%s",
				  "changes":"%s"
				}
				""".formatted(
				        event.getServiceThumbnail(),
				        event.getLogoUrl(),
				        event.getBannerUrl(),
				        event.getShopName(),
				        event.getChanges()
				);

	    NotificationRequest request =
	            NotificationRequest.builder()
	                    .userId(event.getUserId())
	                    .title("Service Updated")
	                    .message("Service '" +
	                            event.getServiceName() +
	                            "' updated successfully. "
	                            + event.getChanges())
	                    .targetId(event.getServiceId())
	                    .imageUrl(event.getServiceThumbnail())
	                    .metadataJson(metadataJson)
	                    .targetType("SERVICE")
	                    .type(NotificationType.SERVICE)
	                    .sendPush(true)
	                    .build();

	    createNotification(request);
	}
	
	public void handleServiceDeleted(ServiceDeletedEvent event) {
		
		String metadataJson = """
				{
				  "serviceThumbnail":"%s",
				  "shopLogo":"%s",
				  "shopBanner":"%s",
				  "shopName":"%s"
				}
				""".formatted(
				        event.getServiceThumbnail(),
				        event.getShopLogo(),
				        event.getShopBanner(),
				        event.getShopName()
				);

	    NotificationRequest request =
	            NotificationRequest.builder()
	                    .userId(event.getUserId())
	                    .title("Service Deleted")
	                    .message("Service '" +
	                            event.getServiceName() +
	                            "' has been deleted.")
	                    .targetId(event.getServiceId())
	                    .targetType("SERVICE")
	                    .imageUrl(event.getServiceThumbnail())
	                    .metadataJson(metadataJson)
	                    .type(NotificationType.SERVICE)
	                    .sendPush(true)
	                    .build();

	    createNotification(request);
	}
	
	public void handleReelCreated(ReelCreatedEvent event) {
		String metadataJson = """
				{
				  "reelThumbnail":"%s",
				  "shopLogo":"%s",
				  "shopBanner":"%s",
				  "shopName":"%s"
				}
				""".formatted(
				        event.getReelThumbnail(),
				        event.getShopLogo(),
				        event.getShopBanner(),
				        event.getShopName()
				);

	    NotificationRequest request =
	            NotificationRequest.builder()
	                    .userId(event.getUserId())
	                    .title("Reel Created")
	                    .message("A new reel has been uploaded successfully.")
	                    .targetId(event.getReelId())
	                    .targetType("REEL")
	                    .imageUrl(event.getReelThumbnail())
	                    .metadataJson(metadataJson)
	                    .type(NotificationType.REEL)
	                    .sendPush(true)
	                    .build();

	    createNotification(request);

	    /*
	     * Later:
	     * Notify followers here
	     */
	}
	
	public void handleReelUpdated(ReelUpdatedEvent event) {
		
		String metadataJson = """
				{
				  "reelThumbnail":"%s",
				  "shopLogo":"%s",
				  "shopBanner":"%s",
				  "shopName":"%s",
				  "changes":"%s"
				}
				""".formatted(
				        event.getReelThumbnail(),
				        event.getShopLogo(),
				        event.getShopBanner(),
				        event.getShopName(),
				        event.getChanges()
				);

	    NotificationRequest request =
	    		NotificationRequest.builder()
	    	    .userId(event.getUserId())
	    	    .title("Reel Updated")
	    	    .message(event.getChanges())
	    	    .targetId(event.getReelId())
	    	    .targetType("REEL")
	    	    .imageUrl(event.getReelThumbnail())
	    	    .metadataJson(metadataJson)
	    	    .type(NotificationType.REEL)
	    	    .sendPush(true)
	    	    .build();
	    createNotification(request);
	}
	
	public void handleReelDeleted(ReelDeletedEvent event) {
		String metadataJson = """
				{
				  "reelThumbnail":"%s",
				  "shopLogo":"%s",
				  "shopBanner":"%s",
				  "shopName":"%s"
				}
				""".formatted(
				        event.getReelThumbnail(),
				        event.getShopLogo(),
				        event.getShopBanner(),
				        event.getShopName()
				);

	    NotificationRequest request =
	            NotificationRequest.builder()
	                    .userId(event.getUserId())
	                    .title("Reel Deleted")
	                    .message("A reel has been removed successfully.")
	                    .targetId(event.getReelId())
	                    .targetType("REEL")
	                    .type(NotificationType.REEL)
	                    .imageUrl(event.getReelThumbnail())
	                    .metadataJson(metadataJson)
	                    .sendPush(true)
	                    .build();

	    createNotification(request);
	}
}
