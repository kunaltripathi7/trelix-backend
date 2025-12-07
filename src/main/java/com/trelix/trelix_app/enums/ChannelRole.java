package com.trelix.trelix_app.enums;

public enum ChannelRole {
    OWNER,      // Can delete channel, manage all members
    MEMBER;     // Can send messages

    public boolean canManageChannel() {
        return this == OWNER;
    }
}
