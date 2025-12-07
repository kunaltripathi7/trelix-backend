package com.trelix.trelix_app.enums;

public enum ProjectRole {
    ADMIN,      // Can manage project, add/remove members, delete project
    MEMBER;     // Can view project, work on tasks

    public boolean canManageProject() {
        return this == ADMIN;
    }
}
