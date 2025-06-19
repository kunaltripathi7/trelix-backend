package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.NotificationDTO;
import com.trelix.trelix_app.entity.Notification;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.NotificationRepository;
import com.trelix.trelix_app.util.AppMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.trelix.trelix_app.util.AppMapper.convertToNotificationDTO;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private NotificationRepository notificationRepository;

    public List<NotificationDTO> getAllNotifcations(UUID userId) {
        return notificationRepository.findByNotifierIdOrderByCreatedAtDesc(userId).stream().map(AppMapper::convertToNotificationDTO).toList();
    }

    public List<NotificationDTO> getAllUnreadNotifications(UUID userId) {
        return notificationRepository.findByNotifierIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream().map(AppMapper::convertToNotificationDTO).toList();
    }

    public void updateNotificationRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found."));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void updateAllNotificationsRead(UUID userId) {
        List<Notification> notifications = notificationRepository.findByNotifierIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        notifications.forEach(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }



}
