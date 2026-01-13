package com.trelix.trelix_app.controller;

import com.trelix.trelix_app.dto.SendMessageRequest;
import com.trelix.trelix_app.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final MessageService messageService;

    @MessageMapping("/chat.{channelId}")
    public void handleChatMessage(
            @DestinationVariable UUID channelId,
            @Payload Map<String, Object> payload) {

        log.info("WebSocket message received for channel: {}", channelId);

        UUID senderId = UUID.fromString((String) payload.get("senderId"));
        String content = (String) payload.get("content");
        UUID payloadChannelId = UUID.fromString((String) payload.get("channelId"));

        SendMessageRequest request = new SendMessageRequest(payloadChannelId, content);

        messageService.sendMessage(request, senderId);

        log.info("Message processed for channel: {}", channelId);
    }
}
