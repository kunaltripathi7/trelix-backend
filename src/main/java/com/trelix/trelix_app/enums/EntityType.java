package com.trelix.trelix_app.enums;

import lombok.Getter;

@Getter
public enum EntityType {
    TASK("tasks"),
    MESSAGE("messages");

    private final String tableName;

    EntityType(String tableName) {
        this.tableName = tableName;
    }

}




