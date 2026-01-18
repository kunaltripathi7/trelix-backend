package com.trelix.trelix_app.service.notification;

import com.trelix.trelix_app.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TeamInviteGenerator implements MessageGenerator {

    @Override
    public NotificationType getType() {
        return NotificationType.TEAM_INVITE;
    }

    @Override
    public String generate(String actorName, Map<String, String> metadata) {
        return actorName + " invited you to team: " + metadata.getOrDefault("teamName", "N/A");
    }
}
