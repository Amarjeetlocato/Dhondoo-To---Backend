package com.whoami.launch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.whoami.launch.entity.Notification;
import com.whoami.launch.enums.NotificationType;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(
            String userId,
            Pageable pageable);

    Page<Notification> findByUserIdAndIsReadFalseAndIsDeletedFalseOrderByCreatedAtDesc(
            String userId,
            Pageable pageable);

    Page<Notification> findByUserIdAndTypeAndIsDeletedFalseOrderByCreatedAtDesc(
            String userId,
            NotificationType type,
            Pageable pageable);

    @Query("""
            SELECT COUNT(n)
            FROM Notification n
            WHERE n.userId = :userId
            AND n.isRead = false
            AND n.isDeleted = false
            """)
    Long countUnreadNotifications(
            @Param("userId") String userId);

    @Query("""
            SELECT COUNT(n)
            FROM Notification n
            WHERE n.userId = :userId
            AND n.isDeleted = false
            """)
    Long countTotalNotifications(
            @Param("userId") String userId);

    Page<Notification> findByTargetIdAndTargetTypeAndIsDeletedFalseOrderByCreatedAtDesc(
            String targetId,
            String targetType,
            Pageable pageable);

    Optional<Notification> findByNotificationId(
            String notificationId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.isDeleted = true
            WHERE n.notificationId = :notificationId
            """)
    void softDeleteById(
            @Param("notificationId") String notificationId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.isRead = true
            WHERE n.notificationId = :notificationId
            """)
    void markAsRead(
            @Param("notificationId") String notificationId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Notification n
            SET n.isRead = true
            WHERE n.userId = :userId
            AND n.isDeleted = false
            """)
    void markAllAsRead(
            @Param("userId") String userId);

    List<Notification>
    findByTargetIdAndTargetTypeAndIsDeletedFalse(
            String targetId,
            String targetType);

    @Query("""
            SELECT CASE
                WHEN COUNT(n) > 0
                THEN true
                ELSE false
            END
            FROM Notification n
            WHERE n.userId = :userId
            AND n.isRead = false
            AND n.isDeleted = false
            """)
    boolean hasUnreadNotifications(
            @Param("userId") String userId);
}