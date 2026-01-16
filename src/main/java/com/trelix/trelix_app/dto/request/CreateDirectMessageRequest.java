package com.trelix.trelix_app.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateDirectMessageRequest(
        @NotNull(message = "Other user ID cannot be null")
        UUID otherUserId
) {}




