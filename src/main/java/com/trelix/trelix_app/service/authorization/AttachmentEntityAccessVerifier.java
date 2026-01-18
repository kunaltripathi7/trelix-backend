package com.trelix.trelix_app.service.authorization;

import com.trelix.trelix_app.enums.EntityType;

import java.util.UUID;

public interface AttachmentEntityAccessVerifier {
    EntityType getType();

    void verify(UUID entityId, UUID userId);
}
