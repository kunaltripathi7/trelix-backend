package com.trelix.trelix_app.service.notification;

import com.trelix.trelix_app.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProjectInviteGenerator implements MessageGenerator {

    @Override
    public NotificationType getType() {
        return NotificationType.PROJECT_INVITE;
    }

    @Override
    public String generate(String actorName, Map<String, String> metadata) {
        return actorName + " invited you to project: " + metadata.getOrDefault("projectName", "N/A");
    }
}
