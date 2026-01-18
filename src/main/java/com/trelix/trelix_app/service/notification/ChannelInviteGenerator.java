package com.trelix.trelix_app.service.notification;

import com.trelix.trelix_app.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChannelInviteGenerator implements MessageGenerator {

    @Override
    public NotificationType getType() {
        return NotificationType.CHANNEL_INVITE;
    }

    @Override
    public String generate(String actorName, Map<String, String> metadata) {
        return actorName + " invited you to channel: " + metadata.getOrDefault("channelName", "N/A");
    }
}
