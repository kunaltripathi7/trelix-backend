package com.trelix.trelix_app.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateDirectMessageRequest(
        @NotNull(message = "Other user ID cannot be null")
        UUID otherUserId
) {}
