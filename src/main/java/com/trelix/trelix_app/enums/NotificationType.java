package com.trelix.trelix_app.enums;

public enum NotificationType {
    // Task notifications
    TASK_ASSIGNED("You were assigned to a task"),
    TASK_UPDATED("A task you're assigned to was updated"),
    TASK_COMPLETED("A task you're assigned to was completed"),
    TASK_STATUS_CHANGED("Task status changed"),

    // Message notifications
    MESSAGE_MENTION("You were mentioned in a message"),
    MESSAGE_REPLY("Someone replied to your message"),

    // Membership notifications
    PROJECT_INVITE("You were added to a project"),
    CHANNEL_INVITE("You were added to a channel"),
    TEAM_INVITE("You were added to a team"),

    // Event notifications
    EVENT_REMINDER("Upcoming event reminder");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTaskRelated() {
        return this.name().startsWith("TASK_");
    }

    public boolean isMessageRelated() {
        return this.name().startsWith("MESSAGE_");
    }
}




