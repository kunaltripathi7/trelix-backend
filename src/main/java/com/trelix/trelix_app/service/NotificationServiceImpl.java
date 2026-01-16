package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.request.CreateNotificationRequest;
import com.trelix.trelix_app.dto.response.NotificationResponse;
import com.trelix.trelix_app.dto.response.PagedNotificationResponse;
import com.trelix.trelix_app.entity.Notification;
import com.trelix.trelix_app.entity.User;
import com.trelix.trelix_app.enums.ErrorCode;
import com.trelix.trelix_app.enums.NotificationType;
import com.trelix.trelix_app.exception.ForbiddenException;
import com.trelix.trelix_app.exception.ResourceNotFoundException;
import com.trelix.trelix_app.repository.NotificationRepository;
import com.trelix.trelix_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        User actor = userRepository.findById(request.actorId())
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with ID: " + request.actorId()));

        Notification notification = Notification.builder()
                .notifierId(request.notifierId())
                .actorId(request.actorId())
                .type(request.type())
                .referenceId(request.referenceId())
                .metadata(request.metadata())
                .isRead(false)
                .build();

        notification = notificationRepository.save(notification);
        return toNotificationResponse(notification, actor.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedNotificationResponse getNotifications(UUID userId, Boolean isRead, NotificationType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> notificationPage;

        if (isRead != null && type != null) {
            notificationPage = notificationRepository.findByNotifierIdAndIsReadAndTypeOrderByCreatedAtDesc(userId, isRead, type, pageable);
        } else if (isRead != null) {
            notificationPage = notificationRepository.findByNotifierIdAndIsReadOrderByCreatedAtDesc(userId, isRead, pageable);
        } else if (type != null) {
            notificationPage = notificationRepository.findByNotifierIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
        } else {
            notificationPage = notificationRepository.findByNotifierIdOrderByCreatedAtDesc(userId, pageable);
        }

        List<NotificationResponse> notificationResponses = notificationPage.getContent().stream()
                .map(notification -> {
                    User actor = userRepository.findById(notification.getActorId()).orElse(null);
                    return toNotificationResponse(notification, actor != null ? actor.getName() : "Unknown");
                })
                .collect(Collectors.toList());

        long unreadCount = notificationRepository.countByNotifierIdAndIsRead(userId, false);

        return new PagedNotificationResponse(
                notificationResponses,
                notificationPage.getNumber(),
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements(),
                unreadCount
        );
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(UUID notificationId, UUID requesterId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        verifyOwnership(notification.getId(), requesterId);

        User actor = userRepository.findById(notification.getActorId())
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found for notification"));

        return toNotificationResponse(notification, actor.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByNotifierIdAndIsRead(userId, false);
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        verifyOwnership(notification.getId(), userId);

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void markAsUnread(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        verifyOwnership(notification.getId(), userId);

        if (notification.isRead()) {
            notification.setRead(false);
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        verifyOwnership(notification.getId(), userId);
        notificationRepository.delete(notification);
    }

    private void verifyOwnership(UUID notificationId, UUID userId) {
        if (!notificationRepository.isOwner(notificationId, userId)) {
            throw new ForbiddenException("You can only access your own notifications", ErrorCode.FORBIDDEN);
        }
    }

    private NotificationResponse toNotificationResponse(Notification notification, String actorName) {
        String message = generateMessage(notification.getType(), actorName, notification.getMetadata());
        return new NotificationResponse(
                notification.getId(),
                notification.getNotifierId(),
                notification.getActorId(),
                actorName,
                notification.getType(),
                notification.getReferenceId(),
                message,
                notification.getMetadata(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }

    private String generateMessage(NotificationType type, String actorName, Map<String, String> metadata) {
        Map<String, String> safeMetadata = metadata != null ? metadata : Map.of();

        return switch (type) {
            case TASK_ASSIGNED -> actorName + " assigned you to task: " + safeMetadata.getOrDefault("taskTitle", "N/A");
            case MESSAGE_MENTION -> actorName + " mentioned you in a message";
            case TEAM_INVITE -> actorName + " invited you to team: " + safeMetadata.getOrDefault("teamName", "N/A");
            case TASK_UPDATED -> actorName + " updated task: " + safeMetadata.getOrDefault("taskTitle", "N/A");
            case TASK_COMPLETED -> actorName + " completed task: " + safeMetadata.getOrDefault("taskTitle", "N/A");
            case TASK_STATUS_CHANGED -> actorName + " changed status of task: " + safeMetadata.getOrDefault("taskTitle", "N/A");
            case MESSAGE_REPLY -> actorName + " replied to your message";
            case PROJECT_INVITE -> actorName + " invited you to project: " + safeMetadata.getOrDefault("projectName", "N/A");
            case CHANNEL_INVITE -> actorName + " invited you to channel: " + safeMetadata.getOrDefault("channelName", "N/A");
            case EVENT_REMINDER -> "Reminder: " + safeMetadata.getOrDefault("eventTitle", "N/A");
        };
    }
}




