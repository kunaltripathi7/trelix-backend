package com.trelix.trelix_app.enums;

public enum EventEntityType {
    TEAM("teams"),
    PROJECT("projects"),
    TASK("tasks");

    private final String tableName;

    EventEntityType(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
