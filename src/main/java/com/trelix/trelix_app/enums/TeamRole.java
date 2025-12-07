package com.trelix.trelix_app.enums;

public enum TeamRole {
    OWNER,      // Can delete team, transfer ownership
    ADMIN,      // Can manage team, add/remove members, create projects
    MEMBER;     // Can view team, participate in team channels

    public boolean canManageTeam() {
        return this == OWNER || this == ADMIN;
    }

    public boolean canDeleteTeam() {
        return this == OWNER;
    }
}
