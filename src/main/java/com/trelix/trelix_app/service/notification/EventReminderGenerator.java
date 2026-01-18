package com.trelix.trelix_app.service.notification;

import com.trelix.trelix_app.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventReminderGenerator implements MessageGenerator {

    @Override
    public NotificationType getType() {
        return NotificationType.EVENT_REMINDER;
    }

    @Override
    public String generate(String actorName, Map<String, String> metadata) {
        return "Reminder: " + metadata.getOrDefault("eventTitle", "N/A");
    }
}
