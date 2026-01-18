package com.trelix.trelix_app.service.notification;

import com.trelix.trelix_app.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TaskCompletedGenerator implements MessageGenerator {

    @Override
    public NotificationType getType() {
        return NotificationType.TASK_COMPLETED;
    }

    @Override
    public String generate(String actorName, Map<String, String> metadata) {
        return actorName + " completed task: " + metadata.getOrDefault("taskTitle", "N/A");
    }
}
