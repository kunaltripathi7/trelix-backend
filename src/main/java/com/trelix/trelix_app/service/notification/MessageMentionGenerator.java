package com.trelix.trelix_app.service.notification;

import com.trelix.trelix_app.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageMentionGenerator implements MessageGenerator {

    @Override
    public NotificationType getType() {
        return NotificationType.MESSAGE_MENTION;
    }

    @Override
    public String generate(String actorName, Map<String, String> metadata) {
        return actorName + " mentioned you in a message";
    }
}
