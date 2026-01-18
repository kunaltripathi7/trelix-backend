package com.trelix.trelix_app.service.authorization;

import com.trelix.trelix_app.enums.EventEntityType;

import java.util.UUID;

public interface EventEntityAccessVerifier {
    EventEntityType getType();

    void verify(UUID entityId, UUID userId);
}
