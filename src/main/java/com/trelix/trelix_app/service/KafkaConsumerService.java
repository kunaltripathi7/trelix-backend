package com.trelix.trelix_app.service;

import com.trelix.trelix_app.dto.common.NotificationEvent;
import com.trelix.trelix_app.dto.response.NotificationResponse;
import com.trelix.trelix_app.entity.Notification;
import com.trelix.trelix_app.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final NotificationRepository notificationRepository;
    private final WebSocketService webSocketService;

    @RetryableTopic(attempts = "4", backoff = @Backoff(delay = 1000, multiplier = 2), dltStrategy = DltStrategy.FAIL_ON_ERROR, include = {
            Exception.class }) // retry on any Exception
    @KafkaListener(topics = "trelix-notifications", groupId = "trelix-group")
    public void consume(NotificationEvent event) {
        log.info("Received notification event: {}", event);

        Notification notification = Notification.builder()
                .notifierId(event.recipientId())
                .actorId(event.actorId())
                .type(event.type())
                .referenceId(event.relatedEntityId())
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Saved notification to database: {}", savedNotification.getId());

        NotificationResponse response = new NotificationResponse(
                savedNotification.getId(),
                savedNotification.getNotifierId(),
                savedNotification.getActorId(),
                null,
                savedNotification.getType(),
                savedNotification.getReferenceId(),
                event.message(),
                savedNotification.getMetadata(),
                savedNotification.isRead(),
                savedNotification.getCreatedAt());
        webSocketService.sendNotificationToUser(event.recipientId(), response);
        log.info("Pushed notification to user via WebSocket: {}", event.recipientId());
    }

    @DltHandler
    public void handleDlt(NotificationEvent event) {
        log.error("DLT: Message failed after all retries. Event: {}", event);
        // prod -> send an alert to admin / persist in Db as well.
    }
}
