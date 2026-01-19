package com.trelix.trelix_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate; // server initiated pushes

    public void broadcastToChannel(UUID channelId, Object payload) {
        String destination = "/topic/channel." + channelId;
        messagingTemplate.convertAndSend(destination, payload);
        log.debug("Broadcast to channel: {}", destination);
    }

    public void broadcastEvent(UUID channelId, String type, Object data) {
        broadcastToChannel(channelId, Map.of("type", type, "data", data));
    }

    public void sendNotificationToUser(UUID userId, Object notification) {
        String destination = "/topic/notifications." + userId;
        messagingTemplate.convertAndSend(destination, notification);
        log.debug("Sent notification to user: {}", userId);
    }
}
