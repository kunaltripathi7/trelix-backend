package com.trelix.trelix_app.service.notification;

import com.trelix.trelix_app.enums.NotificationType;

import java.util.Map;

public interface MessageGenerator {
    NotificationType getType();

    String generate(String actorName, Map<String, String> metadata);
}
