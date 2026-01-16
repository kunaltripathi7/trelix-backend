package com.trelix.trelix_app.enums;

public enum TaskPriority {
    LOW("Low Priority"),
    MEDIUM("Medium Priority"),
    HIGH("High Priority"),
    URGENT("Urgent - Immediate Action Required");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}




