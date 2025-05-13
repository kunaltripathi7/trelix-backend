package com.trelix.trelix_app.repository;

import com.trelix.trelix_app.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByNotifierIdOrderByCreatedAtDesc(UUID notifierId);

    List<Notification> findByNotifierIdAndIsReadFalseOrderByCreatedAtDesc(UUID notifierId);

}
