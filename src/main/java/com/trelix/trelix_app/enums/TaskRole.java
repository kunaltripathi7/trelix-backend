package com.trelix.trelix_app.enums;

public enum TaskRole {
    ASSIGNEE,   // Responsible for completing the task
    REVIEWER;   // Reviews the task, can approve/request changes

    public boolean canEditTask() {
        return this == ASSIGNEE;
    }

    public boolean canReview() {
        return this == REVIEWER;
    }
}
