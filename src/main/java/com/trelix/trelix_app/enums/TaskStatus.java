package com.trelix.trelix_app.enums;

public enum TaskStatus {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    IN_REVIEW("In Review"),
    DONE("Done"),
    CANCELLED("Cancelled");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCompleted() {
        return this == DONE || this == CANCELLED;
    }

    public boolean isActive() {
        return this == TODO || this == IN_PROGRESS || this == IN_REVIEW;
    }
}




