package com.whoami.launch.repository;

import com.whoami.launch.entity.Notification;
import com.whoami.launch.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Notification entity
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    /**
     * Get paginated notifications for a user (excluding deleted)
     */
    Page<Notification> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Get unread notifications for a user
     */
    Page<Notification> findByUserIdAndIsReadFalseAndIsDeletedFalseOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Get notifications by type for a user
     */
    Page<Notification> findByUserIdAndTypeAndIsDeletedFalseOrderByCreatedAtDesc(String userId, NotificationType type, Pageable pageable);

    /**
     * Count unread notifications for a user
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.isRead = false AND n.isDeleted = false")
    Long countUnreadNotifications(@Param("userId") String userId);

    /**
     * Count total notifications for a user (excluding deleted)
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.isDeleted = false")
    Long countTotalNotifications(@Param("userId") String userId);

    /**
     * Get notifications by target
     */
    Page<Notification> findByTargetIdAndTargetTypeAndIsDeletedFalseOrderByCreatedAtDesc(
            String targetId, String targetType, Pageable pageable);

    /**
     * Soft delete a notification
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isDeleted = true WHERE n.notificationId = :notificationId")
    void softDeleteById(@Param("notificationId") String notificationId);

    /**
     * Mark notification as read
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.notificationId = :notificationId")
    void markAsRead(@Param("notificationId") String notificationId);

    /**
     * Mark all notifications as read for a user
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isDeleted = false")
    void markAllAsRead(@Param("userId") String userId);

    /**
     * Get notifications by target ID
     */
    List<Notification> findByTargetIdAndTargetTypeAndIsDeletedFalse(String targetId, String targetType);

    /**
     * Check if a user has unread notifications
     */
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Notification n WHERE n.userId = :userId AND n.isRead = false AND n.isDeleted = false")
    boolean hasUnreadNotifications(@Param("userId") String userId);
}
