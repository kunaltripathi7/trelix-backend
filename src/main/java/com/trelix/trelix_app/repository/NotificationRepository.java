package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Notification;
import com.trelix.trelix_app.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByNotifierIdOrderByCreatedAtDesc(UUID notifierId, Pageable pageable);

    Page<Notification> findByNotifierIdAndIsReadOrderByCreatedAtDesc(UUID notifierId, boolean isRead, Pageable pageable);

    Page<Notification> findByNotifierIdAndTypeOrderByCreatedAtDesc(UUID notifierId, NotificationType type, Pageable pageable);

    Page<Notification> findByNotifierIdAndIsReadAndTypeOrderByCreatedAtDesc(UUID notifierId, boolean isRead, NotificationType type, Pageable pageable);

    long countByNotifierIdAndIsRead(UUID notifierId, boolean isRead);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Notification n WHERE n.id = :id AND n.notifierId = :userId")
    boolean isOwner(@Param("id") UUID id, @Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.notifierId = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") UUID userId);
}




